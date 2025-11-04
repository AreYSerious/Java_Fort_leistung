package com.socialmedia.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:h2:./socialmedia_db";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private static Connection connection;
    private static boolean initialized = false;
    
    /**
     * Gets the database connection, creating it if necessary
     * @return The database connection
     * @throws SQLException if connection fails
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (!initialized) {
                initializeDatabase();
                initialized = true;
            }
        }
        return connection;
    }
    
    /**
     * Initializes the database schema
     * @throws SQLException if initialization fails
     */
    private static void initializeDatabase() throws SQLException {
        try (InputStream is = DatabaseConnection.class.getResourceAsStream("/schema.sql")) {
            if (is == null) {
                throw new SQLException("Could not find schema.sql resource");
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sql = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Skip comments
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sql.append(line).append("\n");
            }
            
            // Execute SQL statements
            String[] statements = sql.toString().split(";");
            try (Statement stmt = connection.createStatement()) {
                for (String statement : statements) {
                    if (!statement.trim().isEmpty()) {
                        stmt.execute(statement.trim());
                    }
                }
            }
        } catch (IOException e) {
            throw new SQLException("Error reading schema file", e);
        }
    }
    
    /**
     * Closes the database connection
     */
    public static synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                initialized = false;
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
