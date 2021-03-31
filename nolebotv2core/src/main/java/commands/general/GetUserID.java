package commands.general;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class GetUserID extends Command {
    public GetUserID() {
        name = "userid";
        description = "Gets the User ID for a member";
        helpDescription = "Gets the User ID for a member. Used for literally nothing";
        requiredPermissionLevel = 0;
        usages.add("userid <List of @User>");
    }
    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        final List<User> users = event.getOriginatingJDAEvent().getMessage().getMentionedUsers();
        users.forEach(user -> event.getChannel().sendMessage(user.getId()).queue());
    }
}
