package commands.guildcommands;

import commands.util.CommandEvent;
import commands.util.ReactionCommand;
import enums.EmojiCodes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.NoleBotUtil;
import util.chat.EmbedHelper;
import util.db.statements.AttendanceStatements;
import util.reactions.ReactionMessage;
import util.reactions.ReactionMessageCache;
import util.reactions.ReactionMessageType;
import util.settings.Settings;
import util.settings.SettingsCache;
import util.settings.SettingsFactory;

import java.sql.SQLException;
import java.text.NumberFormat;
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

public class Attendance extends ReactionCommand {
    private static final Logger logger = LogManager.getLogger(Attendance.class);
    private final AttendanceStatements statements = new AttendanceStatements();

    private final ConcurrentHashMap<Guild, Duration> timeLeft    = new ConcurrentHashMap<>();
    private final HashMap<Guild, List<Member>> countedMembers    = new HashMap<>();
    private final HashMap<Guild, Message> attendanceMessageCache = new HashMap<>();

    public Attendance() {
        name                    = "attendance";
        description             = "Use this command to start/stop the attendance reaction message, or set the timer. Should only be used once per 'meeting'";
        helpDescription         = "Starts/Stops attendance reaction message, or sets the timer.";
        requiredPermissionLevel = 1000;
        usages.add("attendance start");
        usages.add("attendance timer [time][s/m]");
        usages.add("attendance stop");
    }

