package com.tut.nolebotv2core.commands.guildcommands.guilds.roles;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import com.tut.nolebotv2core.util.NoleBotUtil;
import com.tut.nolebotv2core.util.permissions.GenericPermission;
import com.tut.nolebotv2core.util.settings.Settings;
import com.tut.nolebotv2core.util.settings.SettingsCache;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AddAssignableRole extends Command {
    // Todo: lang
    final String permissionLevelValidText =
            "Could not find a valid permission level. The permission level must not exceed your own.";
    final String cantMakeHigherPermissionLower =
            "You cannot assign a lower permission to a command that has a higher permission level than you.";

    /**
     * Constructor.
     */
    public AddAssignableRole() {
        this.name = "addassignablerole";
        this.description = "Marks a role from your server as assignable";
        this.helpDescription = "Marks a role from your server as assignable by the add role command.";
        this.requiredPermissionLevel = 1000;
        this.usages.add("addassignablerole <List of @Role> <permission level>");
    }

    @Override
    public void registerCommand(JDA jda) {
        jda.upsertCommand(
                Commands.slash(name, description)
                        .addOption(OptionType.ROLE, "role", "The role that should be assignable")
                        .addOption(OptionType.INTEGER, "permission", "The permission required to assign the role")
        ).queue();
    }

    @Override
    public void executeCommand(final CommandEvent event) throws Exception {
        final Settings settings = event.getSettings();
        final TreeSet<GenericPermission> permissionList = new TreeSet<>(settings.getPermissionList());
        final List<Role> roles = event.getMentionedRoles();
        final int userInitiatedPermissionLevel = event.getUserInitiatedPermissionLevel();
        final String rawMessageContent = event.getRawMessageContent();
        final String messageContentMinusName = rawMessageContent.substring(this.name.length());

        final Optional<Integer> permissionLevel = getPermissionLevelFromInputMessage(roles, messageContentMinusName);

        if (event.getMentionedRoles().size() == 0) {
            event.sendErrorResponseToOriginatingChannel("You did not mention any roles.");
            return;
        }
        if (permissionLevel.isEmpty()) {
            event.sendErrorResponseToOriginatingChannel("Can't parse entered permission level.");
            return;
        }

        final int permissionLevelParsed = permissionLevel.get();

        if (permissionLevelParsed > userInitiatedPermissionLevel) {
            event.sendErrorResponseToOriginatingChannel("Your permission level is too low.");
            return;
        }

        final MessageBuilder messageBuilder = new MessageBuilder();

        for (final Role role : event.getMentionedRoles()) {
            final GenericPermission genericPermission = new GenericPermission(role, permissionLevelParsed);

            final List<GenericPermission> potentialMatches = permissionList
                    .stream()
                    .filter(permission -> permission.getSnowflakeId().equals(genericPermission.getSnowflakeId()))
                    .collect(Collectors.toList());

            if (messageBuilder.length() != 0) {
                messageBuilder.append("\n");
            }

            if (!potentialMatches.isEmpty()) {
                final Predicate<? super GenericPermission> checkUserCanCreate =
                        match -> match.getPermissionLevel() > event.getUserInitiatedPermissionLevel();

                if (potentialMatches.stream().anyMatch(checkUserCanCreate)) {
                    event.sendErrorResponseToOriginatingChannel(cantMakeHigherPermissionLower);
                    continue;
                }

                messageBuilder.append("Found existing assignable role for role [**")
                        .append(role.getName())
                        .append("]** with permission level(s) **")
                        .append(NoleBotUtil.getFormattedStringFromList(
                                potentialMatches
                                        .stream()
                                        .map(GenericPermission::getPermissionLevel).collect(Collectors.toList())
                        ))
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

    private Optional<Integer> getPermissionLevelFromInputMessage(final List<Role> roleList, final String rawMessage) {
        String permissionLevelString = rawMessage;

        for (final Role role : roleList) {
            permissionLevelString = permissionLevelString.replace(role.getAsMention(), "");
        }
        permissionLevelString = permissionLevelString.trim();

        try {
            return Optional.of(Integer.parseInt(permissionLevelString));
        }
        catch (final Exception unused) {
            return Optional.empty();
        }
    }
}
