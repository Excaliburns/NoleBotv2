package util.db.statements;

import util.db.DBConnection;
import util.db.entities.CategoryEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryStatements {
    static final String insertCategory =
            " INSERT INTO GuildCategories                         " +
            "   (Id, CategoryName, GuildId)                       " +
            "   VALUES                                            " +
            "   (NEWID(), ?, ?                                  ) ";

    static final String selectByGuildIdAndCategoryName =
            " SELECT COUNT(Id) FROM GuildCategories               " +
            "   WHERE GuildId=?                                   " +
            "   AND   CategoryName=?                              ";

    static final String deleteByGuildIdAndCategoryName =
            " DELETE * FROM GuildCategories                       " +
            "   WHERE GuildId=?                                   " +
            "   AND   CategoryName=?                              ";

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
     * @throws SQLException if an exception comes from the db
     */
    public boolean deleteCategory(final CategoryEntity category) throws SQLException {
        final PreparedStatement preparedStatement = DBConnection.getConnection()
                .prepareStatement(deleteByGuildIdAndCategoryName);

        preparedStatement.setString(1, category.getName());
        preparedStatement.setString(2, category.getGuildId());

        return preparedStatement.execute();
    }

    /**
     * Checks if a category already exists in the GuildCategories table.
     *
     * @param category The category to check
     * @throws SQLException if an exception comes from the db
     */
    public boolean checkExists(final CategoryEntity category) throws SQLException {
        final PreparedStatement preparedStatement =
                DBConnection.getConnection().prepareStatement(selectByGuildIdAndCategoryName);

        preparedStatement.setString(1, category.getGuildId());
        preparedStatement.setString(2, category.getName());

        ResultSet rs = preparedStatement.executeQuery();
        if (rs.first()) {
            return true;
        }
        return false;
    }
}
