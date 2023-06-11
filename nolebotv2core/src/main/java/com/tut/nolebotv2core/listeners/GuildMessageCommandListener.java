package com.tut.nolebotv2core.listeners;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class GuildMessageCommandListener extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(GuildMessageCommandListener.class);


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
    }
    /**
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {


        List<String> commandMessage;
        final String message = event.getMessage().getContentRaw();
        final Settings settings = SettingsFactory.getSettings(event.getGuild());

        if (message.startsWith(settings.getPrefix())) {
            // remove prefix
            final String noPrefixMessage = message.substring(settings.getPrefix().length());
            commandMessage = Arrays.asList(noPrefixMessage.split("\\s"));

            // after getting the command name, remove it.
            final String commandName = commandMessage.get(0);
            final Command command;

            synchronized (CommandUtil.commandIndex) {
                int i = CommandUtil.commandIndex.getOrDefault(commandName, -1);
                command = i != -1 ? CommandUtil.commands.get(i) : null;
            }


            if (command != null) {
                final CommandEvent commandEvent = new CommandEvent(
                        event,
                        noPrefixMessage,
                        commandMessage,
                        settings,
                        command
                );

                logger.info("commandEvent executed: Command [{}] executed by [{}] in Guild [{}] - [{}]",
                        command::getName,
                        () -> event.getAuthor().getAsTag(),
                        () -> event.getGuild().getName(),
                        () -> event.getGuild().getId()
                );

                try {
                    command.executeCommand(commandEvent);
                }
                catch (Exception e) {
                    commandEvent.sendErrorResponseToOriginatingChannel(
                            "There was an exception while processing your request!",
                            "Check the logs and message the bot author if this seems unexpected."
                    );

                    commandEvent.printStackTraceToChannelFromThrowable(event.getChannel(), e);
                }
            }
        }
    }
    **/
}
