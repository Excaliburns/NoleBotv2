package com.tut.nolebotv2core.commands.util;


import com.tut.nolebotv2core.enums.EmojiCodes;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import com.tut.nolebotv2core.util.chat.EmbedHelper;
import com.tut.nolebotv2core.util.reactions.ReactionMessage;
import com.tut.nolebotv2core.util.reactions.ReactionMessageCache;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public abstract class ReactionCommand extends Command {
    private EmojiCodes previousPageEmoji;
    private EmojiCodes nextPageEmoji;
    private EmojiCodes exitEmoji;
    private MessageEmbed exitMessage;
    protected static List<EmojiCodes> defaultEmojiCodeList = Arrays.asList(
            EmojiCodes.PREVIOUS_ARROW,
            EmojiCodes.EXIT,
            EmojiCodes.NEXT_ARROW
    );

    /**
     * Default constructor.
     * Sets the Previous Page emoji to PREVIOUS_ARROW,
     * Next Page emoji to NEXT_ARROW,
     * Exit emoji to EXIT,
     * and defaults the Exit Message
     */
    public ReactionCommand() {
        previousPageEmoji = EmojiCodes.PREVIOUS_ARROW;
        nextPageEmoji = EmojiCodes.NEXT_ARROW;
        exitEmoji = EmojiCodes.EXIT;
        exitMessage = EmbedHelper.getDefaultExitMessage();
    }

    /**
     * Default handle reaction implementation.
     * Will use the previous page emoji, next page emoji, and exit emoji of the instance.
     * exit message is also defaulted to EmbedHelper.getDefaultExitMessage()
     *
     * @param event ReactionAddEvent event
     * @param message Reaction Message payload
     * @param retrievedDiscordMessage original Discord Message object to act on
     */
    public void handleReaction(
            final GuildMessageReactionAddEvent event,
            final ReactionMessage message,
            final Message retrievedDiscordMessage
    ) {
        int nextPage;

        // if left arrow
        if (event.getReactionEmote().getEmoji().equals(previousPageEmoji.unicodeValue)) {
            nextPage = message.getCurrentEmbedPage() - 1;
        }
        // if right arrow
        else if (event.getReactionEmote().getEmoji().equals(nextPageEmoji.unicodeValue)) {
            nextPage = message.getCurrentEmbedPage() + 1;
        }
        else if (event.getReactionEmote().getEmoji().equals(exitEmoji.unicodeValue)) {
            retrievedDiscordMessage.editMessageEmbeds(exitMessage).queue();
            retrievedDiscordMessage.clearReactions().queue();
            ReactionMessageCache.expireReactionMessage(retrievedDiscordMessage.getId());
            return;
        }
        // if other reaction
        else {
            return;
        }
        // If the next page is between 0 and the number of permission pages
        if (nextPage > -1 && nextPage < message.getEmbedList().size()) {
            //Set the permission message to the next page
            message.setCurrentEmbedPage(nextPage);
            //Clear previous reactions
            retrievedDiscordMessage.editMessageEmbeds(message.getEmbedList().get(nextPage)).queue(editDone -> {
                editDone.clearReactions().queue();

                for (EmojiCodes emojiCode : message.getReactionsUsed()) {
                    // If the last embed page had a next arrow, and we are on the last embed page
                    boolean isLastPage = emojiCode == nextPageEmoji && nextPage == message.getEmbedList().size() - 1;
                    // If the last embed page had a next arrow, and we are on the first embed page
                    boolean isFirstPage = emojiCode == previousPageEmoji && nextPage == 0;
                    // If we aren't on the first page or last page, add the reaction we are checking
                    if (!isLastPage && !isFirstPage) {
                        editDone.addReaction(emojiCode.unicodeValue).queue();
                    }
                }

                ReactionMessageCache.setReactionMessage(message.getMessageId(), message);
            });
        }
    }

    /**
     * Sends the first page of the reaction command.
     *
     * @param event Original CommandEvent
     * @param firstPageEmbed First page MessageEmbed to send
     * @param pages Number of pages of the reaction message
     * @param reactionList Reaction list to use
     * @param reactionMessage ReactionMessage object to store in cache
     */
    public void sendFirstPage(
            final CommandEvent event,
            final MessageEmbed firstPageEmbed,
            final int pages,
            final List<EmojiCodes> reactionList,
            final ReactionMessage reactionMessage
    ) {
        event.getChannel().sendMessageEmbeds(firstPageEmbed)
                .queue(message -> {
                    if (pages > 1) {
                        for (EmojiCodes reaction : reactionList) {
                            if (reaction != previousPageEmoji) {
                                message.addReaction(reaction.unicodeValue).queue();
                            }
                        }
                    }
                    ReactionMessageCache.setReactionMessage(message.getId(), reactionMessage);
                });
    }
}
