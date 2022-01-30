package listeners;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.chat.EmbedHelper;
import util.reactions.ReactionMessage;
import util.reactions.ReactionMessageCache;

import java.util.Optional;

public class GuildMessageReactionListener extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(GuildMessageReactionListener.class);
    //Fire when someone reacts to a message
    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        //If the user reacting is a bot, do nothing
        if (event.getUser().isBot()) {
            return;
        }

        // If the message can be retrieved, do some stuff.
        event.retrieveMessage().queue(callback -> {
            //If the author of the message reacted to is the bot
            //Clean up the ReactionMessageCache
            //If the message is a reaction message, execute the corresponding command
            if (callback.getAuthor() == event.getJDA().getSelfUser()) {
                ReactionMessageCache.cleanUpCache();
                final Optional<ReactionMessage> reactionMessageOptional = ReactionMessageCache.getReactionMessage(callback.getId());

                if (reactionMessageOptional.isPresent()) {
                    final ReactionMessage message = reactionMessageOptional.get();

                    message.getType().command.handleReaction(event, message, callback);
                }
                else {
                    // If the message wasn't found in the queue, but it was made by us, and it was edited, it was most
                    // Likely a reaction message that has expired in the cache.
                    if (callback.isEdited()) {
                        logger.warn("Message was not found in cache - message likely expired.");
                        callback.editMessageEmbeds(EmbedHelper.getDefaultExpiryReactionMessage()).queue();
                    }
                }
            }
        });

    }
}
