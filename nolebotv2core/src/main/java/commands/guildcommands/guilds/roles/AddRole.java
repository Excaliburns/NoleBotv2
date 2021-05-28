package commands.guildcommands.guilds.roles;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import util.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AddRole extends Command {
    public AddRole()
    {
        name = "addrole";
        description = "Assigns a role to a user";
        helpDescription = "Adds an assignable role to a user. You can either mention a list of users and roles that will be assigned to those users, or the bot can attempt" +
                          "to search for the roles and users that you want to add. You can do this by prefixing the roles with R$ and the users with U$. If you choose to use this method, " +
                          "the bot will ask for confirmation before assigning everyone their roles. You can also mix mentions and this method.";
        usages.add("addrole <List of @Role Mentions> <List of @User mentions>");
        usages.add("addrole <List of names of roles prefixed by R$> <List of names of users prefixed by U$>");
        examples.add("!addrole @Tut @Admin (pretend these are @mentions)");
        examples.add("!addrole U$Tut R$Admin");
        requiredPermissionLevel = 1000;
    }

    @Override
    public boolean doesUserHavePermission(CommandEvent event) {
        final Settings guildSettings = event.getSettings();

        final List<Role> mentionedRolesList = event.getMentionedRoles();
        final boolean guildHasRoleOverrides = !guildSettings.getRoleOverrides().isEmpty();
        final HashMap<String, List<String>> guildOverrides = guildSettings.getRoleOverrides();
        final Member executingMember = event.getOriginatingJDAEvent().getMember();

        getListOfRolesFromMessage(event);

        if (executingMember == null ) { throw new NullPointerException("Couldn't find user executing command in guild!"); }

        final List<String> authorsRoleIds = executingMember.getRoles()
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

        //Gets the mentioned members, then loops through and adds the role mentioned
        for (final Member member : mentionedMembers) {
            for (final Role role : mentionedRoles) {
                event.getGuild().addRoleToMember(member, role).queue();
            }
        }

        sendSuccessMessageAfterAddingRoles(mentionedMembers, mentionedRoles, event);
    }

    private void sendSuccessMessageAfterAddingRoles(final List<Member> memberList, final List<Role> rolesList, final CommandEvent event) {
        final StringBuilder successMessageBeforeMember = new StringBuilder();
        successMessageBeforeMember.append("Successfully added ");

        successMessageBeforeMember.append("(");
        rolesList.forEach( role -> successMessageBeforeMember.append(String.format("[%s], ", role.getName())));
        successMessageBeforeMember.append(")");

        List<String> successMessages = new ArrayList<>();
        memberList.forEach( member -> successMessages.add(successMessageBeforeMember.toString() + " to " + member.getEffectiveName()));

        event.sendSuccessResponseToOriginatingChannel(successMessages);
    }

    private List<Role> getListOfRolesFromMessage (CommandEvent event) {
        final List<Role> rolesInMessage = new ArrayList<>();

        final Guild  guild             = event.getGuild();
        String        rawMessageContent = event.getRawMessageContent();
        final boolean mentionedRolesByPrefix = rawMessageContent.contains("R$");
        final boolean mentionedUsersByPrefix = rawMessageContent.contains("U$");

        // If there are some roles we need to search for
        if (mentionedRolesByPrefix) {
            // Add the ones that we've already mentioned to the rolesInMessage list, and remove them from the content.
            if (!event.getMentionedRoles().isEmpty()) {
                rolesInMessage.addAll(event.getMentionedRoles());
                for (final Role role : rolesInMessage) {
                    rawMessageContent = rawMessageContent.replace(role.getAsMention(), "");
                }
            }

            final HashMap<String, List<Role>> searchTermToRoleMap = new HashMap<>();
            // Now, we should be left with a string maybe like... !addrole R$Some Role <@231231231231> U$Some User R$Some Role
            // So, continually search for new strings
            while (rawMessageContent.contains("R$")) {
                // When we find the first index of an R$, we should continue until we find the literal U$ or <, since that is the start of the next mention
                final String userSearchTerm = getSearchTerm(rawMessageContent, "R$");
                final String roleSearchTerm = userSearchTerm.substring("R$".length());

                final List<Role> foundRoles = guild.getRoles()
                     .stream()
                     .filter( each -> each.getName().contains(roleSearchTerm))
                     .collect(Collectors.toList());

                searchTermToRoleMap.put(roleSearchTerm, foundRoles);

                rawMessageContent = rawMessageContent.replace(userSearchTerm, "");
            }

            event.sendMessageToOriginatingChannel(searchTermToRoleMap.toString());
        }

        return new ArrayList<>();
    }

    final String getSearchTerm (final String rawMessage, final String prefix) {
        final int firstIndexOfRoleName = rawMessage.indexOf(prefix);
        final int indexOfNextRoleSearch = rawMessage.indexOf("R$", firstIndexOfRoleName);
        final int indexOfNextNameSearch = rawMessage.indexOf("U$", firstIndexOfRoleName);
        final int indexOfNextMention    = rawMessage.indexOf("<",  firstIndexOfRoleName);

        final boolean nextMentionNotFound = indexOfNextMention == -1;
        final boolean nextNameSearchNotFound = indexOfNextNameSearch == -1;
        final boolean nextRoleSearchNotFound = indexOfNextRoleSearch == -1;

        final int lastIndexOfRoleName;

        if (nextMentionNotFound && nextNameSearchNotFound && ind) {
            lastIndexOfRoleName = rawMessage.length();
        }
        else if (nextMentionNotFound || nextNameSearchNotFound) {
            lastIndexOfRoleName = nextMentionNotFound ? indexOfNextNameSearch : indexOfNextMention;
        }
        else {
            lastIndexOfRoleName = Math.min(indexOfNextMention, indexOfNextNameSearch);
        }

        return rawMessage.substring(firstIndexOfRoleName, lastIndexOfRoleName).trim();
    }
}
