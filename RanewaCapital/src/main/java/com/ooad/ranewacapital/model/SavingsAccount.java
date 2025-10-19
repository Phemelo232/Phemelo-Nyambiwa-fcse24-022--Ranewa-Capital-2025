package com.ooad.ranewacapital.model;

public class SavingsAccount extends Account implements InterestBearing {
    private final double interestRate = 0.005;

    public SavingsAccount(String accountNumber, String branch) {
        super(accountNumber, branch);
    }

    @Override
    public double calculateInterest() {
        return balance * interestRate;
    }

    @Override
    public void withdraw(double amount) {
        // Override to potentially add restrictions, e.g., no withdrawal if below minimum
        if (balance - amount < 0) { // Simple check, could be more complex
            throw new IllegalStateException("Insufficient funds for withdrawal");
        }
        super.withdraw(amount);
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double ignoredInterestRate) {
    }
}