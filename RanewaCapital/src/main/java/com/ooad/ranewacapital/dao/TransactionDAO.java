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

    /**
     * Saves a new transaction to the database.
     */
    public void create(Transaction transaction) throws SQLException {
        // UPDATED: Uses 'account_id' to match PostgreSQL schema
        String sql = "INSERT INTO Transactions (id, amount, type, date, account_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getId());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getType().toString());
            pstmt.setTimestamp(4, new Timestamp(transaction.getDate().getTime()));
            pstmt.setInt(5, transaction.getAccount().getId()); // Gets the DB ID of the account

            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a single transaction by its ID.
     */
    public Transaction read(String id) throws SQLException {
        String sql = "SELECT * FROM Transactions WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // We need the full Account object to reconstruct the Transaction
                    // We use AccountDAO to fetch it using the foreign key 'account_id'
                    int accountId = rs.getInt("account_id");
                    AccountDAO accountDao = new AccountDAO(conn);

                    Account account = null; // accountDao.read(accountId);

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

    /**
     * Lists all transactions for a specific Account ID.
     */
    public List<Transaction> listByAccount(int accountId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        // UPDATED: Uses 'account_id' column
        String sql = "SELECT * FROM Transactions WHERE account_id = ? ORDER BY date DESC";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accountId);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    transactions.add(new Transaction(
                            rs.getString("id"),
                            rs.getDouble("amount"),
                            Transaction.Type.valueOf(rs.getString("type")),
                            new Date(rs.getTimestamp("date").getTime()),
                            null // Account is null here to avoid complex DAO dependency loops in this specific DAO
                    ));
                }
            }
        }
        return transactions;
    }

    /**
     * Updates an existing transaction (rarely used in banking, but included for CRUD completeness).
     */
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

    /**
     * Deletes a transaction by ID.
     */
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM Transactions WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }
}