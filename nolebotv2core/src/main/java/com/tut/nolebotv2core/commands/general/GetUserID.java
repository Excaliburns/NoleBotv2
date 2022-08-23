package com.tut.nolebotv2core.commands.general;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class GetUserID extends Command {
    /**
     * Creates an instance of the userid command.
     */
    public GetUserID() {
        name = "userid";
        description = "Gets the User ID for a member";
        helpDescription = "Gets the User ID for a member. Used for literally nothing";
        requiredPermissionLevel = 0;
        usages.add("userid <List of @User>");
    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        final List<User> mentionedUsers = event.getOriginatingJDAEvent().getMessage().getMentionedUsers();
        mentionedUsers.forEach(user -> event.getChannel().sendMessage(user.getId()).queue());
    }
}
