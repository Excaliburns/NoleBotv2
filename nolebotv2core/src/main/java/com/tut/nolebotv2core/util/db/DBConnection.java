package com.tut.nolebotv2core.util.db;

import com.tut.nolebotv2core.enums.PropEnum;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.tut.nolebotv2core.util.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final Logger logger = LogManager.getLogger(DBConnection.class);
    private static Connection connection;

    static final String dbName = PropertiesUtil.getProperty(PropEnum.DB_NAME);
    static final String dbAddr = PropertiesUtil.getProperty(PropEnum.DB_ADDR);
    static final String dbUser = PropertiesUtil.getProperty(PropEnum.DB_USER);
    static final String dbPass = PropertiesUtil.getProperty(PropEnum.DB_PASS);
    static final String dbPort = PropertiesUtil.getProperty(PropEnum.DB_PORT);
    static final String dbType = PropertiesUtil.getProperty(PropEnum.DB_TYPE);

    /**
     * Get the current connection and reinitialize if it has failed.
     *
     * @return DB Connection instance
     */
    @SneakyThrows
    public static Connection getConnection() {
        // Exception is only thrown if timeout < 0
        if (!connection.isValid(0)) {
            initDbConnection();
        }

        return connection;
    }

    /**
     * Initializes the database connection.
     */
    public static void initDbConnection() {
        switch (dbType) {
            case ("sqlserver"):
                initSqlServerConnection();
                break;
            case ("mysql"):
                initMySqlConnection();
                break;
            default:
                logger.info("Database type not configured in prop file!");
        }
    }

    /**
     * Initialize the MySql connection, usually performed on startup.
     */
    public static void initMySqlConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(
                    "jdbc:sqlserver://" + dbAddr + ":" + dbPort + "/" + dbName + "?autoReconnect=true",
                    dbUser,
                    dbPass
            );
            logger.info("Successfully initialized database connection: MySQL");

        }
        catch (ClassNotFoundException e) {
            logger.error(
                    "MySQL driver could not be instantiated. " +
                    "Commands that require a database connection will work, but will error. {}",
                    e::getMessage
            );
        }
        catch (SQLException e) {
            logger.error(
                    "Could not establish DB Connection. " +
                    "Commands that require a database connection will work, but will error. {}",
                    e::getMessage
            );
        }
    }

    /**
     * Initialize the SQLServer connection, usually performed on startup.
     */
    public static void initSqlServerConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            final String dbName = PropertiesUtil.getProperty(PropEnum.DB_NAME);
            final String dbAddr = PropertiesUtil.getProperty(PropEnum.DB_ADDR);
            final String dbUser = PropertiesUtil.getProperty(PropEnum.DB_USER);
            final String dbPass = PropertiesUtil.getProperty(PropEnum.DB_PASS);
            final String dbPort = PropertiesUtil.getProperty(PropEnum.DB_PORT);

            connection = DriverManager.getConnection(
                    "jdbc:sqlserver://" + dbAddr + ":" + dbPort +
                            ";database=" + dbName +
                            ";encrypt=true;" +
                            "trustServerCertificate=true;",
                    dbUser,
                    dbPass
            );
            logger.info("Successfully initialized database connection: SQLServer");

        }
        catch (ClassNotFoundException e) {
            logger.error(
                    "SQLServer driver could not be instantiated. " +
                            "Commands that require a database connection will work, but will error. {}",
                    e::getMessage
            );
        }
        catch (SQLException e) {
            logger.error(
                    "Could not establish DB Connection. " +
                            "Commands that require a database connection will work, but will error. {}",
                    e::getMessage
            );
        }
    }
}
