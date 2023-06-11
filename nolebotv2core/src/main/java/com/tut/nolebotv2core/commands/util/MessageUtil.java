package com.tut.nolebotv2core.commands.util;

import com.tut.nolebotv2core.enums.EmojiCodes;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class MessageUtil {
    /**
     * Send an error message to the channel.
     *
     * @param errorMessageContent Content of the error message
     * @param channel MessageChannel to send it in
     */
    public static void sendErrorResponseToChannel(String errorMessageContent, MessageChannelUnion channel) {
        channel.sendMessage(
                EmojiCodes.DOUBLE_BANG.unicodeValue
                        + "     "
                        + errorMessageContent
                        + "     "
                        + EmojiCodes.DOUBLE_BANG.unicodeValue
                )
                .queue();
    }

    /**
     * Send an error message to the channel.
     *
     * @param channel MessageChannel to send it in
     * @param errorMessages Error messages to send
     */
    public static void sendErrorResponseToChannel(MessageChannelUnion channel, String ...errorMessages) {
        sendErrorResponseToChannel(channel, Arrays.asList(errorMessages));
    }

    /**
     * Send an error message to the channel.
     *
     * @param channel MessageChannel to send it in
     * @param errorMessageContent Error messages to send
     */
    public static void sendErrorResponseToChannel(MessageChannelUnion channel, List<String> errorMessageContent) {
        StringBuilder builder = new StringBuilder();

        for (String s : errorMessageContent) {
            if (builder.length() > 1500) {
                channel.sendMessage(builder.toString()).queue();
                builder = new StringBuilder();
            }

            builder.append(s);
        }

        channel.sendMessage(builder.toString()).queue();
    }

    /**
     * Send a success message to the channel.
     *
     * @param successMessageContent Success message to send
     * @param channel MessageChannel to send it in
     */
    public static void sendSuccessResponseToChannel(final String successMessageContent, final MessageChannelUnion channel) {
        channel.sendMessage(
                EmojiCodes.CHECK_MARK.unicodeValue
                        + " "
                        + EmojiCodes.DASH.unicodeValue
                        + " "
                        + successMessageContent
        ).queue();
    }

    /**
     * Send a success message to the channel.
     *
     * @param successMessageContent Success messages to send
     * @param channel MessageChannel to send it in
     */
    public static void sendSuccessResponseToChannel(
            final MessageChannelUnion channel,
            final List<String> successMessageContent
    ) {
        StringBuilder builder = new StringBuilder();

        builder.append(EmojiCodes.CHECK_MARK.unicodeValue)
                .append(" ")
                .append(EmojiCodes.DASH.unicodeValue)
                .append(" ")
                .append("Successfully executed the following events:\n");

        for (String s : successMessageContent) {
            if (builder.length() > 1500) {
                channel.sendMessage(builder.toString()).queue();
                builder = new StringBuilder();
            }

            builder.append(s);
        }

        channel.sendMessage(builder.toString()).queue();
    }

    /**
     * Send a stack trace to the channel in an escape block.
     *
     * @param channel MessageChannel to send it in
     * @param e Throwable to parse
     */
    public static void printStackTraceToChannelFromThrowable(final MessageChannelUnion channel, final Throwable e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw  = new PrintWriter(sw);
        e.printStackTrace(pw);

        final String firstTenLinesOfStackTrace = Arrays.stream((sw + " ").split("\r?\n"))
                                                       .limit(10)
                                                       .collect(Collectors.joining("\n"));

        channel.sendMessage("```java\n" + firstTenLinesOfStackTrace + "...```").queue();
    }

    /**
     * Send a message to a channel.
     *
     * @param message Queue containing a message to send
     * @param channel Channel to send it in
     */
    public static void sendMessageToChannel(Queue<MessageCreateData> message, final MessageChannelUnion channel) {
        while (message.peek() != null) {
            channel.sendMessage(message.poll()).queue();
        }
    }

    public static void sendMessageToChannel(final MessageCreateData message, final MessageChannelUnion channel) {
        channel.sendMessage(message).queue();
    }

    public static void sendMessageToChannel(final String message, final MessageChannelUnion channel) {
        channel.sendMessage(message).queue();
    }

    public static void sendMessageToChannel(final MessageEmbed embed, final MessageChannelUnion channel) {
        channel.sendMessageEmbeds(embed).queue();
    }
}
