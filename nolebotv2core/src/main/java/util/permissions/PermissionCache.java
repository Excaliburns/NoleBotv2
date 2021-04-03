package util.permissions;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class PermissionCache {
    private static final Logger logger = LogManager.getLogger(PermissionCache.class);
    //Builds a cache that matches a SnowflakeID (currently only works for UserID) in a Guild to a set of permissions
    // I think it should be possible to store permissions for any SnowflakeID in this cache.
    // I know each UserID is unique, but I'm unsure if there is ever overlap between UserIDs and RoleIDs
    private static final LoadingCache<Pair<String, Guild>, TreeSet<GenericPermission>> permissionCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public TreeSet<GenericPermission> load(@NotNull Pair<String, Guild> key) throws Exception {
                    logger.info("Permissions not in cache for user {} - loading from disk.", key);
                    return GenericPermissionsFactory.getPermissionsForUser(key.getLeft(), key.getRight());
                }
            });
    /**
     * Gets the GenericPermission object for the user in the current guild, if there is one
     * @param userId The ID of the user to check permissions for
     * @param guild The guild object that the user to check is a member of
     * @return The GenericPermission stored for the user
     */
    //Gets the set of permissions for the user specified
    //It is possible that this could error I think. If the user is not a member of the guild, the getPermissionForUser method
    //in GenericPermissionsFactory that is called when getting the permission from the cache can return null. I do not think it will in practice though
    //Why do we use Pair<userID, Guild> to map permissions instead of just a String memberId?
    //Are there any cases where the member we are checking permissions for wont be in the guild?
    public static GenericPermission getPermissionForUser(String userId, Guild guild) {
        return permissionCache.getUnchecked(Pair.of(userId, guild)).first();
    }
}
