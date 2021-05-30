package commands.guildcommands.guilds.roles;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Role;
import util.NoleBotUtil;
import util.permissions.GenericPermission;
import util.settings.Settings;
import util.settings.SettingsCache;
import util.settings.SettingsManager;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AddAssignableRole extends Command {
    public AddAssignableRole() {
        this.name = "addassignablerole";
        this.description = "Marks a role from your server as assignable";
        this.helpDescription = "Marks a role from your server as assignable by the add role command.";
        this.requiredPermissionLevel = 1000;
        this.usages.add("addassignablerole <List of @Role> <permission level>");
    }

    @Override
    public void onCommandReceived(final CommandEvent event) throws Exception {
        final Settings settings = event.getSettings();
        final TreeSet<GenericPermission> permissionList = settings.getPermissionList();
        int permissionLevel;

        if (event.getMentionedRoles().size() == 0) {
            event.sendErrorResponseToOriginatingChannel("You did not mention any roles.");
            return;
        }

        try {
            permissionLevel = getPermissionLevelFromInputMessage(event.getMentionedRoles(), event.getRawMessageContent().substring(this.name.length()));
            if (permissionLevel > event.getUserInitiatedPermissionLevel()) { throw new Exception(); }
        }
        catch (final Exception _e) {
            event.sendErrorResponseToOriginatingChannel("Could not find a valid permission level. The permission level must not exceed your own.");
            return;
        }

        final MessageBuilder messageBuilder = new MessageBuilder();

        for (final Role role : event.getMentionedRoles()) {
            final GenericPermission genericPermission = new GenericPermission(role, permissionLevel);

            final List<GenericPermission> potentialMatches = permissionList
                    .stream()
                    .filter(permission -> permission.getSnowflakeId().equals(genericPermission.getSnowflakeId()))
                    .collect(Collectors.toList());

            if (messageBuilder.length() != 0) {
                messageBuilder.append("\n");
            }
            if (potentialMatches.size() > 0) {
                if (potentialMatches.stream().anyMatch( match -> match.getPermissionLevel() > event.getUserInitiatedPermissionLevel())) {
                    event.sendErrorResponseToOriginatingChannel("You cannot assign a lower permission to a command that has a higher permission level than you.");
                    continue;
                }

                messageBuilder.append("Found existing assignable role for role [**")
                              .append(role.getName())
                              .append("]** with permission level(s) **")
                              .append(NoleBotUtil.getFormattedStringFromList(potentialMatches.stream().map(GenericPermission::getPermissionLevel).collect(Collectors.toList())))
                              .append("**. Updating this to ")
                              .append(permissionLevel);
                potentialMatches.forEach(permissionList::remove);
            }
            else {
                messageBuilder.append("Added permission level **")
                              .append(permissionLevel)
                              .append("** for role [**")
                              .append(role.getName())
                              .append("**].");
            }

            permissionList.add(genericPermission);
        }

        event.sendMessageToOriginatingChannel(messageBuilder.buildAll(MessageBuilder.SplitPolicy.NEWLINE));
        settings.setPermissionList(permissionList);
        SettingsCache.saveSettingsForGuild(event.getGuild(), settings);
    }

    private int getPermissionLevelFromInputMessage(final List<Role> roleList, final String rawMessage) {
        String permissionLevelString = rawMessage;

        for (final Role role : roleList) {
            permissionLevelString = permissionLevelString.replace(role.getAsMention(), "");
        }
        permissionLevelString = permissionLevelString.trim();

        return Integer.parseInt(permissionLevelString);
    }
}
