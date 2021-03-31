package listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.FilesUtil;
import util.settings.Settings;
import util.settings.SettingsCache;
import util.settings.SettingsFactory;
import util.settings.SettingsManager;

import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class OnReadyListener extends ListenerAdapter {
    private static Logger logger = LogManager.getLogger(OnReadyListener.class);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("Hey! It looks like we're ready :D. Time to make sure everything is in order..");

        final List<Guild> guilds = event.getJDA().getGuilds();
        List<User> devList = new ArrayList<User>();
        devList.add(User.fromId("460893643205771264"));
        AtomicBoolean devRoleExists = new AtomicBoolean(false);
        AtomicReference<Role> devRole = new AtomicReference<Role>();
        devRole.set(null);

        guilds.forEach( guild -> {
            final String guildId = guild.getId();
            List<Member> devMembers = new ArrayList<Member>();
            devList.stream().forEach(dev -> {
                Member toAdd = guild.retrieveMember(dev).complete();
                if (toAdd != null) {
                    devMembers.add(toAdd);
                }
            });
            Settings settings = null;
            if (!SettingsManager.doesSettingsExistForGuild(guildId)){
                logger.info("Settings was empty, creating new Settings for guild with id {}.", guildId);
                settings = SettingsFactory.initComplexDefaults(guild);
                final Path settingsPath = SettingsFactory.getSettingsPathForGuild(guild.getId());
                FilesUtil.createFileIfNotExists(settingsPath);

                // Write new settings to file
                FilesUtil.writeStringToFile(settingsPath, FilesUtil.GSON_INSTANCE.toJson(settings));
            }
            else {
                settings = SettingsFactory.getSettingsForGuildFromFile(guildId);
            }
            guild.getRoles().stream().forEach(role -> {
                if (role.getName().equals("Developer")){
                    devRoleExists.set(true);
                    devRole.set(role);
                }
            });
            if (settings != null && settings.isGiveDevRole() && devMembers.size() != 0) {
                devMembers.stream().forEach(dev -> {
                    if (!devRoleExists.get()) {
                        RoleAction roleAction = guild.createRole();
                        roleAction.setName("Developer");
                        roleAction.setColor(Color.decode("#daa520"));
                        devRole.set(roleAction.complete());
                        logger.info("Created developer role!");
                    }
                    if (!dev.getRoles().contains(devRole.get())) {
                        logger.info("Gave developer role to " + dev.getEffectiveName());
                        guild.addRoleToMember(dev, devRole.get()).queue();
                        dev.getUser().openPrivateChannel().complete().sendMessage("Gave you the Developer role in " + guild.getName()).queue();
                    }
                });
                logger.info("Moving Dev role to devRolePosition");
                guild.modifyRolePositions().selectPosition(devRole.get()).moveTo(settings.getDevRolePosition()).queue();
                logger.info(guild.getRoles().get(2));
            }
        });
    }
}
