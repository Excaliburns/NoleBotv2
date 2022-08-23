package com.tut.nolebotv2core.commands.general;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class GetUser extends Command {
    /**
     * Creates an instance of the user command.
     */
    public GetUser() {
        name = "user";
        description = "Gets the information for a member";
        helpDescription = "Gets the information for a member. Used for literally nothing";
        requiredPermissionLevel = 0;
        usages.add("user <List of @User>");
    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        final List<User> users = event.getOriginatingJDAEvent().getMessage().getMentionedUsers();
        users.forEach(user -> event.getChannel().sendMessage(user.getId()).queue());
    }
}
