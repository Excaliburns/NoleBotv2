package commands.util;

import enums.EmojiCodes;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import util.settings.Settings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class CommandEvent {
    private GuildMessageReceivedEvent originatingJDAEvent;
    private Command                   command;

    private List<String>   messageContent;
    private Guild          guild;
    private String         guildId;
    private MessageChannel channel;
    private Settings       settings;

    public CommandEvent(GuildMessageReceivedEvent event, List<String> message, Settings settings, Command command) {
        this.originatingJDAEvent = event;
        this.command             = command;
        this.messageContent      = message;
        this.guild               = event.getGuild();
        this.guildId             = event.getGuild().getId();
        this.channel             = event.getChannel();
        this.settings            = settings;
    }

    public void sendErrorResponseToOriginatingChannel(String errorMessageContent) {
        channel.sendMessage(EmojiCodes.DOUBLE_BANG.unicodeValue + "     " + errorMessageContent + "     " + EmojiCodes.DOUBLE_BANG.unicodeValue)
                .queue();
    }

    public void sendErrorResponseToOriginatingChannel(String ...errorMessages) {
        sendErrorResponseToOriginatingChannel(Arrays.asList(errorMessages));
    }

    public void sendErrorResponseToOriginatingChannel(List<String> errorMessageContent) {
        MessageBuilder builder = new MessageBuilder();

        for (String s : errorMessageContent) {
            if (builder.length() > 1500) {
                channel.sendMessage(builder.build()).queue();
                builder = new MessageBuilder();
            }

            builder.append(s);
        }

        channel.sendMessage(builder.build()).queue();
    }

    public void sendSuccessResponseToOriginatingChannel(String successMessageContent) {
        channel.sendMessage(EmojiCodes.CHECK_MARK.unicodeValue + " " + EmojiCodes.DASH.unicodeValue + " " + successMessageContent).queue();
    }

    public void printStackTraceToChannelFromThrowable(MessageChannel channel, Throwable e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw  = new PrintWriter(sw);
        e.printStackTrace(pw);

        final String firstTenLinesOfStackTrace = Arrays.stream((sw.toString() + " ").split("\r?\n"))
                .limit(10)
                .collect(Collectors.joining("\n"));

        channel.sendMessage("```java\n" + firstTenLinesOfStackTrace + "...```").queue();
    }

    public void sendMessageToOriginatingChannel(Message message) {
        this.originatingJDAEvent.getChannel().sendMessage(message).queue();
    }

    public void sendMessageToOriginatingChannel(MessageEmbed embed) {
        this.originatingJDAEvent.getChannel().sendMessage(embed).queue();
    }
}
