package com.ooad.ranewacapital.dao;

import com.ooad.ranewacapital.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private final Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public void create(Account account, int customerId) throws SQLException {
        // UPDATED: Using snake_case column names to match PostgreSQL
        String sql = "INSERT INTO Accounts (account_number, balance, branch, type, interest_rate, initial_deposit, employer_verified, customer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getAccountNumber());
            ps.setDouble(2, account.getBalance());
            ps.setString(3, account.getBranch());

            if (account instanceof SavingsAccount) {
                ps.setString(4, "SAVINGS");
                ps.setDouble(5, ((SavingsAccount) account).getInterestRate());
                ps.setDouble(6, 0);
                ps.setBoolean(7, false);
            } else if (account instanceof InvestmentAccount) {
                ps.setString(4, "INVESTMENT");
                ps.setDouble(5, ((InvestmentAccount) account).getInterestRate());
                ps.setDouble(6, ((InvestmentAccount) account).getInitialDeposit());
                ps.setBoolean(7, false);
            } else if (account instanceof ChequeAccount) {
                ps.setString(4, "CHEQUE");
                ps.setDouble(5, 0);
                ps.setDouble(6, 0);
                ps.setBoolean(7, ((ChequeAccount) account).isEmployerVerified());
            }
            ps.setInt(8, customerId);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) account.setId(rs.getInt(1));
            }
        }
    }

    public Account findByAccountNumber(String accNum) throws SQLException {
        // UPDATED: WHERE account_number = ?
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ?")) {
            ps.setString(1, accNum);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void update(Account account) throws SQLException {
        // UPDATED: SET balance = ... WHERE account_number = ...
        try (PreparedStatement ps = connection.prepareStatement("UPDATE Accounts SET balance = ? WHERE account_number = ?")) {
            ps.setDouble(1, account.getBalance());
            ps.setString(2, account.getAccountNumber());
            ps.executeUpdate();
        }
    }

    public List<Account> listByCustomer(int custId) throws SQLException {
        // UPDATED: WHERE customer_id = ?
        List<Account> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Accounts WHERE customer_id = ?")) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        // UPDATED: reading snake_case columns
        String accNum = rs.getString("account_number");
        String branch = rs.getString("branch");
        double balance = rs.getDouble("balance");
        boolean verified = rs.getBoolean("employer_verified");

        Account acc;
        switch (type) {
            case "SAVINGS" -> acc = new SavingsAccount(accNum, branch);
            case "INVESTMENT" -> acc = new InvestmentAccount(accNum, branch);
            case "CHEQUE" -> acc = new ChequeAccount(accNum, branch, verified);
            default -> throw new SQLException("Unknown Type");
        }
        acc.setId(rs.getInt("id"));
        acc.setBalance(balance);
        return acc;
    }
}