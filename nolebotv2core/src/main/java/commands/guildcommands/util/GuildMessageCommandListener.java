package commands.guildcommands.util;

import commands.util.Command;
import commands.util.CommandUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import util.settings.Settings;
import util.settings.SettingsCache;

import java.util.Arrays;
import java.util.List;

@Getter @Setter
public class GuildMessageCommandListener extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(GuildMessageCommandListener.class);

    /**
     * @param event
     * Author: Kevin Patlis
     */
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        List<String> commandMessage;
        String message = event.getMessage().getContentRaw();
        final Settings settings = SettingsCache.getSettings(event.getGuild().getId());

        // TODO: SETTINGS PREFIX
        if (message.startsWith(settings.getPrefix())) {
            // remove prefix
            message = message.substring(1);
            commandMessage = Arrays.asList(message.split("\\s"));

            final String commandName = commandMessage.get(0);
            final Command command;

            synchronized (CommandUtil.commandIndex) {
                int i = CommandUtil.commandIndex.getOrDefault(commandName, -1);
                command = i != -1 ? CommandUtil.commands.get(i) : null;
            }


            if (command != null) {
                // TODO Permissions
                GuildMessageCommandEvent commandEvent = new GuildMessageCommandEvent(event, commandMessage, settings);


                logger.info("commandEvent executed: Command [{}] executed by [{}] in Guild [{}] - [{}]",
                        command.getName(),
                        event.getAuthor().getAsTag(),
                        event.getGuild().getName(),
                        event.getGuild().getId());

                command.executeCommand(commandEvent);
            }

        }
    }
}
