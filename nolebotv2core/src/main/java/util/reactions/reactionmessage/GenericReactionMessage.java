package util.reactions.reactionmessage;

import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.List;

public class GenericReactionMessage {
    private CommandEvent       event;
    private String             reactionMessageId;
    // Permission level override to clear out messages?

    private Integer            messageEmbedIndex;
    private List<MessageEmbed> messageEmbeds;
}
