package com.ooad.ranewacapital.model;

import java.util.Date;

public class Transaction {
    private final String id;
    private final double amount;
    private final Type type; // Assuming Type is an enum
    private final Date date;
    private final Account account;

    public enum Type {
        DEPOSIT, WITHDRAWAL, INTEREST
    }

    public Transaction(String id, double amount, Type type, java.sql.Date date, Account account) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.date = new Date();
        this.account = account;
    }

    public void logTransaction() {
        System.out.println("Transaction ID: " + id + ", Amount: " + amount + ", Type: " + type + ", Date: " + date + ", Account: " + account.getAccountNumber());
    }

    // Getters
    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public Type getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public Account getAccount() {
        return account;
    }
}