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
    private static final Cache<String, ReactionMessage> reactionMessageCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(@NotNull RemovalNotification<Object, Object> notification) {
                    if (notification.getCause() == RemovalCause.EXPIRED || notification.getCause() == RemovalCause.EXPLICIT) {
                        final String          originalMessageId = (String) notification.getKey();
                        final ReactionMessage messageContents   = (ReactionMessage) notification.getValue();

                        messageContents.getOriginatingMessageChannel().editMessageById(
                                originalMessageId,
                                EmbedHelper.getDefaultExpiryReactionMessage()
                        ).complete();
                    }
                }
            })
            .build();


    public static Optional<ReactionMessage> getReactionMessage(String message) {
        return Optional.ofNullable(reactionMessageCache.getIfPresent(message));
    }

    public static void addReactionMessageToCache (String message, ReactionMessage reactionMessage) {
        synchronized (reactionMessageCache) {
            reactionMessageCache.put(message, reactionMessage);
        }
    }

    public static void expireReactionMessage(String message) {
        synchronized (reactionMessageCache) {
            reactionMessageCache.invalidate(message);
        }
    }
}
