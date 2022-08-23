package com.tut.nolebotv2core.util.db.statements;

import com.google.common.collect.Lists;
import com.tut.nolebotv2core.util.db.DBConnection;
import lombok.extern.log4j.Log4j2;
import com.tut.nolebotv2core.util.db.entities.CategoryEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CategoryStatements {
    static final String insertCategory =
            " INSERT INTO GuildCategories                         " +
            "   (Id, CategoryName, GuildId)                       " +
            "   VALUES                                            " +
            "   (NEWID(), ?, ?                                  ) ";

    static final String countByGuildIdAndCategoryName =
            " SELECT COUNT(Id) FROM GuildCategories               " +
            "   WHERE GuildId=?                                   " +
            "   AND   CategoryName=?                              ";

    static final String deleteCategoryRolesByGuildIdAndName =
            "DELETE cr FROM GuildCategories gc                    " +
            "    LEFT JOIN CategoryRoles cr ON gc.Id=cr.CategoryId" +
            "    WHERE GuildId=? AND CategoryName=?               ";
    static final String deleteCategoryOwnersByGuildIdAndName =
            "DELETE co FROM GuildCategories gc                    " +
            "   LEFT JOIN CategoryOwners co ON gc.Id=co.CategoryId" +
            "    WHERE GuildId=? AND CategoryName=?               ";
    static final String deleteCategoriesByGuildIdAndName =
            "DELETE gc FROM GuildCategories gc                    " +
            "    WHERE GuildId=? AND CategoryName=?               ";

    static final String getIdByGuildIdAndCategoryName =
            " SELECT Id FROM GuildCategories                      " +
            "   WHERE GuildId=?                                   " +
            "   AND   CategoryName=?                              ";

    static final String insertRole =
            " INSERT INTO CategoryRoles(CategoryId, RoleId, RoleName)" +
            "     VALUES (?, ?, ?)                                   ";

    static final String selectCategoriesByGuild =
            "SELECT CategoryName FROM GuildCategories gc             " +
            "     WHERE gc.GuildId=?                                 ";

    /**
     * Executes an insert into the GuildCategories table.
     *
     * @param category The category to insert.
     * @throws SQLException if an exception comes from the db
     */
    public void insertCategory(final CategoryEntity category) throws SQLException {
        final PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(insertCategory);

        preparedStatement.setString(1, category.getName());
        preparedStatement.setString(2, category.getGuildId());

        preparedStatement.execute();
    }

    /**
     * Executes a delete from the GuildCategories table.
     *
     * @param category The category to delete.
     * @return An array of ints, the result of the executeUpdate method
     * @throws SQLException if an exception comes from the db
     */
    public int[] deleteCategory(final CategoryEntity category) throws SQLException {
        int[] result = new int[3];
        PreparedStatement preparedStatement = DBConnection.getConnection()
                .prepareStatement(deleteCategoryOwnersByGuildIdAndName);
        preparedStatement.setString(1, category.getGuildId());
        preparedStatement.setString(2, category.getName());
        result[0] = preparedStatement.executeUpdate();

        preparedStatement = DBConnection.getConnection()
                .prepareStatement(deleteCategoryRolesByGuildIdAndName);
        preparedStatement.setString(1, category.getGuildId());
        preparedStatement.setString(2, category.getName());
        result[1] = preparedStatement.executeUpdate();

        preparedStatement = DBConnection.getConnection()
                .prepareStatement(deleteCategoriesByGuildIdAndName);
        preparedStatement.setString(1, category.getGuildId());
        preparedStatement.setString(2, category.getName());
        result[2] = preparedStatement.executeUpdate();

        return result;
    }

    /**
     * Checks if a category already exists in the GuildCategories table.
     *
     * @param category The category to check
     * @throws SQLException if an exception comes from the db
     */
    public boolean checkExists(final CategoryEntity category) throws SQLException {
        final PreparedStatement preparedStatement =
                DBConnection.getConnection().prepareStatement(countByGuildIdAndCategoryName);

        preparedStatement.setString(1, category.getGuildId());
        preparedStatement.setString(2, category.getName());

        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        else {
            return false;
        }
    }

    /**
     * Links a category to a specific role.
     *
     * @param guildId The guildId that the category is in
     * @param name The name of the category
     * @param roleId The Discord snowflake ID of the role
     * @param roleName The name of the role in Discord
     * @return The result of the executed query
     * @throws SQLException if an exception comes from the db
     */
    public boolean insertRoleByNameAndId(
            String guildId,
            String name,
            String roleId,
            String roleName) throws SQLException {
        PreparedStatement preparedStatement =
                DBConnection.getConnection().prepareStatement(getIdByGuildIdAndCategoryName);
        preparedStatement.setString(1, guildId);
        preparedStatement.setString(2, name);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            String id = rs.getString(1);
            preparedStatement = DBConnection.getConnection().prepareStatement(insertRole);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, roleId);
            preparedStatement.setString(3, roleName);
            return preparedStatement.execute();
        }
        else {
            throw new SQLException();
        }
    }

    /**
     * Gets the list of category names for a guild.
     *
     * @param guildId The guild ID to get categories for
     * @return The list of category names in the guild
     * @throws SQLException if an exception comes from the db
     */
    public List<String> getCategories(String guildId) throws SQLException {
        PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(selectCategoriesByGuild);
        ArrayList<String> categoryNames = Lists.newArrayList();
        preparedStatement.setString(1, guildId);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            categoryNames.add(rs.getString(1));
        }
        return categoryNames;
    }
}
