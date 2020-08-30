package util.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.FilesUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsFactory {
    private static final Logger logger = LogManager.getLogger(SettingsFactory.class);

    /**
     * Get settings for guild by ID
     * @param guildId will be used as file path for guild
     * @return Settings object
     */
    public static Settings getSettingsForGuildFromFile(String guildId) {
        Settings settings;

        final Path settingsPath = getSettingsPathForGuild(guildId);
        final String guildSettingsJsonString = FilesUtil.getFileContentsAsStringAndCreateIfNotExists(settingsPath);

        if (guildSettingsJsonString.isEmpty()) {
            logger.info("Settings was empty, creating new Settings for guild with id {}.", guildId);
            settings = new Settings(guildId);

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
}
