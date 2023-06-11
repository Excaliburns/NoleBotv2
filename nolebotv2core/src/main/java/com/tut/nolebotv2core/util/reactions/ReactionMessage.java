package com.tut.nolebotv2core.util.reactions;

import com.tut.nolebotv2core.enums.EmojiCodes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReactionMessage {
    private ReactionMessageType   type;
    private MessageChannel originatingMessageChannel;
    private String                userInitiatedId;
    private String                messageId;
    private int                   currentEmbedPage;
    private List<MessageEmbed>    embedList;
    private List<EmojiCodes>      reactionsUsed;
}
