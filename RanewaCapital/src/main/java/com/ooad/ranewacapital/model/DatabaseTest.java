package com.ooad.ranewacapital.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        DatabaseManager dbManager = null;
        try {
            dbManager = new DatabaseManager();
            if (dbManager.testConnection()) {
                System.out.println("✅ DB Connection successful! Schema ready for use.");

                // Optional: Quick schema verification
                try (Statement stmt = dbManager.getConnection().createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'CUSTOMERS'")) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("✅ Customers table exists.");
                    }
                }
            } else {
                System.out.println("❌ Connection test failed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (dbManager != null) {
                dbManager.close();
            }
        }
    }
}
