package util.settings;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.FilesUtil;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class SettingsCache {
    private static final Logger logger = LogManager.getLogger(SettingsCache.class);

    private static final LoadingCache<String, Settings> settingsCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterAccess(4, TimeUnit.HOURS)
            .build(new CacheLoader<>() {
                @Override
                public Settings load(final @NotNull String key) throws Exception {
                    logger.info("Settings not in cache for guild {} - loading from disk.", key);
                    return SettingsFactory.getSettingsForGuildFromFile(key);
                }
            });

    public static Settings getSettings(String guildID) {
        return settingsCache.getUnchecked(guildID);
    }

    /**
     * Save guild settings to file
     * @param guildID guildID
     * @param settings Settings object for Guild
     */
    public static void saveSettingsForGuild(String guildID, Settings settings) {
        final Path settingsPath = SettingsFactory.getSettingsPathForGuild(guildID);

        FilesUtil.writeStringToFile(settingsPath, FilesUtil.GSON_INSTANCE.toJson(settings));
        settingsCache.put(guildID, settings);
    }
}
