package com.socialmedia.util;

public class ValidationUtilTest {
    
    public static void testEmailValidation() {
        System.out.println("Testing email validation...");
        
        // Valid emails
        assert ValidationUtil.isValidEmail("test@example.com") : "Valid email should pass";
        assert ValidationUtil.isValidEmail("user.name@domain.co.uk") : "Email with dots should pass";
        assert ValidationUtil.isValidEmail("user+tag@example.com") : "Email with plus should pass";
        
        // Invalid emails
        assert !ValidationUtil.isValidEmail("invalid") : "Invalid email should fail";
        assert !ValidationUtil.isValidEmail("@example.com") : "Email without local part should fail";
        assert !ValidationUtil.isValidEmail("user@") : "Email without domain should fail";
        assert !ValidationUtil.isValidEmail("") : "Empty email should fail";
        assert !ValidationUtil.isValidEmail(null) : "Null email should fail";
        
        System.out.println("✓ Email validation works");
    }
    
    public static void testCsvLineValidation() {
        System.out.println("Testing CSV line validation...");
        
        // Valid CSV lines
        assert ValidationUtil.isValidCsvLine("user@test.com;100.50;Payment") : "Valid CSV line should pass";
        assert ValidationUtil.isValidCsvLine("test@mail.com;50;") : "CSV line with empty description should pass";
        assert ValidationUtil.isValidCsvLine("a@b.com;10;Some;Description;With;Semicolons") : "CSV line with semicolons in description should pass";
        
        // Invalid CSV lines
        assert !ValidationUtil.isValidCsvLine("invalid") : "Invalid CSV line should fail";
        assert !ValidationUtil.isValidCsvLine("only;one") : "CSV line with only two fields should fail";
        assert !ValidationUtil.isValidCsvLine("") : "Empty CSV line should fail";
        assert !ValidationUtil.isValidCsvLine(null) : "Null CSV line should fail";
        
        System.out.println("✓ CSV line validation works");
    }
    
    public static void testAmountValidation() {
        System.out.println("Testing amount validation...");
        
        // Valid amounts
        assert ValidationUtil.isValidAmount("100") : "Integer amount should be valid";
        assert ValidationUtil.isValidAmount("100.50") : "Decimal amount should be valid";
        assert ValidationUtil.isValidAmount("0.99") : "Small decimal should be valid";
        assert ValidationUtil.isValidAmount("1000000") : "Large amount should be valid";
        
        // Invalid amounts
        assert !ValidationUtil.isValidAmount("-100") : "Negative amount should be invalid";
        assert !ValidationUtil.isValidAmount("abc") : "Non-numeric should be invalid";
        assert !ValidationUtil.isValidAmount("100.999") : "More than 2 decimals should be invalid";
        assert !ValidationUtil.isValidAmount("") : "Empty amount should be invalid";
        assert !ValidationUtil.isValidAmount(null) : "Null amount should be invalid";
        
        System.out.println("✓ Amount validation works");
    }
    
    public static void testCsvLineParsing() {
        System.out.println("Testing CSV line parsing...");
        
        String line = "user@test.com;100.50;Payment for services";
        String[] parts = ValidationUtil.parseCsvLine(line);
        
        assert parts != null : "Parsing should succeed";
        assert parts.length == 3 : "Should have 3 parts";
        assert parts[0].equals("user@test.com") : "First part should be recipient";
        assert parts[1].equals("100.50") : "Second part should be amount";
        assert parts[2].equals("Payment for services") : "Third part should be description";
        
        // Test with semicolons in description
        String line2 = "test@mail.com;50;Description;with;semicolons";
        String[] parts2 = ValidationUtil.parseCsvLine(line2);
        assert parts2[2].equals("Description;with;semicolons") : "Description with semicolons should be preserved";
        
        System.out.println("✓ CSV line parsing works");
    }
    
    public static void runAllTests() {
        System.out.println("\n=== ValidationUtil Tests ===");
        testEmailValidation();
        testCsvLineValidation();
        testAmountValidation();
        testCsvLineParsing();
        System.out.println("All ValidationUtil tests passed!\n");
    }
}
