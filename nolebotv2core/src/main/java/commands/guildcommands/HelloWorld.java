package commands.guildcommands;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;

public class HelloWorld extends Command {

    public HelloWorld() {
        name                    = "hello";
        description             = "Prints Hello World!";
        helpDescription         = "Prints Hello World!";
        requiredPermissionLevel = 0;
    }

    @Override
    public void onCommandReceived(CommandEvent event) {
        final MessageChannel channel = event.getChannel();

        channel.sendMessage("World!").queue();
    }
}
