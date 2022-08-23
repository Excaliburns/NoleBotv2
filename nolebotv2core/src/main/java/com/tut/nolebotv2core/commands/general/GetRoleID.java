package com.tut.nolebotv2core.commands.general;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class GetRoleID extends Command {
    /**
     * Creates an instance of the roleid command.
     */
    public GetRoleID() {
        name = "roleid";
        description = "Gets the Role ID for a role";
        helpDescription = "Gets the Role ID for a Role. Used for overriding permissions";
        requiredPermissionLevel = 0;
        usages.add("roleid <List of @Role>");
    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        List<Role> mentionedRoles = event.getOriginatingJDAEvent().getMessage().getMentionedRoles();
        mentionedRoles.forEach((role -> event.getChannel().sendMessage(role.getId()).queue()));
    }
}
