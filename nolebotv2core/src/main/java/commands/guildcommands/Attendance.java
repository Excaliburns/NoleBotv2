package commands.guildcommands;

import commands.util.CommandEvent;
import commands.util.ReactionCommand;
import enums.EmojiCodes;
import enums.PropEnum;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.NoleBotUtil;
import util.PropertiesUtil;
import util.chat.EmbedHelper;
import util.db.entities.AttendanceEntity;
import util.db.statements.AttendanceStatements;
import util.reactions.ReactionMessage;
import util.reactions.ReactionMessageCache;
import util.reactions.ReactionMessageType;
import util.settings.Settings;
import util.settings.SettingsCache;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This command is used to take attendance in a guild at a given time.
 */
public class Attendance extends ReactionCommand {
    // Todo: lang
    final String improperlyFormattedError =
            "Your message is improperly formatted. Use !help attendance to see more detailed usage information.";
    final String didNotSpecifyTimer =
            "You didn't specify minutes or seconds. Please use 'm' or 's' to denote the time scale you would like.";

    private static final Logger logger = LogManager.getLogger(Attendance.class);
    private final AttendanceStatements statements = new AttendanceStatements();

    private final ConcurrentHashMap<String, Duration> timeLeft = new ConcurrentHashMap<>();
    private final HashMap<String, List<Member>> countedMembers = new HashMap<>();
    private final HashMap<String, String> attendanceMessageCache = new HashMap<>();


    /**
     * Constructor.
     */
    public Attendance() {
        name = "attendance";
        description = "Use this command to start/stop the attendance reaction message, or set the timer. " +
                      "Should only be used once per 'meeting'";
        helpDescription = "Starts/Stops attendance reaction message, or sets the timer.";
        requiredPermissionLevel = 1000;
        usages.add("attendance start");
        usages.add("attendance timer [time][s/m]");
        usages.add("attendance stop");
    }

    @Override
    public void onCommandReceived(CommandEvent event) {
        final List<String> messages = event.getMessageContent();
        final Settings guildSettings = event.getSettings();

        if (messages.size() < 1) {
            event.sendErrorResponseToOriginatingChannel(
                    "Please start, stop, or modify the timer. Use !help attendance"
            );
        }

        final String command = messages.get(1);

        switch (command) {
            case "start" -> {
                if (!attendanceMessageCache.containsKey(event.getGuild().getId())) {
                    handleStartAttendance(event);
                }
                else {
                    event.sendErrorResponseToOriginatingChannel(
                            "Can not start two attendance counters at the " + "same time in one guild"
                    );
                }
            }
            case "timer" -> saveTimer(messages, event, guildSettings);
            case "stop" -> {
                if (attendanceMessageCache.containsKey(event.getGuild().getId())) {
                    timeLeft.put(event.getGuild().getId(), Duration.ZERO);
                }
                else {
                    event.sendErrorResponseToOriginatingChannel(
                            "Attendance is not currently being taken."
                    );
                }
            }
            default -> event.sendErrorResponseToOriginatingChannel("Unknown usage.");
        }
    }

    @Override
    public void handleReaction(
            final GuildMessageReactionAddEvent event,
            final ReactionMessage message,
            final Message retrievedDiscordMessage
    ) {
        final Member originalMember = event.getGuild().getMemberById(message.getUserInitiatedId());
        if (originalMember == null) {
            event.getUser()
                    .openPrivateChannel()
                    .queue(callback -> callback.sendMessage("It seems like the person who started attendance " +
                            "has left all available guilds? Weird! Get them to join back... Or something."
                    ).queue());
            return;
        }

        final GuildVoiceState originalMemberVoiceState = originalMember.getVoiceState();
        if (originalMemberVoiceState == null || !originalMember.getVoiceState().inVoiceChannel()) {
            event.getUser()
                    .openPrivateChannel()
                    .queue(callback -> callback.sendMessage(
                            "It seems like the person who is taking attendance is currently not in a voice channel. " +
                                 "Please tell them to join one so we can verify that you are in the meeting with them!"
                    ).queue());
            return;
        }

        final VoiceChannel originalUserVoiceChannel = originalMember.getVoiceState().getChannel();
        final GuildVoiceState userVoiceState = event.getMember().getVoiceState();
        final boolean leaderMustBeInChannel = Boolean.parseBoolean(PropertiesUtil.getProperty(PropEnum.ATTENDANCE_LEADER_IN_CHANNEL));
        if (leaderMustBeInChannel && (Objects.isNull(userVoiceState) || originalUserVoiceChannel != userVoiceState.getChannel())) {
            event.getUser()
                    .openPrivateChannel()
                    .queue(callback -> callback.sendMessage(
                            "It seems that you aren't in the same voice channel as the person taking attendance. " +
                                    "Make sure you join their voice channel in order to be counted!"
                    ).queue());
            return;
        }

        if (event.getReactionEmote().getEmoji().equals(EmojiCodes.CHECK_MARK.unicodeValue)) {
            final List<Member> memberList = countedMembers.containsKey(event.getGuild().getId())
                    ? countedMembers.get(event.getGuild().getId())
                    : new ArrayList<>();

            if (!memberList.contains(event.getMember())) {
                memberList.add(event.getMember());
            }

            countedMembers.put(event.getGuild().getId(), memberList);
        }
    }

