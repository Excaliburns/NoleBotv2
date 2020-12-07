package commands.guildcommands;

import commands.util.Command;
import commands.util.CommandEvent;
import commands.util.ReactionCommand;
import enums.EmojiCodes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import util.NoleBotUtil;
import util.chat.EmbedHelper;
import util.reactions.ReactionMessage;
import util.reactions.ReactionMessageCache;
import util.reactions.ReactionMessageType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Attendance extends ReactionCommand {
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
                case "timer" -> event.sendErrorResponseToOriginatingChannel("This functionality hasn't been implemented yet. " + EmojiCodes.PLEADING.unicodeValue);
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
        if (event.getReactionEmote().getEmoji().equals(EmojiCodes.CHECK_MARK.unicodeValue)) {
            final List<Member> memberList = countedMembers.containsKey(event.getGuild()) ? countedMembers.get(event.getGuild()) : new ArrayList<>();

            if (!memberList.contains(event.getMember())) {
                memberList.add(event.getMember());
            }

            countedMembers.put(event.getGuild(), memberList);
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
                                finalEmbed.addField("Final count! These names have also been added to the database", "", false);

                                //noinspection ConstantConditions
                                finalEmbed = addAttendanceToMessageEmbed(getAttendanceAsEmbedFieldList(guild), finalEmbed);

                                channel.sendMessage(finalEmbed.build()).queue();
                                attendanceMessageCache.remove(guild);
                                timeLeft.remove(guild);
                                countedMembers.remove(guild);
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
}