    @Override
    public void onCommandReceived(CommandEvent event) {
        final List<String> messages = event.getMessageContent();

        if (messages.size() > 1) {
            final String command = messages.get(1);

            switch (command) {
                case "start" -> {
                    if (!attendanceMessageCache.containsKey(event.getGuild())) {
                        handleStartAttendance(event);
                    }
                    else {
                        event.sendErrorResponseToOriginatingChannel("Can not start two attendance counters at the same time in one guild");
                    }
                }
                case "timer" -> {
                    if (messages.size() > 2) {
                        int durationNum;
                        final String potentialContainsSecondsOrMinute = messages.get(2);
                        final Settings guildSettings = event.getSettings();

                        if (potentialContainsSecondsOrMinute.toLowerCase().contains("m")) {
                            final String durationString = potentialContainsSecondsOrMinute.substring(0, potentialContainsSecondsOrMinute.toLowerCase().indexOf("m"));

                            durationNum = getTimerInteger(durationString, event);
                            if (durationNum == -1) return;

                            guildSettings.setAttendanceTimer(Duration.ofSeconds(durationNum));
                            SettingsCache.saveSettingsForGuild(event.getGuild(), guildSettings);
                            event.sendSuccessResponseToOriginatingChannel("Your new attendance timer is: " + durationNum + " minutes.");

                        }
                        else if (potentialContainsSecondsOrMinute.toLowerCase().contains("s")) {
                            final String durationString = potentialContainsSecondsOrMinute.substring(0, potentialContainsSecondsOrMinute.toLowerCase().indexOf("s"));

                            durationNum = getTimerInteger(durationString, event);
                            if (durationNum == -1) return;

                            guildSettings.setAttendanceTimer(Duration.ofSeconds(durationNum));
                            SettingsCache.saveSettingsForGuild(event.getGuild(), guildSettings);
                            event.sendSuccessResponseToOriginatingChannel("Your new attendance timer is: " + durationNum + " seconds.");
                        }
                        else {
                            if (messages.size() > 3) {

                                durationNum = getTimerInteger(messages.get(2), event);
                                if (durationNum == -1) return;

                                final String probableMOrSChar = messages.get(3);

                                if (probableMOrSChar.equalsIgnoreCase("m")) {
                                    guildSettings.setAttendanceTimer(Duration.ofMinutes(durationNum));
                                    SettingsCache.saveSettingsForGuild(event.getGuild(), guildSettings);
                                    event.sendSuccessResponseToOriginatingChannel("Your new attendance timer is: " + durationNum + " minutes.");
                                }
                                else if (probableMOrSChar.equalsIgnoreCase("s")) {
                                    guildSettings.setAttendanceTimer(Duration.ofSeconds(durationNum));
                                    SettingsCache.saveSettingsForGuild(event.getGuild(), guildSettings);
                                    event.sendSuccessResponseToOriginatingChannel("Your new attendance timer is: " + durationNum + " seconds.");
                                }
                                else {
                                    event.sendErrorResponseToOriginatingChannel("You didn't specify minutes or seconds. Please use 'm' or 's' to denote the time scale you would like.");
                                }
                            }
                            else {
                                event.sendErrorResponseToOriginatingChannel("Your message is improperly formatted. Use !help attendance to see more detailed usage information.");
                            }
                        }
                    }
                    else {
                        event.sendErrorResponseToOriginatingChannel("You didn't specify a time! Use !help attendance");
                    }
                }
                case "stop" -> {
                    if (attendanceMessageCache.containsKey(event.getGuild())) {
                        timeLeft.put(event.getGuild(), Duration.ZERO);
                    }
                    else {
                        event.sendErrorResponseToOriginatingChannel("Attendance is not currently being taken");
                    }
                }
            }
        }
        else {
            event.sendErrorResponseToOriginatingChannel("Please start, stop, or modify the timer. Use !help attendance");
        }
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent event, ReactionMessage message, Message retrievedDiscordMessage) {
        final Member originalMember = event.getGuild().getMemberById(message.getUserInitiatedId());
        if (originalMember != null && Objects.nonNull(originalMember.getVoiceState()) && originalMember.getVoiceState().inVoiceChannel()) {
            final VoiceChannel voiceChannel = originalMember.getVoiceState().getChannel();

            // If they are in the same voice channel as the original sender
            if (Objects.nonNull(event.getMember().getVoiceState()) && event.getMember().getVoiceState().getChannel() == voiceChannel) {
                if (event.getReactionEmote().getEmoji().equals(EmojiCodes.CHECK_MARK.unicodeValue)) {
                    final List<Member> memberList = countedMembers.containsKey(event.getGuild()) ? countedMembers.get(event.getGuild()) : new ArrayList<>();

                    if (!memberList.contains(event.getMember())) {
                        memberList.add(event.getMember());
                    }

                    countedMembers.put(event.getGuild(), memberList);
                }
            }
            else {
                event.getUser().openPrivateChannel().queue(callback -> callback.sendMessage("It seems that you aren't in the same voice channel as the person taking attendance. Make sure you join their voice channel in order to be counted!").queue());
            }
        }
        else {
            message.getOriginatingMessageChannel().sendMessage("It seems like the person who is taking attendance is currently not in a voice channel. Please tell them to join one so we can verify that you are in the meeting with them!").queue();
        }
    }

