package util.reactions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import net.dv8tion.jda.api.JDA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.chat.EmbedHelper;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ReactionMessageCache {
    private static final Logger logger = LogManager.getLogger(ReactionMessageCache.class);

    // MessageId, ReactionMessage
    private static final LoadingCache<String, ReactionMessage> reactionMessageCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(@NotNull RemovalNotification<Object, Object> notification) {
                    if ((notification.getCause() == RemovalCause.EXPIRED)) {
                        final ReactionMessage messageContents = (ReactionMessage) notification.getValue();

                        messageContents.getOriginatingMessageChannel().sendMessage(
                                EmbedHelper.getDefaultExpiryReactionMessage()
                        ).queue();
                    }
                }
            })

            .build(new CacheLoader<String, ReactionMessage>() {
                @Override
                public ReactionMessage load(String key) throws Exception {
                    // do some stuff
                    return null;
                }
            });
}
