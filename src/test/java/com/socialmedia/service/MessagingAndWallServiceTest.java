package com.socialmedia.service;

import com.socialmedia.model.DirectMessage;
import com.socialmedia.model.WallComment;
import com.socialmedia.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class MessagingAndWallServiceTest {
    
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
    
    public static void testWallComments() throws Exception {
        System.out.println("Testing wall comments...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        WallService wallService = new WallService();
        
        authService.register("wallowner@test.com", "password123");
        authService.register("commenter@test.com", "password123");
        
        // Test posting a comment
        wallService.postComment("wallowner@test.com", "commenter@test.com", "Great wall!");
        
        List<WallComment> comments = wallService.getWallComments("wallowner@test.com");
        assert comments.size() == 1 : "Should have 1 comment";
        assert comments.get(0).getCommentText().equals("Great wall!") : "Comment text should match";
        assert comments.get(0).getCommenterUsername().equals("commenter@test.com") : "Commenter should match";
        System.out.println("✓ Wall comments work");
        
        // Test empty comment
        try {
            wallService.postComment("wallowner@test.com", "commenter@test.com", "");
            assert false : "Should not allow empty comment";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("leer") : "Should have empty comment error";
            System.out.println("✓ Empty comment rejected");
        }
    }
    
    public static void testDirectMessages() throws Exception {
        System.out.println("Testing direct messages...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        MessagingService messagingService = new MessagingService();
        
        authService.register("sender@test.com", "password123");
        authService.register("receiver@test.com", "password123");
        
        // Test sending a message
        messagingService.sendMessage("sender@test.com", "receiver@test.com", "Hello!");
        
        List<DirectMessage> inbox = messagingService.getInbox("receiver@test.com");
        assert inbox.size() == 1 : "Should have 1 message";
        assert inbox.get(0).getMessageText().equals("Hello!") : "Message text should match";
        assert inbox.get(0).getSenderUsername().equals("sender@test.com") : "Sender should match";
        System.out.println("✓ Direct messages work");
        
        // Test self-messaging
        try {
            messagingService.sendMessage("sender@test.com", "sender@test.com", "To myself");
            assert false : "Should not allow self-messaging";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("selbst") : "Should have self-message error";
            System.out.println("✓ Self-messaging prevented");
        }
        
        // Test empty message
        try {
            messagingService.sendMessage("sender@test.com", "receiver@test.com", "");
            assert false : "Should not allow empty message";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("leer") : "Should have empty message error";
            System.out.println("✓ Empty message rejected");
        }
    }
    
    public static void testConversation() throws Exception {
        System.out.println("Testing conversation...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        MessagingService messagingService = new MessagingService();
        
        authService.register("user1@test.com", "password123");
        authService.register("user2@test.com", "password123");
        
        messagingService.sendMessage("user1@test.com", "user2@test.com", "Hi there!");
        messagingService.sendMessage("user2@test.com", "user1@test.com", "Hello back!");
        messagingService.sendMessage("user1@test.com", "user2@test.com", "How are you?");
        
        List<DirectMessage> conversation = messagingService.getConversation("user1@test.com", "user2@test.com");
        
        assert conversation.size() == 3 : "Should have 3 messages in conversation";
        assert conversation.get(0).getMessageText().equals("Hi there!") : "First message should be correct";
        assert conversation.get(2).getMessageText().equals("How are you?") : "Last message should be correct";
        System.out.println("✓ Conversation retrieval works");
    }
    
    public static void testInbox() throws Exception {
        System.out.println("Testing inbox...");
        cleanupDatabase();
        
        AuthService authService = new AuthService();
        MessagingService messagingService = new MessagingService();
        
        authService.register("inbox@test.com", "password123");
        authService.register("sender1@test.com", "password123");
        authService.register("sender2@test.com", "password123");
        
        messagingService.sendMessage("sender1@test.com", "inbox@test.com", "Message 1");
        messagingService.sendMessage("sender2@test.com", "inbox@test.com", "Message 2");
        messagingService.sendMessage("inbox@test.com", "sender1@test.com", "Reply");
        
        List<DirectMessage> inbox = messagingService.getInbox("inbox@test.com");
        
        assert inbox.size() == 3 : "Should have 3 messages (sent and received)";
        System.out.println("✓ Inbox retrieval works");
    }
    
    public static void runAllTests() {
        System.out.println("\n=== Messaging and Wall Service Tests ===");
        try {
            testWallComments();
            testDirectMessages();
            testConversation();
            testInbox();
            System.out.println("All Messaging and Wall Service tests passed!\n");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
