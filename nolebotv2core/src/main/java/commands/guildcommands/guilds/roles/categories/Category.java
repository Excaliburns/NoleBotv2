package commands.guildcommands.guilds.roles.categories;

import commands.util.Command;
import commands.util.CommandEvent;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Role;
import util.db.entities.CategoryEntity;
import util.db.statements.CategoryStatements;

import java.sql.SQLException;
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
        String roleName = origMsg.substring(16, origMsg.indexOf("<")).trim();
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
}