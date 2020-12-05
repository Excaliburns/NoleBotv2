package commands.util;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import util.reactions.ReactionMessage;

public abstract class ReactionCommand extends Command {
    public abstract void handleReaction(GuildMessageReactionAddEvent event, ReactionMessage message, Message retrievedDiscordMessage);
}
