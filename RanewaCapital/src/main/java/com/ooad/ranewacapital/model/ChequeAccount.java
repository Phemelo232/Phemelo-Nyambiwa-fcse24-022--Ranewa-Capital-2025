package com.ooad.ranewacapital.model;

public class ChequeAccount extends Account {
    private boolean employerVerified;

    public ChequeAccount(String accountNumber, String branch, boolean employerVerified) {
        super(accountNumber, branch);
        this.employerVerified = employerVerified;
    }

    @Override
    public void withdraw(double amount) {
        if (!employerVerified) {
            throw new IllegalStateException("Employer verification required for withdrawal");
        }
        super.withdraw(amount);
    }

    public boolean isEmployerVerified() {
        return employerVerified;
    }

    public void setEmployerVerified(boolean employerVerified) {
        this.employerVerified = employerVerified;
    }
}