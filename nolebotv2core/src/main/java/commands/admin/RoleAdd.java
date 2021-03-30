package commands.admin;

import commands.util.Command;
import commands.util.CommandEvent;
import util.roles.Role;
import util.roles.RoleUtil;

import java.awt.*;
import java.util.List;

import static util.roles.RoleUtil.roleMap;
import static util.roles.RoleUtil.roles;

public class RoleAdd extends Command
{
    private RoleUtil roleUtil;
    public RoleAdd(RoleUtil roleUtil) {
        name = "roleadd";
        description = "Adds a role to your server";
        helpDescription = "Adds a role to your server";
        usages.add("roleadd [rolename] [role permission level]");
        usages.add("roleadd [rolename] [role permission level] [role color]");
        usages.add("roleadd [rolename] [role permission level] [true|false]");
        usages.add("roleadd [rolename] [role permission level] [true|false] [role color]");
        requiredPermissionLevel = 1000;
        this.roleUtil = roleUtil;
    }
    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        final List<String> messageContent = event.getMessageContent();
        final int messageSize = messageContent.size();
        Role roleToAdd = null;

        if (messageSize == 3) {
            roleToAdd = new Role(messageContent.get(1), Integer.valueOf(messageContent.get(2)));
            roleUtil.addNewRole(roleToAdd);
        }
        if (messageSize == 4) {
            if (messageContent.get(3).equals("true") || messageContent.get(3).equals("false")) {
                roleToAdd = new Role(messageContent.get(1), Integer.valueOf(messageContent.get(2)), Boolean.valueOf(messageContent.get(3)));
            }
            else{
                roleToAdd = new Role(messageContent.get(1), Integer.valueOf(messageContent.get(2)), Color.decode(messageContent.get(3)));
            }
        }
        if (messageSize == 5) {
                roleToAdd = new Role(messageContent.get(1), Integer.valueOf(messageContent.get(2)), Boolean.valueOf(messageContent.get(3)), Color.decode(messageContent.get(4)));
        }
        if (!roleMap.containsKey(roleToAdd.getRoleName()))
        {
            roles.add(roleToAdd);
        }
    }
}
