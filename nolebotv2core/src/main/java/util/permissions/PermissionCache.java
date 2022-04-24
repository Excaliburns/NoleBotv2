package util.permissions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.NoleBotUtil;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class PermissionCache {
    private static final Logger logger = LogManager.getLogger(PermissionCache.class);
    //Builds a cache that matches a SnowflakeID (currently only works for UserID) in a Guild to a set of permissions
    // I think it should be possible to store permissions for any SnowflakeID in this cache.
    // I know each UserID is unique, but I'm unsure if there is ever overlap between UserIDs and RoleIDs
    private static final LoadingCache<Pair<String, String>, TreeSet<GenericPermission>> permissionCache =
            CacheBuilder.newBuilder()
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .build(new CacheLoader<>() {
                        @SuppressWarnings("NullableProblems")
                        @Override
                        public TreeSet<GenericPermission> load(@NotNull Pair<String, String> key) throws Exception {
                            logger.info("Permissions not in cache for user {} - loading from disk.", key);
                            final Set<GenericPermission> permissionTree =
                                    GenericPermissionsFactory.getPermissionsForUser(
                                            key.getLeft(),
                                            NoleBotUtil.getJda().getGuildById(key.getRight())
                                    );
                            if (permissionTree == null) {
                                final String exceptionString = String.format(
                                        "Couldn't build permission set in permission cache for Guild:User %s:%s",
                                        key.getLeft(),
                                        key.getRight()
                                );
                                throw new Exception(exceptionString);
                            }
                            return new TreeSet<>(permissionTree);
                        }
                    });

    /**
     * Gets the GenericPermission object for the user in the current guild, if there is one.
     *
     * @param userId  The ID of the user to check permissions for
     * @param guildId The guild object that the user to check is a member of
     * @return The GenericPermission stored for the user
     */
    public static GenericPermission getPermissionForUser(String userId, String guildId) {
        return permissionCache.getUnchecked(Pair.of(userId, guildId)).first();
    }
}
