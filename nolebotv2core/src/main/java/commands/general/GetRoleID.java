package commands.general;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class GetRoleID extends Command {
    private static final Logger logger = LogManager.getLogger(GetRoleID.class);
    public GetRoleID() {
        name = "roleid";
        description = "Gets the Role ID for a role";
        helpDescription = "Gets the Role ID for a Role. Used for overriding permissions";
        requiredPermissionLevel = 1000;
        usages.add("roleid @Role");
    }
    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        List<Role> roles = event.getOriginatingJDAEvent().getMessage().getMentionedRoles();
        roles.stream().forEach((role -> {
            event.getChannel().sendMessage(role.getId()).queue();
        }));
    }
}
