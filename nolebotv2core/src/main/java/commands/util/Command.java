package commands.util;

import lombok.Getter;
import lombok.Setter;

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

    public final void executeCommand(CommandEvent event) throws Exception {
        onCommandReceived(event);
    }
}
