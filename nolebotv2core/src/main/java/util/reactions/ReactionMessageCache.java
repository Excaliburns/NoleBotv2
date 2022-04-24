package util.reactions;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import net.dv8tion.jda.api.entities.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.chat.EmbedHelper;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ReactionMessageCache {
    private static final Logger logger = LogManager.getLogger(ReactionMessageCache.class);

    // MessageId, ReactionMessage
    @SuppressWarnings({"Convert2Lambda", "Convert2Diamond"})
    //A cache of message contents to Reaction Messages
    private static final Cache<String, ReactionMessage> reactionMessageCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                //Edits the original message when the cache expires to show that
                // Reactions are no longer being listened to
                //Also clears reactions from the original message.
                public void onRemoval(@NotNull RemovalNotification<Object, Object> notification) {
                    if (
                            notification.getCause() == RemovalCause.EXPIRED
                            || notification.getCause() == RemovalCause.COLLECTED
                    ) {
                        final String          originalMessageId = (String) notification.getKey();
                        final ReactionMessage messageContents   = (ReactionMessage) notification.getValue();

                        if (messageContents != null && originalMessageId != null) {
                            messageContents.getOriginatingMessageChannel().editMessageEmbedsById(
                                    originalMessageId,
                                    EmbedHelper.getDefaultExpiryReactionMessage()
                            ).queue(andAfter -> andAfter.clearReactions().queue());
                        }
                    }
                }
            })
            .build();


    public static Optional<ReactionMessage> getReactionMessage(String message) {
        return Optional.ofNullable(reactionMessageCache.getIfPresent(message));
    }

    /**
     * Set a reaction message to a new reaction message in the cache.
     *
     * @param messageId MessageId to set
     * @param reactionMessage Reaction Message to put in the cache
     */
    public static void setReactionMessage(String messageId, ReactionMessage reactionMessage) {
        synchronized (reactionMessageCache) {
            reactionMessageCache.put(messageId, reactionMessage);
        }
    }

    /**
     * Expire a reaction message in the cache.
     *
     * @param messageId MessageId to expire
     */
    public static void expireReactionMessage(String messageId) {
        synchronized (reactionMessageCache) {
            reactionMessageCache.invalidate(messageId);
        }
    }

    public static void cleanUpCache() {
        reactionMessageCache.cleanUp();
    }
}