    private void handleStartAttendance(CommandEvent event) {
        final Guild guild = event.getGuild();
        final MessageChannel channel = event.getChannel();
        final String authorId = event.getOriginatingJDAEvent().getAuthor().getId();
        final String messageId = event.getOriginatingJDAEvent().getMessageId();

        timeLeft.put(guild, event.getSettings().getAttendanceTimer());

        final ReactionMessage message = getReactionMessageForAttendance(guild, channel, authorId, messageId);

        event.getChannel().sendMessage(message.getEmbedList().get(0))
                .queue(sentMessage -> {
                    sentMessage.addReaction(EmojiCodes.CHECK_MARK.unicodeValue).queue();
                    ReactionMessageCache.setReactionMessage(sentMessage.getId(), message);
                    attendanceMessageCache.put(guild, sentMessage);

                    Timer timer  = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            updateAttendancePage(guild, channel, authorId, messageId);

                            if (timeLeft.get(guild).toSeconds() <= 0) {
                                timer.cancel();

                                EmbedBuilder finalEmbed = EmbedHelper.getDefaultEmbedBuilder();

                                try {

                                    if (insertAttendanceList(guild)) {
                                        finalEmbed.addField("Final count! These names have also been added to the database", "", false);
                                    }
                                }
                                catch (SQLException e) {
                                    logger.error(e);
                                    channel.sendMessage("Couldn't update the database!").queue();
                                    channel.sendMessage(EmbedHelper.getDefaultExceptionReactionMessage(e)).queue();

                                    finalEmbed.addField("Final count! As there was an error, these names have not been added to the database", "", false);
                                }
                                finally {
                                    //noinspection ConstantConditions
                                    finalEmbed = addAttendanceToMessageEmbed(getAttendanceAsEmbedFieldList(guild), finalEmbed);

                                    channel.sendMessage(finalEmbed.build()).queue();
                                    attendanceMessageCache.remove(guild);
                                    timeLeft.remove(guild);
                                    countedMembers.remove(guild);
                                }
                            }
                        }
                    }, 0, Duration.ofSeconds(2).toMillis());
                });
    }

    // Called every thirty seconds
    private void updateAttendancePage(
            final Guild guild,
            final MessageChannel channel,
            final String authorId,
            final String messageId
    ) {
        Duration newTimeLeft = timeLeft.get(guild).minus(Duration.ofSeconds(2));
        timeLeft.put(guild, newTimeLeft);

        final ReactionMessage newReactionMessage = getReactionMessageForAttendance(guild, channel, authorId, messageId);

        channel.editMessageById(
                attendanceMessageCache.get(guild).getId(), newReactionMessage.getEmbedList().get(0)
        ).queue(sentMessage -> {
            ReactionMessageCache.setReactionMessage(sentMessage.getId(), newReactionMessage);
            attendanceMessageCache.put(guild, sentMessage);
        });
    }

    private ReactionMessage getReactionMessageForAttendance(
            final Guild guild,
            final MessageChannel channel,
            final String authorID,
            final String messageId
    ) {
        final Duration attendanceTimer = timeLeft.get(guild);
        List<String> countedMemberList = getAttendanceAsEmbedFieldList(guild);

        EmbedBuilder attendancePage = EmbedHelper.getDefaultEmbedBuilder()
                .addField("Click the check mark to be added to the attendance!", "", false);

        //noinspection ConstantConditions
        attendancePage = addAttendanceToMessageEmbed(countedMemberList, attendancePage);

        if ( !attendanceTimer.isZero() && !attendanceTimer.isNegative() ) {
            attendancePage.addField("Time Left", NoleBotUtil.getFormattedDurationString(attendanceTimer), false);
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
        if (countedMembers.containsKey(guild)) {
            StringBuilder workingString = new StringBuilder();

            for (Member m : countedMembers.get(guild)) {
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

    private EmbedBuilder addAttendanceToMessageEmbed(final List<String> countedMemberList, final EmbedBuilder embedBuilder) {
        try {
            for (int i = 0; i < countedMemberList.size(); i++) {
                if (i == 0) {
                    embedBuilder.addField("Counted Members", countedMemberList.get(i), true);
                }
                else {
                    embedBuilder.addField("", countedMemberList.get(i), true);
                }
            }
        } catch (IllegalArgumentException e) {
            embedBuilder.addField("Max Counted Users reached!", "", false);
        }

        return embedBuilder;
    }

    private boolean insertAttendanceList(Guild guild) throws SQLException {
        if (countedMembers.containsKey(guild)) {
            final List<util.db.entities.Attendance> attendanceList = countedMembers.get(guild).stream()
                    .map( member -> new util.db.entities.Attendance(member.getId(), guild.getId(), member.getNickname()))
                    .collect(Collectors.toList());

            return Arrays.stream(statements.insertAttendanceList(attendanceList)).noneMatch(pred -> pred == -1);
        }
        else {
            return false;
        }
    }

    private int getTimerInteger(String possibleTimerInput, CommandEvent event) {
        int durationNum;

        try {
            durationNum = Integer.parseInt(possibleTimerInput);
            if (durationNum < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            event.sendErrorResponseToOriginatingChannel("Your second parameter is not a valid number!");
            return -1;
        }

        return durationNum;
    }
}
