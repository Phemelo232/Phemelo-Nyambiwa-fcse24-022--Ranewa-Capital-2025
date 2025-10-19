package com.ooad.ranewacapital.model;

// Account.java
import java.util.Date;

public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected Date openDate;

    public Account(String accountNumber, String branch) {
        this.accountNumber = accountNumber;
        this.balance = 0.0;
        this.branch = branch;
        this.openDate = new Date();
    }

    public void deposit(double amount, String description) {
        if (amount > 0) {
            balance += amount;
            // Basic logging or transaction creation could go here, but keeping it simple
            System.out.println("Deposited " + amount + " to account " + accountNumber + ": " + description);
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrew " + amount + " from account " + accountNumber);
        } else {
            throw new IllegalArgumentException("Invalid withdrawal amount");
        }
    }

    public double calculateInterest() {
        return 0.0; // Base implementation, overridden in subclasses
    }

    // Getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getBranch() {
        return branch;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public int getId() {
        return 0;
    }

    public void setId(int id) {
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public void setInterestRate(double interestRate) {
    }
}