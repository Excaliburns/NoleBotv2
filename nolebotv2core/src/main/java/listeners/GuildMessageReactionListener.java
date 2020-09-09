package listeners;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.reactions.ReactionMessage;
import util.reactions.ReactionMessageCache;

import java.util.Optional;

public class GuildMessageReactionListener extends ListenerAdapter {
    private static Logger logger = LogManager.getLogger(GuildMessageReactionListener.class);

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;

        // If the message can be retrieved, do some stuff.
        event.retrieveMessage().queue(callback -> {
            ReactionMessageCache.cleanUpCache();
            final Optional<ReactionMessage> reactionMessageOptional = ReactionMessageCache.getReactionMessage(callback.getId());

            if (reactionMessageOptional.isPresent()) {
                final ReactionMessage message = reactionMessageOptional.get();
                final int nextPage;


                switch (message.getType()) {
                    case HELP_COMMAND:
                        // if left arrow
                        if (event.getReactionEmote().getEmoji().equals("\u2B05")) {
                            nextPage = message.getCurrentEmbedPage() - 1;
                        }
                        else if (event.getReactionEmote().getEmoji().equals("\u27A1")) {
                            nextPage = message.getCurrentEmbedPage() + 1;
                        }
                        else {
                            throw new UnsupportedOperationException("Emoji not in supported emoji list!");
                        }

                        if (nextPage > -1 && nextPage < message.getEmbedList().size()) {
                            message.setCurrentEmbedPage(nextPage);

                            callback.editMessage(message.getEmbedList().get(nextPage)).queue(editDone -> {
                                editDone.clearReactions().queue();
                                message.getReactionsUsed().forEach(reaction -> editDone.addReaction(reaction).queue());
                                ReactionMessageCache.setReactionMessage(message.getMessageId(), message);
                            });
                        }
                }
            }
        });

    }
}
