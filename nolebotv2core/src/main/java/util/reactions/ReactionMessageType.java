package util.reactions;


import commands.util.CommandUtil;
import commands.util.ReactionCommand;

// This is probably wrong... I want to get the instance of the class that's in CommandUtil by passing the class or something.
// Probably need another map or something. IDK. This works for now.
// Yeah probably another map that maps names to ReactionCommands.
// Since we know which Commands in the CommandUtil map are ReactionCommands, this will continue working though
// If we want to put in error checking we could just check if the commands in this enum are instances of Reaction command
@SuppressWarnings("OptionalGetWithoutIsPresent")
public enum ReactionMessageType {
    ATTENDANCE_COMMAND((ReactionCommand) CommandUtil.getCommandFromMap("attendance").get()),
    HELP_COMMAND((ReactionCommand) CommandUtil.getCommandFromMap("help").get());

    public ReactionCommand command;

    ReactionMessageType(ReactionCommand command) {
        this.command = command;
    }
}
