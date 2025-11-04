package com.socialmedia;

import java.io.File;
import java.io.IOException;

public class SocialMediaPlatformTest {
    
    public static void main(String[] args) {
        testBasicFunctionality();
        testExportAndUpload();
        System.out.println("All tests passed!");
    }
    
    private static void testBasicFunctionality() {
        System.out.println("Testing basic functionality...");
        
        SocialMediaPlatform platform = new SocialMediaPlatform();
        
        // Create users
        User user1 = new User("alice", "alice@example.com");
        User user2 = new User("bob", "bob@example.com");
        
        user1.addFunds(100.0);
        user2.addFunds(50.0);
        
        platform.addUser(user1);
        platform.addUser(user2);
        
        // Create post
        Post post = new Post("bob", "Hello World!", 10.0);
        platform.addPost(post);
        
        // Test payment
        assert user1.getBalance() == 100.0 : "User1 balance should be 100.0";
        assert user2.getBalance() == 50.0 : "User2 balance should be 50.0";
        
        boolean success = platform.purchasePost("alice", post);
        assert success : "Purchase should succeed";
        assert user1.getBalance() == 90.0 : "User1 balance should be 90.0 after purchase";
        assert user2.getBalance() == 60.0 : "User2 balance should be 60.0 after receiving payment";
        
        System.out.println("Basic functionality test passed!");
    }
    
    private static void testExportAndUpload() {
        System.out.println("Testing export and upload...");
        
        String testFilePath = "/tmp/test_platform.dat";
        
        try {
            // Create and populate platform
            SocialMediaPlatform platform = new SocialMediaPlatform();
            
            User user1 = new User("charlie", "charlie@example.com");
            user1.addFunds(200.0);
            platform.addUser(user1);
            
            Post post1 = new Post("charlie", "Test Post", 5.0);
            platform.addPost(post1);
            
            // Export
            platform.export(testFilePath);
            System.out.println("Platform exported to: " + testFilePath);
            
            // Upload (load)
            SocialMediaPlatform loadedPlatform = SocialMediaPlatform.upload(testFilePath);
            System.out.println("Platform loaded from: " + testFilePath);
            
            // Verify data
            assert loadedPlatform.getUsers().size() == 1 : "Should have 1 user";
            assert loadedPlatform.getPosts().size() == 1 : "Should have 1 post";
            
            User loadedUser = loadedPlatform.getUser("charlie");
            assert loadedUser != null : "User charlie should exist";
            assert loadedUser.getBalance() == 200.0 : "User balance should be 200.0";
            assert loadedUser.getEmail().equals("charlie@example.com") : "Email should match";
            
            Post loadedPost = loadedPlatform.getPosts().get(0);
            assert loadedPost.getAuthor().equals("charlie") : "Post author should be charlie";
            assert loadedPost.getContent().equals("Test Post") : "Post content should match";
            assert loadedPost.getPrice() == 5.0 : "Post price should be 5.0";
            
            // Cleanup
            File testFile = new File(testFilePath);
            if (testFile.exists()) {
                testFile.delete();
            }
            
            System.out.println("Export and upload test passed!");
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Test failed: " + e.getMessage());
        }
    }
}
