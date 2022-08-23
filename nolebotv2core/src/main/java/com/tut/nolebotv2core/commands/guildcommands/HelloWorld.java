package com.tut.nolebotv2core.commands.guildcommands;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.MessageChannel;

public class HelloWorld extends Command {

    /**
     * Default Constructor.
     */
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
