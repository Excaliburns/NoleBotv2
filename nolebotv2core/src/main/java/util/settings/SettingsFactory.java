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

    /**
     * Get settings for guild by ID
     * @param guild will be used as file path for guild & initalizing settings if needed
     * @return Settings object
     */
    public static Settings getSettingsForGuildFromFile(Guild guild) {
        final String guildId = guild.getId();
        Settings settings;

        final Path settingsPath = getSettingsPathForGuild(guildId);
        final String guildSettingsJsonString = FilesUtil.getFileContentsAsStringAndCreateIfNotExists(settingsPath);

        if (guildSettingsJsonString.isEmpty()) {
            logger.info("Settings was empty, creating new Settings for guild with id {}.", guildId);
            settings = initDefaultPermissionListForGuild(guild);

            // Write new settings to file
            FilesUtil.writeStringToFile(settingsPath, FilesUtil.GSON_INSTANCE.toJson(settings));
        }
        else {
            logger.info("Found settings for guild with id {}", guildId);
            settings = FilesUtil.GSON_INSTANCE.fromJson(guildSettingsJsonString, Settings.class);
        }

        return settings;
    }


    public static Path getSettingsPathForGuild(String guildID) {
        return Paths.get("config/" + guildID + "/settings.json");
    }

    /**
     * Initialize default settings object for guild
     * @param guild
     * @return settings with default permissions and prefix
     */
    private static Settings initDefaultPermissionListForGuild(Guild guild) {
        List<Role> defaultAdminRoles = guild.getRoles()
                                            .stream()
                                            .filter(role -> role.hasPermission(Permission.ADMINISTRATOR))
                                            .collect(Collectors.toList());

        TreeSet<GenericPermission> defaultPermissions = defaultAdminRoles.stream()
                                                     .map(role -> new GenericPermission(PermissionType.ROLE, role.getName(), role.getId(), 1000))
                                                     .collect(Collectors.toCollection(TreeSet::new));

        Settings settings = new Settings();
        settings.setPermissionList(defaultPermissions);

        logger.info("Successfully created settings");
        return settings;
    }
}
