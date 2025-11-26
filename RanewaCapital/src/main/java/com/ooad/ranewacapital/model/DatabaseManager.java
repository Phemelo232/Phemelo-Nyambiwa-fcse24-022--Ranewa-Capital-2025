package com.ooad.ranewacapital.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // Using the credentials from your DBConnection.java file
    private static final String URL = "jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:6543/postgres?user=postgres.lgksjkqrqnpetrftfgtv&password=Ranewa@232";

    private Connection connection;

    public DatabaseManager() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found", e);
        }
        this.connection = DriverManager.getConnection(URL);
    }

    public Connection getConnection() {
        return connection;
    }

    // Helper to ensure connection is valid
    public void reconnect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            this.connection = DriverManager.getConnection(URL);
        }
    }

    public boolean testConnection() {
        return false;
    }

    public void close() {
    }
}