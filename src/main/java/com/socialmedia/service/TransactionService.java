package com.socialmedia.service;

import com.socialmedia.dao.TransactionDAO;
import com.socialmedia.dao.UserDAO;
import com.socialmedia.model.Transaction;
import com.socialmedia.model.Transaction.TransactionType;
import com.socialmedia.model.User;
import com.socialmedia.util.DatabaseConnection;
import com.socialmedia.util.ValidationUtil;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;
    private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public TransactionService() {
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();
    }
    
    /**
     * Deposits money into a user's account
     * @param username The username
     * @param amount The amount to deposit
     * @throws SQLException if database error occurs
     * @throws IllegalArgumentException if amount is invalid
     */
    public void deposit(String username, BigDecimal amount) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Betrag muss größer als 0 sein");
        }
        
        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            
            User user = userDAO.findByUsername(username);
            if (user == null) {
                throw new IllegalArgumentException("Benutzer nicht gefunden");
            }
            
            // Update balance
            BigDecimal newBalance = user.getBalance().add(amount);
            userDAO.updateBalance(username, newBalance);
            
            // Create transaction record
            Transaction transaction = new Transaction(null, username, "", amount, TransactionType.EINZAHLUNG);
            transaction.setTransactionDate(LocalDateTime.now());
            transactionDAO.createTransaction(transaction);
            
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                // Ignore rollback exception
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException autoCommitEx) {
                // Ignore auto-commit exception
            }
        }
    }
    
    /**
     * Withdraws money from a user's account
     * @param username The username
     * @param amount The amount to withdraw
     * @throws SQLException if database error occurs
     * @throws IllegalArgumentException if amount is invalid or insufficient funds
     */
    public void withdraw(String username, BigDecimal amount) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Betrag muss größer als 0 sein");
        }
        
        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            
            User user = userDAO.findByUsername(username);
            if (user == null) {
                throw new IllegalArgumentException("Benutzer nicht gefunden");
            }
            
            if (user.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Unzureichendes Guthaben");
            }
            
            // Update balance
            BigDecimal newBalance = user.getBalance().subtract(amount);
            userDAO.updateBalance(username, newBalance);
            
            // Create transaction record
            Transaction transaction = new Transaction(username, null, "", amount, TransactionType.AUSZAHLUNG);
            transaction.setTransactionDate(LocalDateTime.now());
            transactionDAO.createTransaction(transaction);
            
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                // Ignore rollback exception
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException autoCommitEx) {
                // Ignore auto-commit exception
            }
        }
    }
    
    /**
     * Transfers money from one user to another
     * @param senderUsername The sender's username
     * @param recipientUsername The recipient's username
     * @param amount The amount to transfer
     * @param description The transfer description
     * @throws SQLException if database error occurs
     * @throws IllegalArgumentException if transfer is invalid
     */
    public void transfer(String senderUsername, String recipientUsername, BigDecimal amount, String description) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Betrag muss größer als 0 sein");
        }
        
        if (senderUsername.equals(recipientUsername)) {
            throw new IllegalArgumentException("Sie können nicht an sich selbst überweisen");
        }
        
        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            
            User sender = userDAO.findByUsername(senderUsername);
            User recipient = userDAO.findByUsername(recipientUsername);
            
            if (sender == null) {
                throw new IllegalArgumentException("Absender nicht gefunden");
            }
            
            if (recipient == null) {
                throw new IllegalArgumentException("Empfänger nicht gefunden: " + recipientUsername);
            }
            
            if (sender.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Unzureichendes Guthaben");
            }
            
            // Update balances
            BigDecimal newSenderBalance = sender.getBalance().subtract(amount);
            BigDecimal newRecipientBalance = recipient.getBalance().add(amount);
            
            userDAO.updateBalance(senderUsername, newSenderBalance);
            userDAO.updateBalance(recipientUsername, newRecipientBalance);
            
            // Create transaction record
            Transaction transaction = new Transaction(senderUsername, recipientUsername, description, amount, TransactionType.UEBERWEISUNG);
            transaction.setTransactionDate(LocalDateTime.now());
            transactionDAO.createTransaction(transaction);
            
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                // Ignore rollback exception
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException autoCommitEx) {
                // Ignore auto-commit exception
            }
        }
    }
    
    /**
     * Performs mass transfer from CSV file
     * @param senderUsername The sender's username
     * @param csvFilePath The path to the CSV file
     * @return List of error messages (empty if successful)
     * @throws SQLException if database error occurs
     * @throws IOException if file error occurs
     */
    public List<String> massTransfer(String senderUsername, String csvFilePath) throws SQLException, IOException {
        List<String> errors = new ArrayList<>();
        List<TransferData> transfers = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Read and validate CSV file
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            int lineNumber = 0;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip header line if it exists
                if (isFirstLine && line.trim().toLowerCase().startsWith("empfänger")) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Validate line format
                if (!ValidationUtil.isValidCsvLine(line)) {
                    errors.add("Zeile " + lineNumber + ": Ungültiges Format");
                    continue;
                }
                
                String[] parts = ValidationUtil.parseCsvLine(line);
                String recipient = parts[0].trim();
                String amountStr = parts[1].trim();
                String description = parts[2].trim();
                
                // Validate amount format
                if (!ValidationUtil.isValidAmount(amountStr)) {
                    errors.add("Zeile " + lineNumber + ": Ungültiger Betrag");
                    continue;
                }
                
                BigDecimal amount = new BigDecimal(amountStr);
                
                // Validate amount > 0
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add("Zeile " + lineNumber + ": Betrag muss größer als 0 sein");
                    continue;
                }
                
                // Check self-transfer
                if (recipient.equals(senderUsername)) {
                    errors.add("Zeile " + lineNumber + ": Sie können nicht an sich selbst überweisen");
                    continue;
                }
                
                transfers.add(new TransferData(recipient, amount, description));
                totalAmount = totalAmount.add(amount);
            }
        }
        
        // If there are format errors, return them
        if (!errors.isEmpty()) {
            return errors;
        }
        
        // Check if all recipients exist
        for (TransferData transfer : transfers) {
            if (!userDAO.exists(transfer.recipient)) {
                errors.add("Benutzer existiert nicht: " + transfer.recipient);
            }
        }
        
        if (!errors.isEmpty()) {
            return errors;
        }
        
        // Check if sender has sufficient balance
        User sender = userDAO.findByUsername(senderUsername);
        if (sender.getBalance().compareTo(totalAmount) < 0) {
            errors.add("Unzureichendes Guthaben. Benötigt: " + totalAmount + ", Verfügbar: " + sender.getBalance());
            return errors;
        }
        
        // Perform all transfers
        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            
            for (TransferData transfer : transfers) {
                transfer(senderUsername, transfer.recipient, transfer.amount, transfer.description);
            }
            
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                // Ignore rollback exception
            }
            throw e;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException autoCommitEx) {
                // Ignore auto-commit exception
            }
        }
        
        return errors; // Empty list = success
    }
    
    /**
     * Gets transaction history for a user
     * @param username The username
     * @return List of transactions
     * @throws SQLException if database error occurs
     */
    public List<Transaction> getTransactionHistory(String username) throws SQLException {
        return transactionDAO.getTransactionsByUser(username);
    }
    
    /**
     * Exports transaction history to CSV file
     * @param username The username
     * @param filePath The output file path
     * @throws SQLException if database error occurs
     * @throws IOException if file error occurs
     */
    public void exportTransactions(String username, String filePath) throws SQLException, IOException {
        List<Transaction> transactions = transactionDAO.getTransactionsByUser(username);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("Transaktionsdatum;Empfänger;Sender;Beschreibung;Betrag;Transaktionstyp");
            
            // Write transactions
            for (Transaction t : transactions) {
                String date = t.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String recipient = t.getRecipientUsername() != null ? t.getRecipientUsername() : "";
                String sender = t.getSenderUsername() != null ? t.getSenderUsername() : "";
                String description = t.getDescription() != null ? t.getDescription() : "";
                String amount = t.getAmount().toString();
                String type = t.getTransactionType().name();
                
                writer.println(String.format("%s;%s;%s;%s;%s;%s", date, recipient, sender, description, amount, type));
            }
        }
    }
    
    /**
     * Helper class for mass transfer data
     */
    private static class TransferData {
        String recipient;
        BigDecimal amount;
        String description;
        
        TransferData(String recipient, BigDecimal amount, String description) {
            this.recipient = recipient;
            this.amount = amount;
            this.description = description;
        }
    }
}
