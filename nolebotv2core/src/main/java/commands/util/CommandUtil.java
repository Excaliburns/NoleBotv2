package commands.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandUtil extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(CommandUtil.class);
    public static final ArrayList<Command> commands = new ArrayList<>();
    public static final HashMap<String, Integer>       commandIndex = new HashMap<>();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("Initializing NoleBotv2...");

        JDA jda = event.getJDA();

        // TODO: Settings
    }

    public void addCommand(Command command) {
        String name = command.getName();

        synchronized (commandIndex) {
            commandIndex.put(name, commands.size());
            logger.info("Command registered: " + command.getName());
        }

        commands.add(command);
    }
}
