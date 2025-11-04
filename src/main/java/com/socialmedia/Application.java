package com.socialmedia;

import com.socialmedia.ui.ConsoleUI;
import com.socialmedia.util.DatabaseConnection;

public class Application {
    public static void main(String[] args) {
        try {
            // Initialize database connection
            DatabaseConnection.getConnection();
            
            // Start console UI
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } catch (Exception e) {
            System.err.println("Fehler beim Starten der Anwendung: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}
