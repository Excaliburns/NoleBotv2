package commands.guildcommands.guilds.roles.categories;

import commands.util.Command;
import commands.util.CommandEvent;
import lombok.extern.log4j.Log4j2;
import util.db.entities.CategoryEntity;
import util.db.statements.CategoryStatements;

import java.sql.SQLException;

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

    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        switch (event.getMessageContent().get(1)) {
            case ("create") -> {
                createCategory(event);
            }
            case ("delete") -> {
                deleteCategory(event);
            }
            default -> {
                event.sendErrorResponseToOriginatingChannel("Category command not found");
            }
        }
    }

    private void createCategory(CommandEvent event) throws SQLException {
        String name = event.getMessageContent().get(2);
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
        String name = event.getMessageContent().get(2);
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
}