    private void handleStartAttendance(CommandEvent event) {
        final Guild guild = event.getGuild();
        final String guildId = guild.getId();
        final MessageChannel channel = event.getChannel();
        final String authorId = event.getOriginatingJDAEvent().getAuthor().getId();
        final String messageId = event.getOriginatingJDAEvent().getMessageId();

        timeLeft.put(guildId, event.getSettings().getAttendanceTimer());

        final ReactionMessage message = getReactionMessageForAttendance(guild, channel, authorId, messageId);

        event.getChannel().sendMessageEmbeds(message.getEmbedList().get(0)).queue(sentMessage -> {
            sentMessage.addReaction(EmojiCodes.CHECK_MARK.unicodeValue).queue();
            ReactionMessageCache.setReactionMessage(sentMessage.getId(), message);
            attendanceMessageCache.put(guildId, sentMessage.getId());

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateAttendancePage(guild, channel, authorId, messageId);

                    if (timeLeft.get(guildId).toSeconds() <= 0) {
                        timer.cancel();
                        EmbedBuilder finalEmbed = EmbedHelper.getDefaultEmbedBuilder();

                        try {
                            if (insertAttendanceList(guild)) {
                                finalEmbed.addField(
                                        "Final count! These names have also been added to the database",
                                        "",
                                        false
                                );
                            }
                        }
                        catch (SQLException e) {
                            logger.error(e);
                            channel.sendMessage("Couldn't update the database!").queue();
                            channel.sendMessageEmbeds(EmbedHelper.getDefaultExceptionReactionMessage(e)).queue();

                            finalEmbed.addField(
                                    "Final count! As there was an error, " +
                                          "these names have not been added to the database",
                                    "",
                                    false
                            );
                        }
                        finally {
                            //noinspection ConstantConditions
                            finalEmbed = addAttendanceToMessageEmbed(getAttendanceAsEmbedFieldList(guild), finalEmbed);

                            channel.sendMessageEmbeds(finalEmbed.build()).queue();
                            timeLeft.remove(guildId);
                            countedMembers.remove(guildId);
                            attendanceMessageCache.remove(guildId);
                        }
                    }
                }
            }, 0, Duration.ofSeconds(2).toMillis());
        });
    }

    /**
     * Helper method that is called every 30 seconds to update the attendance page.
     *
     * @param guild Guild that this operation is in
     * @param channel Channel that the operation is in
     * @param authorId Original author of the attendance message.
     * @param messageId Message id of the attendance method.
     */
    private void updateAttendancePage(
            final Guild guild,
            final MessageChannel channel,
            final String authorId,
            final String messageId
    ) {
        Duration newTimeLeft = timeLeft.get(guild.getId()).minus(Duration.ofSeconds(2));
        timeLeft.put(guild.getId(), newTimeLeft);

        final ReactionMessage newReactionMessage = getReactionMessageForAttendance(guild, channel, authorId, messageId);
        final MessageEmbed attendanceEmbed = newReactionMessage.getEmbedList().get(0);

        channel.editMessageEmbedsById(attendanceMessageCache.get(guild.getId()), attendanceEmbed).queue(sentMessage -> {
            ReactionMessageCache.setReactionMessage(sentMessage.getId(), newReactionMessage);
            if (timeLeft.containsKey(guild.getId()) && !(timeLeft.get(guild.getId()).toSeconds() <= 0)) {
                attendanceMessageCache.put(guild.getId(), sentMessage.getId());
            }
        });
    }

    /**
     * Builds the message embed for the attendance command.
     *
     * @param guild Guild that the command was run in
     * @param channel Channel that the command was run in.
     * @param authorID The author of the attendance command.
     * @param messageId The messageId of the attendance command.
     * @return A build MessageEmbed for attendance.
     */
    private ReactionMessage getReactionMessageForAttendance(
            final Guild guild,
            final MessageChannel channel,
            final String authorID,
            final String messageId
    ) {
        final Duration attendanceTimer = timeLeft.get(guild.getId());
        List<String> countedMemberList = getAttendanceAsEmbedFieldList(guild);

        EmbedBuilder attendancePage = EmbedHelper.getDefaultEmbedBuilder().addField(
                "Click the check mark to be added to the attendance!",
                "",
                false
        );
        //noinspection ConstantConditions
        attendancePage = addAttendanceToMessageEmbed(countedMemberList, attendancePage);

        if (!attendanceTimer.isZero() && !attendanceTimer.isNegative()) {
            attendancePage.addField(
                    "Time Left",
                    NoleBotUtil.getFormattedDurationString(attendanceTimer),
                    false
            );
        }
        else {
            attendancePage.addField("Times up!", "", false);
        }

        final List<EmojiCodes> attendanceReaction = Collections.singletonList(EmojiCodes.CHECK_MARK);
        final List<MessageEmbed> pages = Collections.singletonList(attendancePage.build());

        return new ReactionMessage(
                ReactionMessageType.ATTENDANCE_COMMAND,
                channel,
                authorID,
                messageId,
                0,
                pages,
                attendanceReaction
        );
    }

    private List<String> getAttendanceAsEmbedFieldList(final Guild guild) {
        final List<String> countedMemberList = new ArrayList<>();

        // TODO: This will be calculated every time the message needs to update. Can probably also be stored in a cache.
        if (countedMembers.containsKey(guild.getId())) {
            StringBuilder workingString = new StringBuilder();

            for (Member m : countedMembers.get(guild.getId())) {
                if (workingString.length() < 900) {
                    workingString.append(m.getAsMention());
                    workingString.append("\n");
                }
                else {
                    countedMemberList.add(workingString.toString());
                    workingString = new StringBuilder();
                }
            }
            countedMemberList.add(workingString.toString());
        }

        return countedMemberList;
    }

    private EmbedBuilder addAttendanceToMessageEmbed(
            final List<String> countedMemberList,
            final EmbedBuilder embedBuilder
    ) {
        try {
            for (int i = 0; i < countedMemberList.size(); i++) {
                if (i == 0) {
                    embedBuilder.addField("Counted Members", countedMemberList.get(i), true);
                }
                else {
                    embedBuilder.addField("", countedMemberList.get(i), true);
                }
            }
        }
        catch (IllegalArgumentException e) {
            embedBuilder.addField("Max Counted Users reached!", "", false);
        }

        return embedBuilder;
    }

    private boolean insertAttendanceList(Guild guild) throws SQLException {
        if (countedMembers.containsKey(guild.getId())) {
            final List<AttendanceEntity> attendanceList = countedMembers
                    .get(guild.getId())
                    .stream()
                    .map(member -> new AttendanceEntity(member.getId(), guild.getId(), member.getNickname()))
                    .collect(Collectors.toList());

            return Arrays.stream(statements.insertAttendanceList(attendanceList))
                    .noneMatch(returnCode -> returnCode == -1);
        }
        else {
            return false;
        }
    }

    private int getTimerInteger(String possibleTimerInput, CommandEvent event) {
        int durationNum;

        try {
            durationNum = Integer.parseInt(possibleTimerInput);
            if (durationNum < 0) {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e) {
            event.sendErrorResponseToOriginatingChannel("Your second parameter is not a valid number!");
            return -1;
        }

        return durationNum;
    }

    private void saveNewAttendanceTimer(CommandEvent event, Settings settings, int newTimer, boolean isMinutes) {
        if (isMinutes) {
            settings.setAttendanceTimer(Duration.ofMinutes(newTimer));
            event.sendSuccessResponseToOriginatingChannel("Your new attendance timer is: " + newTimer + " minutes.");
        }
        else {
            settings.setAttendanceTimer(Duration.ofSeconds(newTimer));
            event.sendSuccessResponseToOriginatingChannel("Your new attendance timer is: " + newTimer + " seconds.");
        }

        SettingsCache.saveSettingsForGuild(event.getGuild(), settings);
    }

    /**
     * Method used in the switch statement in the main command.
     * Used to update the default timer for a guild.
     *
     * @param messages original command messages to parse
     * @param event originating CommandEvent
     * @param guildSettings settings for guild
     */
    private void saveTimer(
            final List<String> messages,
            final CommandEvent event,
            final Settings guildSettings
    ) {
        int durationNum;
        String durationString;

        if (messages.size() > 3) {
            durationNum = getTimerInteger(messages.get(2), event);
            final String probableMOrSChar = messages.get(3);
            final boolean isM = probableMOrSChar.equalsIgnoreCase("m");
            final boolean isS = probableMOrSChar.equalsIgnoreCase("s");
            if (!isM && !isS) {
                event.sendErrorResponseToOriginatingChannel(didNotSpecifyTimer);
                return;
            }

            saveNewAttendanceTimer(event, guildSettings, durationNum, isM);
            return;
        }

        if (!(messages.size() > 2)) {
            event.sendErrorResponseToOriginatingChannel(
                    "You didn't specify a time! Use !help attendance"
            );
            return;
        }
        final String userMessage = messages.get(2);
        final boolean containsM = userMessage.toLowerCase().contains("m");
        final boolean containsS = userMessage.toLowerCase().contains("s");
        final boolean containsValidChar = containsM || containsS;

        if (!containsValidChar) {
            event.sendErrorResponseToOriginatingChannel(improperlyFormattedError);
            return;
        }

        if (containsM) {
            durationString = userMessage.substring(0, userMessage.toLowerCase().indexOf("m"));
        }
        else {
            durationString = userMessage.substring(0, userMessage.toLowerCase().indexOf("s"));
        }

        durationNum = getTimerInteger(durationString, event);

        if (durationNum == -1) {
            return;
        }

        saveNewAttendanceTimer(event, guildSettings, durationNum, containsM);
    }
}
