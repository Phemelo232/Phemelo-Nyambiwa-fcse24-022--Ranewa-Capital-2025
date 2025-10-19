package com.ooad.ranewacapital.model;

public class InvestmentAccount extends Account implements InterestBearing {
    private final double initialDeposit = 50.0;
    private final double interestRate = 0.05;

    public InvestmentAccount(String accountNumber, String branch) {
        super(accountNumber, branch);
    }

    @Override
    public double calculateInterest() {
        return balance * interestRate;
    }

    public void validateMin() {
        if (balance < initialDeposit) {
            throw new IllegalStateException("Balance below minimum initial deposit");
        }
    }

    public double getInitialDeposit() {
        return initialDeposit;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double ignoredInterestRate) {
    }

    public void setInitialDeposit(double ignoredInitialDeposit) {
    }
}