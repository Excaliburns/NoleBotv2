package util.db;

import enums.PropEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final Logger logger = LogManager.getLogger(DBConnection.class);
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    public static void initMySqlConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            final String dbName = PropertiesUtil.getProperty(PropEnum.DB_NAME);
            final String dbAddr = PropertiesUtil.getProperty(PropEnum.DB_ADDR);
            final String dbUser = PropertiesUtil.getProperty(PropEnum.DB_USER);
            final String dbPass = PropertiesUtil.getProperty(PropEnum.DB_PASS);
            final String dbPort = PropertiesUtil.getProperty(PropEnum.DB_PORT);

            connection = DriverManager.getConnection("jdbc:mysql://" + dbAddr + ":" + dbPort + "/" + dbName, dbUser, dbPass);
            logger.info("Successfully initialized database connection: MySQL");

        } catch (ClassNotFoundException e) {
            logger.error("MySQL driver could not be instantiated. Commands that require a database connection will work, but will error. {}", e.getMessage());
        } catch (SQLException e) {
            logger.error("Could not establish DB Connection. Commands that require a database connection will work, but will error. {}", e.getMessage());
        }
    }
}
