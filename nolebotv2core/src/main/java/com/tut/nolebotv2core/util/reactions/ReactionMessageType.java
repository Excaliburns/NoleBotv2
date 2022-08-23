package com.tut.nolebotv2core.util.reactions;


import com.tut.nolebotv2core.commands.util.CommandUtil;
import com.tut.nolebotv2core.commands.util.ReactionCommand;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public enum ReactionMessageType {
    ATTENDANCE_COMMAND((ReactionCommand) CommandUtil.getCommandFromMap("attendance").get()),
    HELP_COMMAND((ReactionCommand) CommandUtil.getCommandFromMap("help").get()),
    PERMISSION_COMMAND((ReactionCommand) CommandUtil.getCommandFromMap("listguildpermissions").get());

    public ReactionCommand command;

    ReactionMessageType(ReactionCommand command) {
        this.command = command;
    }
}
