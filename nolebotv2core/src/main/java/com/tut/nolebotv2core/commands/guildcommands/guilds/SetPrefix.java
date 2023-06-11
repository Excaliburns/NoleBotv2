package com.tut.nolebotv2core.commands.guildcommands.guilds;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import com.tut.nolebotv2core.enums.EmojiCodes;
import com.tut.nolebotv2core.util.settings.SettingsCache;

import java.util.Collections;

public class SetPrefix extends Command {
    /**
     * Default Constructor.
     */
    public SetPrefix() {
        name                    = "setprefix";
        description             = "Set the prefix used by NoleBot for your Guild";
        helpDescription         = "Set the prefix used by NoleBot for your Guild. " +
                                  "This can be any combination of characters, up to 5 characters in length.";
        requiredPermissionLevel = 1000;
        setUsages(Collections.singletonList("setprefix <prefix>"));
    }

    @Override
    public void executeCommand(CommandEvent event) {
        final String oldPrefix       = event.getSettings().getPrefix();
        final String newPrefix       = event.getMessageContent().get(1);
        if (oldPrefix.equals(newPrefix)) {
            event.sendErrorResponseToOriginatingChannel(EmojiCodes.THINKING + " Those prefixes are the same!");
        }
        else if (newPrefix.length() > 5) {
            event.sendErrorResponseToOriginatingChannel("Prefix length is restricted to 5 characters.");
        }
        else {
            event.getSettings().setPrefix(newPrefix);
            SettingsCache.saveSettingsForGuild(event.getGuild(), event.getSettings());

            event.sendSuccessResponseToOriginatingChannel("Successfully set new prefix to **" + newPrefix + "**");
        }
    }
}
