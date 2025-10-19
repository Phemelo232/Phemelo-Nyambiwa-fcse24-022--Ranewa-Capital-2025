package com.ooad.ranewacapital.dao;

// CustomerDAO.java
import com.ooad.ranewacapital.model.Customer;
import com.ooad.ranewacapital.model.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private final DatabaseManager dbManager;

    public CustomerDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public int create(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customers (firstName, surname, email, employmentDetails) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getSurname());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getEmploymentDetails());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        customer.setId(id); // Assume Customer has setId(int id)
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public Customer read(int id) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getString("employmentDetails")
                    ); // Assume updated Customer constructor with id
                }
            }
        }
        return null;
    }

    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE Customers SET firstName = ?, surname = ?, email = ?, employmentDetails = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getSurname());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getEmploymentDetails());
            pstmt.setInt(5, customer.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Customers WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Customer> listAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("surname"),
                        rs.getString("email"),
                        rs.getString("employmentDetails")
                ));
            }
        }
        return customers;
    }
}