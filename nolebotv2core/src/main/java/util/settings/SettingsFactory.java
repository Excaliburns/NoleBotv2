package util.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.FilesUtil;
import util.permissions.GenericPermission;
import util.permissions.PermissionType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SettingsFactory {
    private static final Logger logger = LogManager.getLogger(SettingsFactory.class);

    public static Settings getSettings(Guild guild) {
        return SettingsCache.settingsCache.getUnchecked(guild.getId());
    }

    /**
     * Get settings for guild by ID
     * @param guildId will be used as file path for guild
     * @return Settings object
     */
    public static Settings getSettingsForGuildFromFile(String guildId) {
        if (SettingsManager.doesSettingsExistForGuild(guildId)) {
            logger.error("Settings was empty for guild {}, throwing exception. Create guild settings before trying to get them next time.", guildId);
            throw new NullPointerException(String.format("Guild settings do not exist for guild %s!", guildId));
        }
        else {
            logger.info("Found settings for guild with id {}", guildId);
            return FilesUtil.GSON_INSTANCE.fromJson(FilesUtil.getFileContentsAsString(getSettingsPathForGuild(guildId)), Settings.class);
        }
    }


    public static Path getSettingsPathForGuild(String guildID) {
        return Paths.get("config/" + guildID + "/settings.json");
    }

    /**
     * Initialize default settings object for guild
     * @param guild Admin roles from this guild will be initialized with permission level 1000
     * @return settings with default permissions and prefix
     */
    public static Settings initDefaultPermissionListForGuild(Guild guild) {
        List<Role> defaultAdminRoles = guild.getRoles()
                                            .stream()
                                            .filter(role -> role.hasPermission(Permission.ADMINISTRATOR))
                                            .collect(Collectors.toList());

        TreeSet<GenericPermission> defaultPermissions = defaultAdminRoles.stream()
                                                     .map(role -> new GenericPermission(PermissionType.ROLE, role.getName(), role.getId(), 1000))
                                                     .collect(Collectors.toCollection(TreeSet::new));

        Settings settings = new Settings(guild.getId());
        settings.setPermissionList(defaultPermissions);

        logger.info("Successfully created settings");
        return settings;
    }
}
