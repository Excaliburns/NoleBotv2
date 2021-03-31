package commands.util;

import lombok.Getter;
import lombok.Setter;
import util.permissions.PermissionCache;

import java.util.ArrayList;
import java.util.List;

/*
Command Message Content:
index 0: command / alias
index 1-n: space separated list of words
 */
@Getter
@Setter
public abstract class Command {
    protected String name = "";
    protected String description = "No data available";
    protected String helpDescription = "No data available";
    protected List<String> usages = new ArrayList<>();
    protected int requiredPermissionLevel = 1000;

    public abstract void onCommandReceived(CommandEvent event) throws Exception;
    public boolean doesUserHavePermission(CommandEvent event) {
        int permLevelOfUser = PermissionCache.getPermissionForUser(
                event.getOriginatingJDAEvent().getAuthor().getId(),
                event.getGuild())
                .getPermissionLevel();
        return permLevelOfUser >= event.getCommand().getRequiredPermissionLevel();
    }
    public final void executeCommand(CommandEvent event) throws Exception {
        if (doesUserHavePermission(event))
        {
            onCommandReceived(event);
        }
        else
        {
            event.sendErrorResponseToOriginatingChannel("You don't have permission to excecute that command!");
        }
    }
}
