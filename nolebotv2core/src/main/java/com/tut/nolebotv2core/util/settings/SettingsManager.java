package com.tut.nolebotv2core.util.settings;

import com.tut.nolebotv2core.util.FilesUtil;

import java.nio.file.Path;

public class SettingsManager {
    /**
     * Checks if a settings file exists for a guild.
     *
     * @param guildId GuildId to check for
     * @return True if settings exist
     */
    public static boolean doesSettingsExistForGuild(String guildId) {
        final Path settingsPath = SettingsFactory.getSettingsPathForGuild(guildId);
        final String guildSettingsJsonString = FilesUtil.getFileContentsAsString(settingsPath);
        return guildSettingsJsonString.isEmpty();
    }
}
