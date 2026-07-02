package com.socialmedia;

import com.socialmedia.service.AuthServiceTest;
import com.socialmedia.service.MessagingAndWallServiceTest;
import com.socialmedia.service.TransactionServiceTest;
import com.socialmedia.util.PasswordHasherTest;
import com.socialmedia.util.ValidationUtilTest;

public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     Social Media Platform - Test Suite                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        try {
            // Run utility tests
            PasswordHasherTest.runAllTests();
            ValidationUtilTest.runAllTests();
            
            // Run service tests
            AuthServiceTest.runAllTests();
            TransactionServiceTest.runAllTests();
            MessagingAndWallServiceTest.runAllTests();
            
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║     ALL TESTS PASSED SUCCESSFULLY! ✓                       ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            
        } catch (AssertionError e) {
            System.err.println("\n✗ TEST FAILED: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\n✗ TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
