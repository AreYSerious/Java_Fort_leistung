package com.socialmedia.service;

import com.socialmedia.model.User;
import com.socialmedia.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;

public class AuthServiceTest {
    
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
    
    public static void testRegistration() throws Exception {
        System.out.println("Testing user registration...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        
        // Test valid registration
        authService.register("test@example.com", "password123");
        System.out.println("✓ Valid registration works");
        
        // Test duplicate registration
        try {
            authService.register("test@example.com", "password456");
            assert false : "Should not allow duplicate registration";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("existiert bereits") : "Should have proper error message";
            System.out.println("✓ Duplicate registration prevented");
        }
        
        // Test invalid email
        try {
            authService.register("notanemail", "password123");
            assert false : "Should not allow invalid email";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("E-Mail") : "Should have email error message";
            System.out.println("✓ Invalid email rejected");
        }
        
        // Test short password
        try {
            authService.register("new@test.com", "12345");
            assert false : "Should not allow short password";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("6 Zeichen") : "Should have password length error";
            System.out.println("✓ Short password rejected");
        }
    }
    
    public static void testLogin() throws Exception {
        System.out.println("Testing user login...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        
        // Register a user first
        authService.register("login@test.com", "mypassword");
        
        // Test valid login
        User user = authService.login("login@test.com", "mypassword");
        assert user != null : "Login should succeed";
        assert user.getUsername().equals("login@test.com") : "Username should match";
        System.out.println("✓ Valid login works");
        
        // Test invalid password
        try {
            authService.login("login@test.com", "wrongpassword");
            assert false : "Should not allow wrong password";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("Ungültiger") : "Should have invalid credentials message";
            System.out.println("✓ Invalid password rejected");
        }
        
        // Test non-existent user
        try {
            authService.login("nonexistent@test.com", "password");
            assert false : "Should not allow non-existent user";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("Ungültiger") : "Should have invalid credentials message";
            System.out.println("✓ Non-existent user rejected");
        }
    }
    
    public static void runAllTests() {
        System.out.println("\n=== AuthService Tests ===");
        try {
            testRegistration();
            testLogin();
            System.out.println("All AuthService tests passed!\n");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
