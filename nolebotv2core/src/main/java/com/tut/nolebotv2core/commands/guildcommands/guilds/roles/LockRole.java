package com.tut.nolebotv2core.commands.guildcommands.guilds.roles;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import com.tut.nolebotv2core.util.settings.SettingsCache;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class LockRole extends Command {
    /**
     * Default Constructor.
     */
    public LockRole() {
        name = "lockrole";
        description = "Locks a role, so it cant be assigned with the addrole command";
        helpDescription = "Locks a role, so it cant be assigned with the addrole command";
        usages.add("lockrole <List of @Role>");
        requiredPermissionLevel = 1000;
    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        final List<Role> mentionedRoles = event.getOriginatingJDAEvent().getMessage().getMentionedRoles();
        final StringBuilder builder = new StringBuilder();
        if (!mentionedRoles.isEmpty()) {
            mentionedRoles.forEach(role -> {
                event.getSettings().lockRole(role.getId());
                builder.append(String.format("[%s] added to locked roles\n", role.getName()));
            });
            SettingsCache.saveSettingsForGuild(event.getGuild(), event.getSettings());
            event.sendSuccessResponseToOriginatingChannel(builder.toString());
        }
        else {
            event.sendErrorResponseToOriginatingChannel("Please mention at least one role!");
        }
    }
}
