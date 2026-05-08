package com.project.artconnect.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.project.artconnect.config.DatabaseConfig;
import java.sql.DriverManager;
/**
 * Utility class to manage JDBC connections.
 * TODO: Students must implementation the getConnection logic.
 */
public class ConnectionManager {

    /**
     * Provides a connection to the MySQL database.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Please add mysql-connector-java to classpath.", e);
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.URL,
            DatabaseConfig.USER,
            DatabaseConfig.PASSWORD
        );
    }

}
