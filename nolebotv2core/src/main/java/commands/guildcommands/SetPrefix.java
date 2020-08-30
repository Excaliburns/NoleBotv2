package commands.guildcommands;

import commands.util.Command;
import commands.util.CommandEvent;
import util.settings.SettingsCache;

import java.util.Collections;

public class SetPrefix extends Command {
    public SetPrefix() {
        name                    = "setprefix";
        description             = "Set the prefix used by NoleBot for your Guild";
        helpDescription         = "Set the prefix used by NoleBot for your Guild. This can be any combination of characters, up to 5 characters in length.";
        requiredPermissionLevel = 1000;
        setUsages(Collections.singletonList("setprefix <prefix>"));
    }

    @Override
    public void onCommandReceived(CommandEvent event) {
        final String oldPrefix       = event.getSettings().getPrefix();
        final String newPrefix       = event.getMessageContent().get(1);

        if (oldPrefix.equals(newPrefix)) {
            event.sendErrorResponseToOriginatingChannel("\uD83E\uDD14 Those prefixes are the same!");
        }
        else if (newPrefix.length() > 5) {
            event.sendErrorResponseToOriginatingChannel("Prefix length is restricted to 5 characters.");
        }
        else {
            event.getSettings().setPrefix(newPrefix);
            SettingsCache.saveSettingsForGuild(event.getGuildId(), event.getSettings());

            event.sendSuccessResponseToOriginatingChannel("Successfully set new prefix to **" + newPrefix + "**");
        }
    }
}
