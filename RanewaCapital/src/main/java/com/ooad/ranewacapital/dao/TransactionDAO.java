package com.ooad.ranewacapital.dao;

import com.ooad.ranewacapital.model.Account;
import com.ooad.ranewacapital.model.DatabaseManager;
import com.ooad.ranewacapital.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private final DatabaseManager dbManager;

    public TransactionDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void create(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO Transactions (id, amount, type, date, accountId) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getId());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getType().toString());
            pstmt.setTimestamp(4, new Timestamp(transaction.getDate().getTime()));
            pstmt.setInt(5, transaction.getAccount().getId()); // Assume Account has getId()
            pstmt.executeUpdate();
        }
    }

    public Transaction read(String id) throws SQLException {
        String sql = "SELECT * FROM Transactions WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Note: Would need to fetch Account separately via AccountDAO
                    Account account = new AccountDAO(dbManager).read(rs.getInt("accountId")); // Dependency
                    return new Transaction(
                            rs.getString("id"),
                            rs.getDouble("amount"),
                            Transaction.Type.valueOf(rs.getString("type")),
                            new Date(rs.getTimestamp("date").getTime()),
                            account
                    );
                }
            }
        }
        return null;
    }

    public void update(Transaction transaction) throws SQLException {
        String sql = "UPDATE Transactions SET amount = ?, type = ?, date = ? WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, transaction.getAmount());
            pstmt.setString(2, transaction.getType().toString());
            pstmt.setTimestamp(3, new Timestamp(transaction.getDate().getTime()));
            pstmt.setString(4, transaction.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM Transactions WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Transaction> listByAccount(int accountId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE accountId = ? ORDER BY date DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                AccountDAO accountDao = new AccountDAO(dbManager);
                while (rs.next()) {
                    Account account = accountDao.read(rs.getInt("accountId"));
                    transactions.add(new Transaction(
                            rs.getString("id"),
                            rs.getDouble("amount"),
                            Transaction.Type.valueOf(rs.getString("type")),
                            new Date(rs.getTimestamp("date").getTime()),
                            account
                    ));
                }
            }
        }
        return transactions;
    }
}
