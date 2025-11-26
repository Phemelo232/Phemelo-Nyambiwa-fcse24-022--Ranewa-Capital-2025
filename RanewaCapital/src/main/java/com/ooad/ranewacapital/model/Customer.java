// com.ooad.ranewacapital.model.Customer.java
package com.ooad.ranewacapital.model;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class Customer {

    // --- Immutable fields (always set in constructor) ---
    private final String firstName;
    private final String surname;
    private final List<Account> accounts;

    // --- Mutable fields ---
    private int id;
    private String email;
    private String passwordHash;
    private String employmentDetails;

    // Constructor used on SIGN-UP (plain password → we hash it)
    public Customer(String firstName, String surname, String email, String password, String employmentDetails) {
        this.firstName = validateNotEmpty(firstName, "First name");
        this.surname = validateNotEmpty(surname, "Surname");
        this.email = validateNotEmpty(email, "Email").trim().toLowerCase();
        this.passwordHash = hashPassword(validateNotEmpty(password, "Password"));
        this.employmentDetails = employmentDetails != null ? employmentDetails.trim() : "";
        this.accounts = new ArrayList<>();           // initialized here
        this.id = 0;                                 // 0 = not yet persisted
    }

    // Constructor used when loading from DATABASE (password already hashed)
    public Customer(int id, String firstName, String surname, String email, String passwordHash, String employmentDetails) {
        this.id = id;
        this.firstName = validateNotEmpty(firstName, "First name");
        this.surname = validateNotEmpty(surname, "Surname");
        this.email = validateNotEmpty(email, "Email").trim().toLowerCase();
        this.passwordHash = validateNotEmpty(passwordHash, "Password hash");
        this.employmentDetails = employmentDetails != null ? employmentDetails.trim() : "";
        this.accounts = new ArrayList<>();           // always initialized
    }

    // Helper to avoid null/empty strings
    private String validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return value.trim();
    }

    // Secure SHA-256 + Base64 hashing
    private String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainPassword.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // Verify password on login
    public boolean checkPassword(String plainPassword) {
        if (plainPassword == null) return false;
        return passwordHash.equals(hashPassword(plainPassword));
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getEmploymentDetails() {
        return employmentDetails;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts); // safe read-only view
    }

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }

    public void addAccount(Account account) {
        if (account != null) {
            accounts.add(account);
        }
    }

    public void updateProfile(String newEmail, String newEmploymentDetails) {
        this.email = validateNotEmpty(newEmail, "Email").trim().toLowerCase();
        this.employmentDetails = newEmploymentDetails != null ? newEmploymentDetails.trim() : "";
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + firstName + " " + surname + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + accounts.size() +
                '}';
    }
}