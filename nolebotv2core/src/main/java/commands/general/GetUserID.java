package commands.general;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class GetUserID extends Command {
    private static final Logger logger = LogManager.getLogger(GetRoleID.class);
    public GetUserID() {
        name = "userid";
        description = "Gets the User ID for a member";
        helpDescription = "Gets the User ID for a member. Used for literally nothing";
        requiredPermissionLevel = 1000;
        usages.add("user @User");
    }
    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        List<User> users = event.getOriginatingJDAEvent().getMessage().getMentionedUsers();
        users.stream().forEach((user -> {
            event.getChannel().sendMessage(user.getId()).queue();
        }));
    }
}
