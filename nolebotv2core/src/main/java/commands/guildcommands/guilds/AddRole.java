package commands.guildcommands.guilds;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.Role;
import util.settings.Settings;

import java.util.List;

public class AddRole extends Command {
    public AddRole()
    {
        name = "addrole";
        description = "Assigns a role to a user";
        helpDescription = "Assigns a pingable role to a given user";
        usages.add("roleadd @Role @User");
        usages.add("roleadd @Role @User @User @User..@User");
        requiredPermissionLevel = 1000;
    }

    @Override
    public boolean doesUserHavePermission(CommandEvent event) {
        Settings s = event.getSettings();
        boolean result = false;
        String roleIDToAssign = event.getOriginatingJDAEvent().getMessage().getMentionedRoles().get(0).getId();
        if (s.isOverrideRolePerms())
        {
            if (s.getRoleOverrides().containsKey(roleIDToAssign))
            {
                List<String> rolesThatCanAssign = s.getRoleOverrides().get(roleIDToAssign);
                List<Role> authorRoles = event.getOriginatingJDAEvent().getMember().getRoles();
                for (int i = 0; i < authorRoles.size() && !result; i++)
                {
                    Role roleToCheck = authorRoles.get(i);
                    if (rolesThatCanAssign.contains(roleToCheck.getId()))
                    {
                        result = true;
                    }
                }
                if (!result) {
                    event.sendErrorResponseToOriginatingChannel("You don't have permission to send this command!");
                }
            }
        }
        else
        {
            result = super.doesUserHavePermission(event);
        }
        return result;
    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        if (event.getOriginatingJDAEvent().getMessage().getMentionedRoles().size() > 1)
        {
            event.sendErrorResponseToOriginatingChannel("Please only mention 1 role!");
            throw new IllegalStateException();
        }
        //Gets the mentioned members, then loops through and adds the role mentioned
        event.getOriginatingJDAEvent().getMessage().getMentionedMembers().stream().forEach((member -> {
            event.getGuild().addRoleToMember(member, event.getOriginatingJDAEvent().getMessage().getMentionedRoles().get(0)).queue();
        }));
        event.sendSuccessResponseToOriginatingChannel("Role successfully added!");
    }
}
