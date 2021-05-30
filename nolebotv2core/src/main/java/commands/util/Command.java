package commands.util;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
Command Message Content:
index 0: command / alias
index 1-n: space separated list of words
 */
@Getter
@Setter
public abstract class Command {
    protected String name;
    protected String description = "No data available";
    protected String helpDescription = "No data available";
    protected List<String> usages = new ArrayList<>();
    protected int requiredPermissionLevel = 1000;

    // Default constructor uses reflection to assign name.
    public Command() {
        if (this.getClass() != null) {
            this.name = this.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        }
        else {
            throw new RuntimeException("Can't instantiate a Command object with no name!");
        }
    }

    public abstract void onCommandReceived(CommandEvent event) throws Exception;

    public boolean doesUserHavePermission(final CommandEvent event) {
        return event.getUserInitiatedPermissionLevel() >= event.getCommand().getRequiredPermissionLevel();
    }

    public final void executeCommand(CommandEvent event) throws Exception {
        if (doesUserHavePermission(event)) {
            onCommandReceived(event);
        }
        else {
            event.sendErrorResponseToOriginatingChannel("You don't have the required permission level to execute this command!",
                    String.format("Your permission level: [%s]"    , event.getUserInitiatedPermissionLevel()),
                    String.format("Required permission level: [%s]", event.getCommand().getRequiredPermissionLevel()));
        }
    }
}
