package util.permissions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class PermissionCache {
    private static final Logger logger = LogManager.getLogger(PermissionCache.class);

    private static final LoadingCache<Pair<String, Guild>, TreeSet<GenericPermission>> permissionCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public TreeSet<GenericPermission> load(@NotNull Pair<String, Guild> key) throws Exception {
                    logger.info("Permissions not in cache for user {} - loading from disk.", key);
                    return GenericPermissionsFactory.getPermissionsForUser(key.getLeft(), key.getRight());
                }
            });

    public static GenericPermission getPermissionForUser(String userId, Guild guild) {
        return permissionCache.getUnchecked(Pair.of(userId, guild)).first();
    }
}
