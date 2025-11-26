package com.ooad.ranewacapital;

import com.ooad.ranewacapital.model.*;
import com.ooad.ranewacapital.service.CustomerService;

import java.sql.SQLException;
import java.util.UUID;

public class IntegrationTest {
    public static void main(String[] args) {
        System.out.println("--- STARTING INTEGRATION TEST ---");

        try {
            // 1. Setup
            DatabaseManager dbManager = new DatabaseManager();
            CustomerService service = new CustomerService(dbManager);
            String testEmail = "testuser_" + System.currentTimeMillis() + "@example.com"; // Unique email

            // 2. Test Customer Creation
            System.out.print("Testing Customer Registration... ");
            Customer customer = new Customer("Test", "User", testEmail, "Password123", "Tester");
            int customerId = service.createCustomer(customer);
            if (customerId > 0) System.out.println("✅ PASS (ID: " + customerId + ")");
            else throw new RuntimeException("Failed to create customer");

            // 3. Test Login
            System.out.print("Testing Login Validation... ");
            Customer loggedIn = service.validateLogin(testEmail, "Password123");
            if (loggedIn != null && loggedIn.getEmail().equals(testEmail)) System.out.println("✅ PASS");
            else throw new RuntimeException("Login failed");

            // Note: For this test to create accounts, you would usually need direct access to AccountDAO
            // or expose a 'createAccount' method in CustomerService.
            // Assuming manual account creation for test purposes since Service layer
            // in your provided files didn't have createAccount exposed:

            System.out.println("\n(To fully test Deposits/Withdrawals, ensure 'createAccount' is implemented in Service)");
            System.out.println("--- INTEGRATION TEST COMPLETE ---");

        } catch (SQLException e) {
            System.err.println("❌ DATABASE ERROR: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}