package com.ooad.ranewacapital.model;

import java.util.List;
import java.util.ArrayList;

public class Customer {
    private final String firstName;
    private final String surname;
    private String email;
    private String employmentDetails;
    private final List<Account> accounts;

    public Customer(int id, String firstName, String surname, String email, String employmentDetails) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.employmentDetails = employmentDetails;
        this.accounts = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts); // Return a copy to prevent external modification
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void updateProfile(String newEmail, String newEmploymentDetails) {
        this.email = newEmail;
        this.employmentDetails = newEmploymentDetails;
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

    public void setId(int id) {
    }

    public int getId() {
        return 0;
    }
}