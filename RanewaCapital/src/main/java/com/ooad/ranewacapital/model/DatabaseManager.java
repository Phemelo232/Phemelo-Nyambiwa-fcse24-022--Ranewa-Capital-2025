package com.ooad.ranewacapital.model;// com.ooad.ranewacapital.model.DatabaseManager.java
import java.sql.*;

public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:h2:mem:bankingdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private final Connection connection;

    public DatabaseManager() throws SQLException {
        // Load H2 driver (assume h2 jar is in classpath for Codespaces)
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 driver not found", e);
        }
        this.connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        initializeSchema();
    }

    private void initializeSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("RUNSCRIPT FROM 'classpath:schema.sql'"); // Or embed the SQL directly as in previous sketch
            // Alternative: stmt.execute(sql from above);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
