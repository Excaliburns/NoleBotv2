package com.tut.nolebotv2core.commands.guildcommands.guilds.roles.categories;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import com.tut.nolebotv2core.util.db.entities.CategoryEntity;
import com.tut.nolebotv2core.util.db.statements.CategoryStatements;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

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
    public void registerCommand(JDA jda) {
        jda.upsertCommand(
                Commands.slash(name, description)
                        .addSubcommands(
                                new SubcommandData("create", "Creates a category")
                                        .addOption(OptionType.STRING, "name", "The name of the category", true),
                                new SubcommandData("delete", "Deletes a category")
                                        .addOption(OptionType.STRING, "category", "The name of the category to delete", true),
                                new SubcommandData("assign", "Adds a role to a category")
                                        .addOption(OptionType.STRING, "category", "The name of the category the role is being added to", true)
                                        .addOption(OptionType.ROLE, "role", "The role being added to the category", true),
                                new SubcommandData("setowner", "Sets the owner of a category")
                                        .addOption(OptionType.STRING, "category", "The name of the category the owner is being assigned to", true)
                                        .addOption(OptionType.USER, "owner", "The owner of the category", true),
                                new SubcommandData("list", "Lists the categories in the current server")
                        )
        ).queue();
    }

    @Override
    @SuppressWarnings("PMD.SwitchStmtsShouldHaveDefault")
    public void executeCommand(CommandEvent event) throws Exception {
        switch (event.getOriginatingJDAEvent().getSubcommandName()) {
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
        String name = event.getOriginatingJDAEvent().getOption("name").getAsString();
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
        String name = event.getOriginatingJDAEvent().getOption("name").getAsString();
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
        Role role = event.getOriginatingJDAEvent().getOption("role").getAsRole();
        String name = event.getOriginatingJDAEvent().getOption("category").getAsString();
        if (statements.checkExists(new CategoryEntity(name, event.getGuildId()))) {
            try {
                statements.insertRoleByNameAndId(event.getGuildId(), name, role.getId(), role.getName());
            }
            catch (SQLException e) {
                event.sendErrorResponseToOriginatingChannel("Role " + role.getName()
                        + " already assigned to category");
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
        User user = event.getOriginatingJDAEvent().getOption("owner").getAsUser();
        String catName = event.getOriginatingJDAEvent().getOption("category").getAsString();
        try {
            success = Arrays.stream(
                            statements.setOwnerOfCategory(user.getId(), user.getName(),
                                    event.getGuildId(),
                                    catName))
                    .anyMatch((i) -> {
                        return i >= 0;
                    });
        }
        catch (SQLException e) {
            event.printStackTraceToChannelFromThrowable(event.getChannel(), e);
        }
        if (success) {
            event.sendSuccessResponseToOriginatingChannel("Successfully set owner");
        }
        else {
            event.sendErrorResponseToOriginatingChannel("Error setting owner");
        }
    }
}
