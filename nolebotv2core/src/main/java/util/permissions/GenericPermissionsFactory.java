package util.permissions;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.settings.Settings;
import util.settings.SettingsCache;
import util.settings.SettingsFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GenericPermissionsFactory {
    private static final Logger logger = LogManager.getLogger(GenericPermissionsFactory.class);

    /**
     * Gets the set of permissions stored that are greater than or equal to permission
     * @param guild The guild object that the user to check is a member of
     * @param permission The permission to check against
     * @return The int permissionLevel for the specified user in the specified guild
     */
    public static TreeSet<GenericPermission> getPermissionsHigherOrEqualToGivenPermission(Guild guild, GenericPermission permission) {
        final Settings settings = SettingsFactory.getSettings(guild);

        return settings.getPermissionList()
                       .stream()
                       .filter(each -> each.getPermissionLevel() >= permission.getPermissionLevel())
                       .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Gets the int permissionLevel for the user in the current guild, if there is one
     * @param userId The ID of the user to check permissions for
     * @param guild The guild object that the user to check is a member of
     * @return The int permissionLevel for the specified user in the specified guild
     */
    public static int getHighestPermissionForUser(String userId, Guild guild) {
        return getHighestPermissionObjectForUser(userId, guild).getPermissionLevel();
    }

    /**
     * Gets the GenericPermission object for specified user in the specified guild, if there is one
     * @param userId The ID of the user to check permissions for
     * @param guild The guild object that the user to check is a member of
     * @return The GenericPermission for the specified user in the specified guild
     */
    public static GenericPermission getHighestPermissionObjectForUser(String userId, Guild guild) {
        return PermissionCache.getPermissionForUser(userId, guild.getId());
    }

    /**
     * Gets the set of permissions for the user in the current guild, if there is one
     * @param userId The ID of the user to check permissions for
     * @param guild The guild object that the user to check is a member of
     * @return The Set of permissions for the specified user in the specified guild if it exists, otherwise null
     */
    public static TreeSet<GenericPermission> getPermissionsForUser(String userId, Guild guild) {
        final Settings settings = SettingsFactory.getSettings(guild);
        final Optional<Member> member = Optional.ofNullable(guild.getMemberById(userId));
        //If the member exists gets tge
        if (member.isPresent()) {
            //The list of RoleIDs that the member holds
            final List<String> memberRoleIds = member.get().getRoles()
                    .stream()
                    .map(ISnowflake::getId)
                    .collect(Collectors.toList());
            //The set of Snowflake IDs for the permissions stored in settings
            final Set<String> guildPermissionIds = settings.getPermissionList()
                    .stream()
                    .map(GenericPermission::getSnowflakeId)
                    .collect(Collectors.toSet());
            //The set of the RoleIDs that the user holds that have a permission stored in settings
            final Set<String> userPermissions = memberRoleIds
                    .stream()
                    .filter(guildPermissionIds::contains)
                    .collect(Collectors.toCollection(TreeSet::new));
            // The set of GenericPermissions that apply to the user from their roles +
            // GenericPermissions defined for their UserID
            final TreeSet<GenericPermission> userGenericPermissionList = settings.getPermissionList()
                    .stream()
                    // Gets a set of GenericPermissions that are stored in settings that
                    // correspond to the RoleIDs that match the snowflake ID of the permissions stored in settings
                    .filter(ele -> userPermissions
                            .stream()
                            .anyMatch(element -> ele.getSnowflakeId().equals(element))
                    )
                    .collect(Collectors.toCollection(TreeSet::new));

            // add user permission to list if present
            settings.getPermissionList()
                    .stream()
                    // Gets the Set of GenericPermissions that are stored for a user
                    .filter(entry -> userId.equals(entry.getSnowflakeId()))
                    // Checks if there are any GenericPermissions stored for a user
                    .findAny()
                    // If there are, add them to the list of permissions stored for a user
                    .ifPresent(userGenericPermissionList::add);

            return userGenericPermissionList;
        }
        else {
            logger.error("Tried to get permissions for nonexistent user {} in Guild {} with ID [{}] but user did not exist in Guild!", userId, guild
                    .getName(), guild.getId());
            return null;
        }
    }
}
