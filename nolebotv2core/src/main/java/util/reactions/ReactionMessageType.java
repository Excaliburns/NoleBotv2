package util.reactions;

import commands.general.Help;
import commands.util.ReactionCommand;

public enum ReactionMessageType {
    HELP_COMMAND(new Help());

    public ReactionCommand command;

    ReactionMessageType(ReactionCommand command) {
        this.command = command;
    }
}
