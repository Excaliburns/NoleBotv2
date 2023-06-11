package com.tut.nolebotv2core.commands.guildcommands.guilds.roles;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class UnlockRole extends Command {
    /**
     * Default Constructor.
     */
    public UnlockRole() {
        name = "unlockrole";
        description = "Unlocks a role, so it can be assigned with the addrole command";
        helpDescription = "Unlocks a role, so it can be assigned with the addrole command";
        usages.add("unlockrole <List of @Role>");
        requiredPermissionLevel = 1000;
    }

    @Override
    public void executeCommand(CommandEvent event) throws Exception {
        final List<Role> mentionedRoles = event.getOriginatingJDAEvent().getMessage().getMentionedRoles();
        final StringBuilder builder = new StringBuilder();
        if (!mentionedRoles.isEmpty()) {
            mentionedRoles.forEach(role -> {
                event.getSettings().unlockRole(role.getId());
                builder.append(String.format("Role [%s] unlocked\n", role.getName()));
            });
            event.sendSuccessResponseToOriginatingChannel(builder.toString());
        }
        else {
            event.sendErrorResponseToOriginatingChannel("Please mention at least one role!");
        }
    }
}
