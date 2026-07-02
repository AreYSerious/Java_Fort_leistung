package com.socialmedia.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    // Email regex pattern (RFC 5322 simplified)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // CSV line pattern for mass transfer: recipient;amount;description
    private static final Pattern CSV_LINE_PATTERN = Pattern.compile(
        "^([^;]+);([^;]+);(.*)$"
    );
    
    // Decimal number pattern
    private static final Pattern DECIMAL_PATTERN = Pattern.compile(
        "^\\d+(\\.\\d{1,2})?$"
    );
    
    /**
     * Validates if a string is a valid email address
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validates a CSV line for mass transfer
     * @param line The CSV line to validate
     * @return true if valid format, false otherwise
     */
    public static boolean isValidCsvLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }
        return CSV_LINE_PATTERN.matcher(line.trim()).matches();
    }
    
    /**
     * Validates if a string represents a valid positive decimal number
     * @param amount The amount string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            return false;
        }
        return DECIMAL_PATTERN.matcher(amount.trim()).matches();
    }
    
    /**
     * Parses a CSV line and returns the parts
     * @param line The CSV line
     * @return Array of [recipient, amount, description] or null if invalid
     */
    public static String[] parseCsvLine(String line) {
        if (!isValidCsvLine(line)) {
            return null;
        }
        return line.split(";", 3);
    }
}
