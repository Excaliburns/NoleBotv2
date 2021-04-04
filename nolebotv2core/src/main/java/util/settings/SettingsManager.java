package util.settings;

import util.FilesUtil;

import java.nio.file.Path;

public class SettingsManager {
    //Checks if a settings file exists for a guild
    public static boolean doesSettingsExistForGuild(String guildId) {
        final Path settingsPath = SettingsFactory.getSettingsPathForGuild(guildId);
        final String guildSettingsJsonString = FilesUtil.getFileContentsAsString(settingsPath);
        return !guildSettingsJsonString.isEmpty();
    }
}
