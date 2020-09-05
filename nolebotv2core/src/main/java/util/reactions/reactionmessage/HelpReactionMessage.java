package util.reactions.reactionmessage;

import net.dv8tion.jda.api.entities.MessageReaction;

public class HelpReactionMessage extends GenericReactionMessage {
    private MessageReaction.ReactionEmote nextPageEmote;
    private MessageReaction.ReactionEmote previousPageEmote;
    private MessageReaction.ReactionEmote closeEmote;
}
