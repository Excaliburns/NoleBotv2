package commands.guildcommands.guilds.roles;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import util.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AddRole extends Command {
    /**
     * Constructor.
     */
    public AddRole() {
        name = "addrole";
        description = "Assigns a role to a user";
        helpDescription = "Assigns a pingable role to a given user";
        usages.add("roleadd <List of @Role> <List of @User>");
        requiredPermissionLevel = 1000;
    }

    @Override
    public boolean doesUserHavePermission(CommandEvent event) {
        final Settings guildSettings = event.getSettings();

        final List<Role> mentionedRolesList = event.getOriginatingJDAEvent().getMessage().getMentionedRoles();
        final boolean guildHasRoleOverrides = !guildSettings.getRoleOverrides().isEmpty();
        final HashMap<String, List<String>> guildOverrides = guildSettings.getRoleOverrides();

        if (event.getOriginatingJDAEvent().getMember() == null) {
            throw new NullPointerException("Couldn't find user executing command in guild!");
        }

        final List<String> authorsRoleIds = event.getOriginatingJDAEvent().getMember().getRoles()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toList());

        for (final Role mentionedRole : mentionedRolesList) {
            if (guildHasRoleOverrides) {
                if (guildOverrides.containsKey(mentionedRole.getId())) {
                    final List<String> roleIdsToCheck = guildOverrides.get(mentionedRole.getId());

                    for (final String roleId : roleIdsToCheck) {
                        if (authorsRoleIds.contains(roleId)) {
                            return true;
                        }
                    }

                    return false;
                }
                else {
                    return super.doesUserHavePermission(event);
                }
            }
            else {
                return super.doesUserHavePermission(event);
            }
        }

        return false;
    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        final List<Role> mentionedRoles = event.getOriginatingJDAEvent().getMessage().getMentionedRoles();
        final List<Member> mentionedMembers = event.getOriginatingJDAEvent().getMessage().getMentionedMembers();
        final int mentionedRolesSize = mentionedRoles.size();
        final int mentionedMembersSize = mentionedMembers.size();
        if (mentionedRolesSize > 0 && mentionedMembersSize > 0) {
            HashSet<Role> addedRoles = new HashSet<Role>();
            HashSet<Member> addedMembers = new HashSet<Member>();
            //Gets the mentioned roles, then loops through and adds the members mentioned
            for (final Role role : mentionedRoles) {
                if (!event.getSettings().getLockedRoles().contains(role.getId())) {
                    for (final Member member : mentionedMembers) {
                        event.getGuild().addRoleToMember(member, role).queue();
                        addedMembers.add(member);
                    }
                    addedRoles.add(role);
                }
                else {
                    event.sendErrorResponseToOriginatingChannel(
                            String.format("Role [%s] is locked, and cannot be assigned", role.getName())
                    );
                }
            }
            sendSuccessMessageAfterAddingRoles(addedMembers, addedRoles, event);
        }
        else {
            if (mentionedMembersSize == 0) {
                event.sendErrorResponseToOriginatingChannel("Please mention at least one person!");
            }
            if (mentionedRolesSize == 0) {
                event.sendErrorResponseToOriginatingChannel("Please mention at least one role!");
            }
        }
    }

    private void sendSuccessMessageAfterAddingRoles(
            final Set<Member> memberList,
            final Set<Role> rolesList,
            final CommandEvent event
    ) {
        final StringBuilder successMessageBeforeMember = new StringBuilder();
        successMessageBeforeMember.append("Successfully added ");

        successMessageBeforeMember.append("(");
        rolesList.forEach(role -> successMessageBeforeMember.append(String.format("[%s], ", role.getName())));
        successMessageBeforeMember.append(")");

        List<String> successMessages = new ArrayList<>();
        memberList.forEach(
                member -> successMessages.add(successMessageBeforeMember + " to " + member.getEffectiveName())
        );

        event.sendSuccessResponseToOriginatingChannel(successMessages);
    }
}
