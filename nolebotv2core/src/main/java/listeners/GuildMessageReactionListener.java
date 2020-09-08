package listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.reactions.ReactionMessageCache;

public class GuildMessageReactionListener extends ListenerAdapter {
    private static Logger logger = LogManager.getLogger(GuildMessageReactionListener.class);

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;

        // If the message can be retrieved, do some stuff.
        event.retrieveMessage().queue(callback -> {
            if (ReactionMessageCache.getReactionMessage(callback.getId()).isPresent()) {
                // Do some special stuff
            }
        });

    }
}
