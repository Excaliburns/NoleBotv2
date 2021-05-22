package commands.util;

import enums.EmojiCodes;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class MessageUtil {
    public static void sendErrorResponseToChannel(String errorMessageContent, MessageChannel channel) {
        channel.sendMessage(EmojiCodes.DOUBLE_BANG.unicodeValue + "     " + errorMessageContent + "     " + EmojiCodes.DOUBLE_BANG.unicodeValue)
               .queue();
    }

    public static void sendErrorResponseToChannel(MessageChannel channel, String ...errorMessages) {
        sendErrorResponseToChannel(channel, Arrays.asList(errorMessages));
    }

    public static void sendErrorResponseToChannel(MessageChannel channel, List<String> errorMessageContent) {
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

    public static void sendSuccessResponseToChannel(final String successMessageContent, final MessageChannel channel) {
        channel.sendMessage(EmojiCodes.CHECK_MARK.unicodeValue + " " + EmojiCodes.DASH.unicodeValue + " " + successMessageContent).queue();
    }

    public static void sendSuccessResponseToChannel(final MessageChannel channel, final List<String> successMessageContent) {
        MessageBuilder builder = new MessageBuilder();

        builder.append(EmojiCodes.CHECK_MARK.unicodeValue)
               .append(" ")
               .append(EmojiCodes.DASH.unicodeValue)
               .append(" ")
               .append("Successfully executed the following events:\n");

        for (String s : successMessageContent) {
            if (builder.length() > 1500) {
                channel.sendMessage(builder.build()).queue();
                builder = new MessageBuilder();
            }

            builder.append(s);
        }

        channel.sendMessage(builder.build()).queue();
    }

    public static void printStackTraceToChannelFromThrowable(final MessageChannel channel, final Throwable e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw  = new PrintWriter(sw);
        e.printStackTrace(pw);

        final String firstTenLinesOfStackTrace = Arrays.stream((sw.toString() + " ").split("\r?\n"))
                                                       .limit(10)
                                                       .collect(Collectors.joining("\n"));

        channel.sendMessage("```java\n" + firstTenLinesOfStackTrace + "...```").queue();
    }

    public static void sendMessageToChannel(Queue<Message> message, final MessageChannel channel) {
        while (message.peek() != null) {
            channel.sendMessage(message.poll()).queue();
        }
    }
    public static void sendMessageToChannel(final Message message, final MessageChannel channel) {
        channel.sendMessage(message).queue();
    }

    public static void sendMessageToChannel(final String message, final MessageChannel channel) {
        channel.sendMessage(message).queue();
    }

    public static void sendMessageToChannel(final MessageEmbed embed, final MessageChannel channel) {
        channel.sendMessage(embed).queue();
    }
}
