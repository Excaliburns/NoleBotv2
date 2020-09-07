package util.reactions;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.List;

@Getter @Setter
public class ReactionMessage {
    private MessageChannel                      originatingMessageChannel;
    private String                              userInitiatedId;
    private String                              messageId;

    private int                                 currentEmbedPage;
    private List<MessageEmbed>                  embedList;

    private List<MessageReaction.ReactionEmote> emoteList;
}
