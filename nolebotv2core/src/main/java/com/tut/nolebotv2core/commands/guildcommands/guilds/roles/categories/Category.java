package com.tut.nolebotv2core.commands.guildcommands.guilds.roles.categories;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Role;
import com.tut.nolebotv2core.util.db.entities.CategoryEntity;
import com.tut.nolebotv2core.util.db.statements.CategoryStatements;
import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class Category extends Command {
    private final CategoryStatements statements = new CategoryStatements();

    /**
     * Constructor.
     */
    public Category() {
        this.name = "category";
        this.description = "Command to manage role categories";
        this.helpDescription = "Allows for various operations on categories, " +
                "such as creation, owner management, and deletion";
        this.requiredPermissionLevel = 1000;
        usages.add("category create [name]");
        usages.add("category delete [name]");
        usages.add("category assign [name] [roles]");
        usages.add("category setowner [name] [owner]");
        usages.add("category list");
    }

    @Override
    @SuppressWarnings("PMD.SwitchStmtsShouldHaveDefault")
    public void onCommandReceived(CommandEvent event) throws Exception {
        switch (event.getMessageContent().get(1)) {
            case ("create") -> {
                createCategory(event);
            }
            case ("delete") -> {
                deleteCategory(event);
            }
            case ("assign") -> {
                assignRoleToCategory(event);
            }
            case ("list") -> {
                listCategories(event);
            }
            case ("setowner") -> {
                setOwner(event);
            }
            default -> {
                event.sendErrorResponseToOriginatingChannel("Category command not found");
            }
        }
    }

    private void createCategory(CommandEvent event) throws SQLException {
        final List<String> msg = event.getMessageContent();
        String name = String.join(" ", msg.subList(2, msg.size()));
        CategoryEntity newCategory = new CategoryEntity(name, event.getGuildId());
        if (statements.checkExists(newCategory)) {
            event.sendErrorResponseToOriginatingChannel("Category exists");
        }
        else {
            try {
                statements.insertCategory(newCategory);
                event.sendSuccessResponseToOriginatingChannel("Category added");
            }
            catch (SQLException e) {
                log.error(e::getMessage);
            }
        }
    }

    private void deleteCategory(CommandEvent event) throws SQLException {
        final List<String> msg = event.getMessageContent();
        String name = String.join(" ", msg.subList(2, msg.size()));
        CategoryEntity newCategory = new CategoryEntity(name, event.getGuildId());
        if (!statements.checkExists(newCategory)) {
            event.sendErrorResponseToOriginatingChannel("Category doesn't exist");
        }
        else {
            try {
                statements.deleteCategory(newCategory);
                event.sendSuccessResponseToOriginatingChannel("Category deleted");
            }
            catch (SQLException e) {
                log.error(e::getMessage);
            }
        }
    }

    private void assignRoleToCategory(CommandEvent event) throws SQLException {
        List<Role> roleList = event.getMentionedRoles();
        String origMsg = event.getRawMessageContent();
        String roleName = origMsg.substring(
                event.getSettings().getPrefix().length() + "category".length() + "assign".length(),
                origMsg.indexOf("<")
        ).trim();
        if (statements.checkExists(new CategoryEntity(roleName, event.getGuildId()))) {
            for (Role r : roleList) {
                try {
                    statements.insertRoleByNameAndId(event.getGuildId(), roleName, r.getId(), r.getName());
                }
                catch (SQLException e) {
                    event.sendErrorResponseToOriginatingChannel("Role " + r.getName()
                            + " already assigned to category");
                }
            }
            event.sendSuccessResponseToOriginatingChannel("Roles added to category!");
        }
        else {
            event.sendErrorResponseToOriginatingChannel("Category doesn't exist");
        }
    }

    private void listCategories(CommandEvent event) throws SQLException {
        List<String> categoryNames = statements.getCategories(event.getGuildId());
        StringBuilder builder = new StringBuilder();
        for (String s : categoryNames) {
            builder.append(s).append("\n");
        }
        event.sendMessageToOriginatingChannel(builder.toString());
    }
    private void setOwner(CommandEvent event) throws SQLException {
        boolean success = true;
        List<User> userList = event.getMentionedUsers();
        String origMsg = event.getRawMessageContent();
        String prefix = event.getSettings().getPrefix();
        String catName = origMsg.substring(
                prefix.length() + "category".length() + "setowner".length(),
                origMsg.indexOf("<")
        ).trim();
        for (User u : userList) {
            try {
                success = Arrays.stream(statements.setOwnerOfCategory(u.getId(), event.getGuildId(), catName)).anyMatch((i) -> {
                    return i >= 0;
                });
            }
            catch (SQLException e) {
                event.printStackTraceToChannelFromThrowable(event.getChannel(), e);
            }
        }
        if (success) {
            event.sendSuccessResponseToOriginatingChannel("Successfully set owner");
        }
        else {
            event.sendErrorResponseToOriginatingChannel("Error setting owner");
        }
    }
}
