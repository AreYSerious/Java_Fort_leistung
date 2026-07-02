package com.socialmedia.dao;

import com.socialmedia.model.Transaction;
import com.socialmedia.model.Transaction.TransactionType;
import com.socialmedia.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    /**
     * Creates a new transaction
     * @param transaction The transaction to create
     * @throws SQLException if database error occurs
     */
    public void createTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (sender_username, recipient_username, description, amount, transaction_type, transaction_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, transaction.getSenderUsername());
            pstmt.setString(2, transaction.getRecipientUsername());
            pstmt.setString(3, transaction.getDescription());
            pstmt.setBigDecimal(4, transaction.getAmount());
            pstmt.setString(5, transaction.getTransactionType().name());
            pstmt.setTimestamp(6, Timestamp.valueOf(transaction.getTransactionDate()));
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    transaction.setId(rs.getInt(1));
                }
            }
        }
    }
    
    /**
     * Gets all transactions for a user (sent or received)
     * @param username The username
     * @return List of transactions
     * @throws SQLException if database error occurs
     */
    public List<Transaction> getTransactionsByUser(String username) throws SQLException {
        String sql = "SELECT * FROM transactions " +
                     "WHERE sender_username = ? OR recipient_username = ? " +
                     "ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }
        return transactions;
    }
    
    /**
     * Gets all transactions
     * @return List of all transactions
     * @throws SQLException if database error occurs
     */
    public List<Transaction> getAllTransactions() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }
    
    /**
     * Maps a ResultSet row to a Transaction object
     * @param rs The ResultSet
     * @return The Transaction object
     * @throws SQLException if database error occurs
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setSenderUsername(rs.getString("sender_username"));
        transaction.setRecipientUsername(rs.getString("recipient_username"));
        transaction.setDescription(rs.getString("description"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
        
        Timestamp transactionDate = rs.getTimestamp("transaction_date");
        if (transactionDate != null) {
            transaction.setTransactionDate(transactionDate.toLocalDateTime());
        }
        
        return transaction;
    }
}
