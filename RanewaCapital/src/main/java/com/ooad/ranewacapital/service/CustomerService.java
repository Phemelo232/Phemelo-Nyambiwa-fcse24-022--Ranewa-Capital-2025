package com.ooad.ranewacapital.service;

import com.ooad.ranewacapital.dao.*;
import com.ooad.ranewacapital.model.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerService {
    private final CustomerDAO customerDAO;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public CustomerService(DatabaseManager dbManager) {
        this.customerDAO = new CustomerDAO(dbManager.getConnection());
        this.accountDAO = new AccountDAO(dbManager.getConnection());
        this.transactionDAO = new TransactionDAO(dbManager);
    }

    // --- Account Creation Logic ---
    public void openNewAccount(Customer customer, String type, double initialDeposit, String branch, boolean employerVerified) throws Exception {
        String accountNumber = type.substring(0, 3).toUpperCase() + (System.currentTimeMillis() % 100000);

        Account newAccount;
        switch (type) {
            case "Savings":
                newAccount = new SavingsAccount(accountNumber, branch);
                break;
            case "Investment":
                if (initialDeposit < 500) throw new IllegalArgumentException("Investment requires min P500");
                newAccount = new InvestmentAccount(accountNumber, branch);
                break;
            case "Cheque":
                if (!employerVerified) throw new IllegalArgumentException("Employer verification required");
                newAccount = new ChequeAccount(accountNumber, branch, true);
                break;
            default:
                throw new IllegalArgumentException("Invalid Type");
        }

        newAccount.setBalance(initialDeposit);
        accountDAO.create(newAccount, customer.getId());

        if (initialDeposit > 0) {
            Transaction t = new Transaction(UUID.randomUUID().toString(), initialDeposit, Transaction.Type.DEPOSIT, new java.sql.Date(System.currentTimeMillis()), newAccount);
            transactionDAO.create(t);
        }
    }

    // --- Existing Logic ---
    public int createCustomer(Customer c) {
        try { return customerDAO.create(c); } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Customer validateLogin(String e, String p) throws SQLException {
        return customerDAO.findByEmailAndPassword(e, p);
    }

    // UPDATED CURRENCY: List summary uses 'P'
    public List<String> getAccountSummaries(Customer c) {
        List<String> list = new ArrayList<>();
        try {
            for (Account a : accountDAO.listByCustomer(c.getId())) {
                // Format: "ACC123: P500.00 (Savings)"
                list.add(a.getAccountNumber() + ": P" + String.format("%.2f", a.getBalance()) + " (" + a.getClass().getSimpleName().replace("Account","") + ")");
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public double getTotalBalance(Customer c) {
        double total = 0;
        try { for (Account a : accountDAO.listByCustomer(c.getId())) total += a.getBalance(); } catch (SQLException ex) {}
        return total;
    }

    public double getBalance(Customer c, String accNum) {
        try { Account a = accountDAO.findByAccountNumber(accNum); return a != null ? a.getBalance() : 0.0; } catch (SQLException e) { return 0.0; }
    }

    public void deposit(Customer c, String accNum, double amt) throws Exception {
        Account a = accountDAO.findByAccountNumber(accNum);
        if (a == null) throw new IllegalArgumentException("Account not found");
        a.deposit(amt, "Mobile Deposit");
        accountDAO.update(a);
        transactionDAO.create(new Transaction(UUID.randomUUID().toString(), amt, Transaction.Type.DEPOSIT, new java.sql.Date(System.currentTimeMillis()), a));
    }

    public void withdraw(Customer c, String accNum, double amt) throws Exception {
        Account a = accountDAO.findByAccountNumber(accNum);
        if (a == null) throw new IllegalArgumentException("Account not found");
        a.withdraw(amt);
        accountDAO.update(a);
        transactionDAO.create(new Transaction(UUID.randomUUID().toString(), amt, Transaction.Type.WITHDRAWAL, new java.sql.Date(System.currentTimeMillis()), a));
    }

    public double calculateInterest(Customer c, String accNum) throws Exception {
        Account a = accountDAO.findByAccountNumber(accNum);
        if (a instanceof InterestBearing) {
            double interest = ((InterestBearing) a).calculateInterest();
            if (interest > 0) {
                a.deposit(interest, "Interest");
                accountDAO.update(a);
                transactionDAO.create(new Transaction(UUID.randomUUID().toString(), interest, Transaction.Type.INTEREST, new java.sql.Date(System.currentTimeMillis()), a));
                return interest;
            }
        }
        return 0.0;
    }
}