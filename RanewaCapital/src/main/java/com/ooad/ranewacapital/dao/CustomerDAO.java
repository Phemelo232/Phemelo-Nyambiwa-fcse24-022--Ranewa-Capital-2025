// com.ooad.ranewacapital.dao.CustomerDAO.java
package com.ooad.ranewacapital.dao;

import com.ooad.ranewacapital.model.Customer;
import java.sql.*;

public class CustomerDAO {
    private final Connection conn;

    public CustomerDAO(Connection conn) {
        this.conn = conn;
    }

    // FIXED: Now saves password + correct column names
    public int create(Customer customer) throws SQLException {
        // Check duplicate email
        if (emailExists(customer.getEmail())) {
            throw new SQLException("Email already exists");
        }

        String sql = """
            INSERT INTO customers 
            (first_name, surname, email, password_hash, employment_details) 
            VALUES (?, ?, ?, ?, ?) 
            RETURNING id
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getSurname());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPasswordHash());  // This is now set!
            ps.setString(5, customer.getEmploymentDetails());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    customer.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM customers WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Login method
    public Customer findByEmailAndPassword(String email, String password) throws SQLException {
        String sql = "SELECT * FROM customers WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("employment_details")
                    );
                    if (c.checkPassword(password)) {
                        return c;
                    }
                }
            }
        }
        return null;
    }
}