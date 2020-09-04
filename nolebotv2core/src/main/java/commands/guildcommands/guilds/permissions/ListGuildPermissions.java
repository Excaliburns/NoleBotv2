package commands.guildcommands.guilds.permissions;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import util.permissions.GenericPermission;

import java.util.List;
import java.util.TreeSet;

public class ListGuildPermissions extends Command {
    public ListGuildPermissions() {
        this.description             = "Show the roles that have permission on your server";
        this.helpDescription         = "Shows the roles and users on your server that have a permission level, and their levels.";
        this.name                    = "listguildpermissions";
        this.requiredPermissionLevel = 1000;
    }

    @Override
    public void onCommandReceived(CommandEvent event) {
        final TreeSet<GenericPermission> permissionList  = event.getSettings().getPermissionList();

        MessageBuilder builder = new MessageBuilder();
        builder.append("\u2139 Your guild has assigned these roles the following permission levels: \u2139\n");

        for (GenericPermission permission : permissionList) {
            switch (permission.getType()) {
                case ROLE -> builder.append("Role");
                case USER -> builder.append("User");
                case GROUP -> builder.append("Group");
                default -> builder.append("UNKNOWN");
            }

            builder.appendFormat(" **%s** has permission level \u2014 %s", permission.getName(), permission.getPermissionLevel());
            builder.append("\n");
        }

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
