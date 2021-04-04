package commands.guildcommands.guilds;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class UnlockRole extends Command {
    public UnlockRole() {
        name = "unlockrole";
        description = "Unlocks a role, so it can be assigned with the addrole command";
        helpDescription = "Unlocks a role, so it can be assigned with the addrole command";
        usages.add("unlockrole <List of @Role>");
        requiredPermissionLevel = 1000;
    }
    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        final List<Role> mentionedRoles = event.getOriginatingJDAEvent().getMessage().getMentionedRoles();
        final StringBuilder builder = new StringBuilder();
        if (mentionedRoles.size() > 0) {
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
