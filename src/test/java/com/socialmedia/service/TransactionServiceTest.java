package com.socialmedia.service;

import com.socialmedia.model.Transaction;
import com.socialmedia.model.User;
import com.socialmedia.util.DatabaseConnection;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class TransactionServiceTest {
    
    private static void cleanupDatabase() throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM direct_messages");
                stmt.execute("DELETE FROM wall_comments");
                stmt.execute("DELETE FROM transactions");
                stmt.execute("DELETE FROM users");
            }
            conn.commit();
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }
    
    public static void testDeposit() throws Exception {
        System.out.println("Testing deposit...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        TransactionService transactionService = new TransactionService();
        UserService userService = new UserService();
        
        authService.register("deposit@test.com", "password123");
        
        // Test valid deposit
        transactionService.deposit("deposit@test.com", new BigDecimal("100.00"));
        User user = userService.getUser("deposit@test.com");
        assert user.getBalance().compareTo(new BigDecimal("100.00")) == 0 : "Balance should be 100.00";
        System.out.println("✓ Deposit works");
        
        // Test invalid amount
        try {
            transactionService.deposit("deposit@test.com", new BigDecimal("-50.00"));
            assert false : "Should not allow negative deposit";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("größer als 0") : "Should have proper error message";
            System.out.println("✓ Negative deposit rejected");
        }
    }
    
    public static void testWithdraw() throws Exception {
        System.out.println("Testing withdraw...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        TransactionService transactionService = new TransactionService();
        UserService userService = new UserService();
        
        authService.register("withdraw@test.com", "password123");
        transactionService.deposit("withdraw@test.com", new BigDecimal("100.00"));
        
        // Test valid withdrawal
        transactionService.withdraw("withdraw@test.com", new BigDecimal("30.00"));
        User user = userService.getUser("withdraw@test.com");
        assert user.getBalance().compareTo(new BigDecimal("70.00")) == 0 : "Balance should be 70.00";
        System.out.println("✓ Withdrawal works");
        
        // Test insufficient funds
        try {
            transactionService.withdraw("withdraw@test.com", new BigDecimal("100.00"));
            assert false : "Should not allow overdraft";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("Unzureichendes") : "Should have insufficient funds message";
            System.out.println("✓ Overdraft prevented");
        }
    }
    
    public static void testTransfer() throws Exception {
        System.out.println("Testing transfer...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        TransactionService transactionService = new TransactionService();
        UserService userService = new UserService();
        
        authService.register("sender@test.com", "password123");
        authService.register("recipient@test.com", "password123");
        
        transactionService.deposit("sender@test.com", new BigDecimal("100.00"));
        
        // Test valid transfer
        transactionService.transfer("sender@test.com", "recipient@test.com", new BigDecimal("40.00"), "Test payment");
        
        User sender = userService.getUser("sender@test.com");
        User recipient = userService.getUser("recipient@test.com");
        
        assert sender.getBalance().compareTo(new BigDecimal("60.00")) == 0 : "Sender balance should be 60.00";
        assert recipient.getBalance().compareTo(new BigDecimal("40.00")) == 0 : "Recipient balance should be 40.00";
        System.out.println("✓ Transfer works");
        
        // Test self-transfer
        try {
            transactionService.transfer("sender@test.com", "sender@test.com", new BigDecimal("10.00"), "Self");
            assert false : "Should not allow self-transfer";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("sich selbst") : "Should have self-transfer error";
            System.out.println("✓ Self-transfer prevented");
        }
        
        // Test non-existent recipient
        try {
            transactionService.transfer("sender@test.com", "nonexistent@test.com", new BigDecimal("10.00"), "Test");
            assert false : "Should not allow transfer to non-existent user";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("nicht gefunden") : "Should have not found error";
            System.out.println("✓ Transfer to non-existent user prevented");
        }
    }
    
    public static void testMassTransfer() throws Exception {
        System.out.println("Testing mass transfer...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        TransactionService transactionService = new TransactionService();
        UserService userService = new UserService();
        
        authService.register("mass@test.com", "password123");
        authService.register("recipient1@test.com", "password123");
        authService.register("recipient2@test.com", "password123");
        
        transactionService.deposit("mass@test.com", new BigDecimal("1000.00"));
        
        // Create CSV file
        String csvPath = "/tmp/test_mass_transfer.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            writer.println("Empfänger;Betrag;Beschreibung");
            writer.println("recipient1@test.com;100.50;Payment 1");
            writer.println("recipient2@test.com;200.75;Payment 2");
        }
        
        // Test valid mass transfer
        List<String> errors = transactionService.massTransfer("mass@test.com", csvPath);
        assert errors.isEmpty() : "Mass transfer should succeed";
        
        User sender = userService.getUser("mass@test.com");
        User recipient1 = userService.getUser("recipient1@test.com");
        User recipient2 = userService.getUser("recipient2@test.com");
        
        assert sender.getBalance().compareTo(new BigDecimal("698.75")) == 0 : "Sender balance should be correct";
        assert recipient1.getBalance().compareTo(new BigDecimal("100.50")) == 0 : "Recipient1 balance should be correct";
        assert recipient2.getBalance().compareTo(new BigDecimal("200.75")) == 0 : "Recipient2 balance should be correct";
        System.out.println("✓ Mass transfer works");
        
        // Test insufficient funds
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            writer.println("recipient1@test.com;1000.00;Too much");
        }
        
        errors = transactionService.massTransfer("mass@test.com", csvPath);
        assert !errors.isEmpty() : "Should fail with insufficient funds";
        assert errors.get(0).contains("Unzureichendes") : "Should have insufficient funds error";
        System.out.println("✓ Mass transfer with insufficient funds prevented");
        
        // Test invalid line format
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            writer.println("invalid;line");
        }
        
        errors = transactionService.massTransfer("mass@test.com", csvPath);
        assert !errors.isEmpty() : "Should fail with invalid format";
        assert errors.get(0).contains("Ungültiges Format") : "Should have format error";
        System.out.println("✓ Invalid CSV format detected");
        
        // Test negative amount
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            writer.println("recipient1@test.com;-10.00;Negative");
        }
        
        errors = transactionService.massTransfer("mass@test.com", csvPath);
        assert !errors.isEmpty() : "Should fail with negative amount";
        System.out.println("✓ Negative amount in mass transfer detected");
        
        // Test self-transfer in CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvPath))) {
            writer.println("mass@test.com;10.00;Self");
        }
        
        errors = transactionService.massTransfer("mass@test.com", csvPath);
        assert !errors.isEmpty() : "Should fail with self-transfer";
        assert errors.get(0).contains("sich selbst") : "Should have self-transfer error";
        System.out.println("✓ Self-transfer in mass transfer detected");
        
        // Cleanup
        new File(csvPath).delete();
    }
    
    public static void testTransactionHistory() throws Exception {
        System.out.println("Testing transaction history...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        TransactionService transactionService = new TransactionService();
        
        authService.register("history@test.com", "password123");
        
        transactionService.deposit("history@test.com", new BigDecimal("100.00"));
        transactionService.withdraw("history@test.com", new BigDecimal("20.00"));
        
        List<Transaction> transactions = transactionService.getTransactionHistory("history@test.com");
        
        assert transactions.size() == 2 : "Should have 2 transactions";
        System.out.println("✓ Transaction history works");
    }
    
    public static void testAccountBalanceConsistency() throws Exception {
        System.out.println("Testing account balance consistency...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        TransactionService transactionService = new TransactionService();
        UserService userService = new UserService();
        
        authService.register("consistency@test.com", "password123");
        
        transactionService.deposit("consistency@test.com", new BigDecimal("100.00"));
        transactionService.deposit("consistency@test.com", new BigDecimal("50.00"));
        transactionService.withdraw("consistency@test.com", new BigDecimal("30.00"));
        
        List<Transaction> transactions = transactionService.getTransactionHistory("consistency@test.com");
        
        BigDecimal calculatedBalance = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.getRecipientUsername() != null && t.getRecipientUsername().equals("consistency@test.com")) {
                calculatedBalance = calculatedBalance.add(t.getAmount());
            }
            if (t.getSenderUsername() != null && t.getSenderUsername().equals("consistency@test.com")) {
                calculatedBalance = calculatedBalance.subtract(t.getAmount());
            }
        }
        
        User user = userService.getUser("consistency@test.com");
        assert user.getBalance().compareTo(calculatedBalance) == 0 : "Balance should match transaction sum";
        assert user.getBalance().compareTo(new BigDecimal("120.00")) == 0 : "Balance should be 120.00";
        System.out.println("✓ Account balance consistency verified");
    }
    
    public static void runAllTests() {
        System.out.println("\n=== TransactionService Tests ===");
        try {
            testDeposit();
            testWithdraw();
            testTransfer();
            testMassTransfer();
            testTransactionHistory();
            testAccountBalanceConsistency();
            System.out.println("All TransactionService tests passed!\n");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
