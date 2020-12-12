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

    public static TreeSet<GenericPermission> getPermissionsHigherOrEqualToGivenPermission(Guild guild, GenericPermission permission) {
        final Settings settings = SettingsCache.getSettings(guild);

        return settings.getPermissionList()
                       .stream()
                       .filter(each -> each.getPermissionLevel() >= permission.getPermissionLevel())
                       .collect(Collectors.toCollection(TreeSet::new));
    }

    public static int getHighestPermissionForUser(String userId, Guild guild) {
        return getHighestPermissionObjectForUser(userId, guild).getPermissionLevel();
    }

    public static GenericPermission getHighestPermissionObjectForUser(String userId, Guild guild) {
        return PermissionCache.getPermissionForUser(userId, guild);
    }

    public static TreeSet<GenericPermission> getPermissionsForUser(String userId, Guild guild) {
        final Settings settings = SettingsCache.getSettings(guild);
        final Optional<Member> member = Optional.ofNullable(guild.getMemberById(userId));

        if (member.isPresent()) {
            final List<String> memberRoleIds = member.get()
                                                     .getRoles()
                                                     .stream()
                                                     .map(ISnowflake::getId)
                                                     .collect(Collectors.toList());

            final Set<String> guildPermissionIds = settings.getPermissionList()
                                                           .stream()
                                                           .map(GenericPermission::getSnowflakeId)
                                                           .collect(Collectors.toSet());

            final Set<String> userPermissions = memberRoleIds.stream()
                                                             .filter(guildPermissionIds::contains)
                                                             .collect(Collectors.toCollection(TreeSet::new));

            final TreeSet<GenericPermission> userGenericPermissionList = settings.getPermissionList()
                                                                                 .stream()
                                                                                 .filter(ele -> userPermissions.stream()
                                                                                                               .anyMatch(element -> ele
                                                                                                               .getSnowflakeId()
                                                                                                               .equals(element)))
                                                                                 .collect(Collectors.toCollection(TreeSet::new));

            // add user permission to list if present
            settings.getPermissionList()
                    .stream()
                    .filter(entry -> userId.equals(entry.getSnowflakeId()))
                    .findAny()
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
