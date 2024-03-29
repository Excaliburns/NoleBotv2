package com.tut.nolebotv2core.commands.guildcommands.guilds.roles;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import com.tut.nolebotv2core.util.permissions.GenericPermission;
import com.tut.nolebotv2core.util.permissions.PermissionType;
import com.tut.nolebotv2core.util.settings.Settings;
import com.tut.nolebotv2core.util.settings.SettingsCache;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class GivePerms extends Command {
    /**
     * Default Constructor.
     */
    public GivePerms() {
        name = "giveperms";
        description = "Assigns a permission level to an entity";
        helpDescription = "Assigns a permission level to a role or user";
        usages.add("giveperms <List of @Roles> <List of @Users> <Number of Permission Level>");
        requiredPermissionLevel = 1000;
    }

    //TODO: Check if role/user already has a lower permission, delete if so.
    @Override
    public void onCommandReceived(final CommandEvent event) throws Exception {
        final List<Role> rolesMentioned = event.getOriginatingJDAEvent().getMessage().getMentionedRoles();
        final List<Member> membersMentioned = event.getOriginatingJDAEvent().getMessage().getMentionedMembers();
        final List<String> message = event.getMessageContent();
        final int permLevel = Integer.parseInt(message.get(message.size() - 1));
        if (rolesMentioned.isEmpty() && membersMentioned.isEmpty()) {
            event.sendErrorResponseToOriginatingChannel("Please mention at least one member or role!");
        }
        else {
            Settings eventSettings = event.getSettings();
            rolesMentioned.forEach(role -> {
                final GenericPermission permToAdd = new GenericPermission(
                        PermissionType.ROLE,
                        role.getName(),
                        role.getId(),
                        permLevel
                );
                eventSettings.addPermission(permToAdd);
                SettingsCache.saveSettingsForGuild(event.getGuild(), eventSettings);
            });
            membersMentioned.forEach(member -> {
                final GenericPermission permToAdd = new GenericPermission(
                        PermissionType.USER,
                        member.getEffectiveName(),
                        member.getId(),
                        permLevel
                );
                eventSettings.addPermission(permToAdd);
                SettingsCache.saveSettingsForGuild(event.getGuild(), eventSettings);
            });
            sendSuccessMessageAfterSettingPerms(rolesMentioned, membersMentioned, permLevel, event);
        }
    }

    private void sendSuccessMessageAfterSettingPerms(
            final List<Role> addedRoles,
            final List<Member> addedMembers,
            final int permLevel,
            final CommandEvent event
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Successfully added permLevel [%s] to :\n", permLevel));
        builder.append("Roles: \n");
        addedRoles.forEach(role -> {
            builder.append(role.getName());
            builder.append('\n');
        });
        builder.append("Members: \n");
        addedMembers.forEach(member -> {
            builder.append(member.getEffectiveName());
            builder.append('\n');
        });
        event.sendSuccessResponseToOriginatingChannel(builder.toString());
    }
}
