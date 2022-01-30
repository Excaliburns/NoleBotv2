package commands.util;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class CommandUtil extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(CommandUtil.class);

    public static final ArrayList<Command>        commands     = new ArrayList<>();
    public static final HashMap<String, Integer>  commandIndex = new HashMap<>();

    /**
    * Adds a command to the command list.
    *
    * @param command the command to add
    */
    public void addCommand(final Command command) {
        String name = command.getName();

        synchronized (commandIndex) {
            commandIndex.put(name, commands.size());
            logger.info("Command registered: " + command.getName());
        }

        commands.add(command);
    }

    /**
     * Gets a command object from the map given the name of the command.
     * Should be equal to the command's classname in Java.
     *
     * @param commandName Name of the command.
     * @return The command, if it exists. Otherwise, an empty optional.
     */
    public static Optional<Command> getCommandFromMap(final String commandName) {
        if (!commandIndex.containsKey(commandName)) {
            return Optional.empty();
        }

        return Optional.of(commands.get(commandIndex.get(commandName)));
    }
}
