package com.socialmedia.util;

public class PasswordHasherTest {
    
    public static void testPasswordHashing() {
        System.out.println("Testing password hashing...");
        
        String password = "testPassword123";
        String hash = PasswordHasher.hashPassword(password);
        
        assert hash != null : "Hash should not be null";
        assert hash.contains(":") : "Hash should contain salt separator";
        assert !hash.equals(password) : "Hash should not equal plain password";
        
        System.out.println("✓ Password hashing works");
    }
    
    public static void testPasswordVerification() {
        System.out.println("Testing password verification...");
        
        String password = "mySecretPassword";
        String hash = PasswordHasher.hashPassword(password);
        
        assert PasswordHasher.verifyPassword(password, hash) : "Correct password should verify";
        assert !PasswordHasher.verifyPassword("wrongPassword", hash) : "Wrong password should not verify";
        
        System.out.println("✓ Password verification works");
    }
    
    public static void testDifferentHashes() {
        System.out.println("Testing different hashes for same password...");
        
        String password = "samePassword";
        String hash1 = PasswordHasher.hashPassword(password);
        String hash2 = PasswordHasher.hashPassword(password);
        
        assert !hash1.equals(hash2) : "Same password should produce different hashes (different salts)";
        assert PasswordHasher.verifyPassword(password, hash1) : "Password should verify against first hash";
        assert PasswordHasher.verifyPassword(password, hash2) : "Password should verify against second hash";
        
        System.out.println("✓ Different hashes for same password work");
    }
    
    public static void runAllTests() {
        System.out.println("\n=== PasswordHasher Tests ===");
        testPasswordHashing();
        testPasswordVerification();
        testDifferentHashes();
        System.out.println("All PasswordHasher tests passed!\n");
    }
}
