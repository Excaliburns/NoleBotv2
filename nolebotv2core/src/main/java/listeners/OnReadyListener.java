package listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.FilesUtil;
import util.settings.Settings;
import util.settings.SettingsFactory;
import util.settings.SettingsManager;

import java.nio.file.Path;
import java.util.List;

public class OnReadyListener extends ListenerAdapter {
    private static Logger logger = LogManager.getLogger(OnReadyListener.class);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("Hey! It looks like we're ready :D. Time to make sure everything is in order..");

        final List<Guild> guilds = event.getJDA().getGuilds();

        guilds.forEach( guild -> {
            final String guildId = guild.getId();
            if (!SettingsManager.doesSettingsExistForGuild(guildId)){
                logger.info("Settings was empty, creating new Settings for guild with id {}.", guildId);
                Settings settings = SettingsFactory.initComplexDefaults(guild);
                final Path settingsPath = SettingsFactory.getSettingsPathForGuild(guild.getId());
                FilesUtil.createFileIfNotExists(settingsPath);

                // Write new settings to file
                FilesUtil.writeStringToFile(settingsPath, FilesUtil.GSON_INSTANCE.toJson(settings));
            }
        });
    }
}
