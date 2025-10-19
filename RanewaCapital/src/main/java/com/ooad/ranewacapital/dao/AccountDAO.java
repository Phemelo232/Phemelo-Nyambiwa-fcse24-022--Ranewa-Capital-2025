package com.ooad.ranewacapital.dao;

// AccountDAO.java (Partial CRUD; extend as needed)
import com.ooad.ranewacapital.model.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public record AccountDAO(DatabaseManager dbManager) {

    public AccountDAO(DatabaseManager dbManager, DatabaseManager dbManager1) {
        this(dbManager1);
    }

    public int create(Account account, int customerId) throws SQLException {
        String sql = "INSERT INTO Accounts (accountNumber, balance, branch, openDate, customerId, type, interestRate, initialDeposit, employerVerified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setDouble(2, account.getBalance());
            pstmt.setString(3, account.getBranch());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pstmt.setString(4, sdf.format(account.getOpenDate()));
            pstmt.setInt(5, customerId);

            switch (account) {
                case SavingsAccount sa -> {
                    pstmt.setString(6, "SAVINGS");
                    pstmt.setDouble(7, sa.getInterestRate());
                    pstmt.setDouble(8, 0.0);
                    pstmt.setBoolean(9, false);
                }
                case InvestmentAccount ia -> {
                    pstmt.setString(6, "INVESTMENT");
                    pstmt.setDouble(7, ia.getInterestRate());
                    pstmt.setDouble(8, ia.getInitialDeposit());
                    pstmt.setBoolean(9, false);
                }
                case ChequeAccount ca -> {
                    pstmt.setString(6, "CHEQUE");
                    pstmt.setDouble(7, 0.0);
                    pstmt.setDouble(8, 0.0);
                    pstmt.setBoolean(9, ca.isEmployerVerified());
                }
                default -> throw new IllegalArgumentException("Unsupported account type");
            }

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        account.setId(id); // Assume Account has setId(int id)
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public Account read(int id) throws SQLException {
        String sql = "SELECT * FROM Accounts WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("type");
                    Account account;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date openDate = null;
                    try {
                        openDate = sdf.parse(rs.getString("openDate"));
                    } catch (Exception e) {
                        // Handle parse error
                    }
                    switch (type) {
                        case "SAVINGS" -> {
                            account = new SavingsAccount(rs.getString("accountNumber"), rs.getString("branch"));
                            ((SavingsAccount) account).setInterestRate(rs.getDouble("interestRate"));
                        }
                        case "INVESTMENT" -> {
                            account = new InvestmentAccount(rs.getString("accountNumber"), rs.getString("branch"));
                            ((InvestmentAccount) account).setInterestRate(rs.getDouble("interestRate"));
                            ((InvestmentAccount) account).setInitialDeposit(rs.getDouble("initialDeposit"));
                        }
                        case "CHEQUE" -> {
                            account = new ChequeAccount(rs.getString("accountNumber"), rs.getString("branch"), rs.getBoolean("employerVerified"));
                        }
                        default -> throw new SQLException("Unknown account type: " + type);
                    }
                    account.setId(rs.getInt("id"));
                    account.setBalance(rs.getDouble("balance"));
                    account.setOpenDate(openDate);
                    return account;
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error reading account", e);
        }
        return null;
    }

    public void update(Account account) throws SQLException {
        String sql = "UPDATE Accounts SET balance = ?, type = ?, interestRate = ?, initialDeposit = ?, employerVerified = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, account.getBalance());
            switch (account) {
                case SavingsAccount sa -> {
                    pstmt.setString(2, "SAVINGS");
                    pstmt.setDouble(3, sa.getInterestRate());
                    pstmt.setDouble(4, 0.0);
                    pstmt.setBoolean(5, false);
                }
                case InvestmentAccount ia -> {
                    pstmt.setString(2, "INVESTMENT");
                    pstmt.setDouble(3, ia.getInterestRate());
                    pstmt.setDouble(4, ia.getInitialDeposit());
                    pstmt.setBoolean(5, false);
                }
                case ChequeAccount ca -> {
                    pstmt.setString(2, "CHEQUE");
                    pstmt.setDouble(3, 0.0);
                    pstmt.setDouble(4, 0.0);
                    pstmt.setBoolean(5, ca.isEmployerVerified());
                }
                default -> throw new IllegalArgumentException("Unsupported account type");
            }
            pstmt.setInt(6, account.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Accounts WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Account> listByCustomer(int customerId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Accounts WHERE customerId = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(read(rs.getInt("id"))); // Reuse read method
                }
            }
        }
        return accounts;
    }
}