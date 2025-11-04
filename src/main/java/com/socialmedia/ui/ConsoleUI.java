package com.socialmedia.ui;

import com.socialmedia.model.DirectMessage;
import com.socialmedia.model.Transaction;
import com.socialmedia.model.User;
import com.socialmedia.model.WallComment;
import com.socialmedia.service.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final AuthService authService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final WallService wallService;
    private final MessagingService messagingService;
    private User currentUser;
    
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthService();
        this.userService = new UserService();
        this.transactionService = new TransactionService();
        this.wallService = new WallService();
        this.messagingService = new MessagingService();
    }
    
    public void start() {
        System.out.println("==========================================");
        System.out.println("  Willkommen zur Social Media Plattform  ");
        System.out.println("==========================================\n");
        
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }
    
    private void showLoginMenu() {
        System.out.println("\n--- Anmeldung ---");
        System.out.println("1. Registrieren");
        System.out.println("2. Anmelden");
        System.out.println("3. Beenden");
        System.out.print("Wahl: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                handleRegister();
                break;
            case "2":
                handleLogin();
                break;
            case "3":
                System.out.println("Auf Wiedersehen!");
                System.exit(0);
                break;
            default:
                System.out.println("Ungültige Wahl!");
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n========== Hauptmenü ==========");
        System.out.println("Eingeloggt als: " + currentUser.getUsername());
        System.out.println("Guthaben: €" + currentUser.getBalance());
        System.out.println("\n1. Guthaben anzeigen");
        System.out.println("2. Einzahlen");
        System.out.println("3. Auszahlen");
        System.out.println("4. Überweisung");
        System.out.println("5. Massenüberweisung (CSV)");
        System.out.println("6. Meine Pinnwand ansehen");
        System.out.println("7. Benutzer suchen und Pinnwand ansehen");
        System.out.println("8. Benutzer suchen und Nachricht senden");
        System.out.println("9. Posteingang ansehen");
        System.out.println("10. Pinnwandnachrichten exportieren");
        System.out.println("11. Direktnachrichten exportieren");
        System.out.println("12. Transaktionen exportieren");
        System.out.println("13. Transaktionshistorie ansehen");
        System.out.println("14. Abmelden");
        System.out.print("Wahl: ");
        
        String choice = scanner.nextLine().trim();
        
        try {
            switch (choice) {
                case "1":
                    handleViewBalance();
                    break;
                case "2":
                    handleDeposit();
                    break;
                case "3":
                    handleWithdraw();
                    break;
                case "4":
                    handleTransfer();
                    break;
                case "5":
                    handleMassTransfer();
                    break;
                case "6":
                    handleViewOwnWall();
                    break;
                case "7":
                    handleSearchAndViewWall();
                    break;
                case "8":
                    handleSearchAndSendMessage();
                    break;
                case "9":
                    handleViewInbox();
                    break;
                case "10":
                    handleExportWall();
                    break;
                case "11":
                    handleExportMessages();
                    break;
                case "12":
                    handleExportTransactions();
                    break;
                case "13":
                    handleViewTransactions();
                    break;
                case "14":
                    currentUser = null;
                    System.out.println("Erfolgreich abgemeldet!");
                    break;
                default:
                    System.out.println("Ungültige Wahl!");
            }
        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void handleRegister() {
        try {
            System.out.print("E-Mail-Adresse (Benutzername): ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Passwort (min. 6 Zeichen): ");
            String password = scanner.nextLine().trim();
            
            authService.register(username, password);
            System.out.println("Registrierung erfolgreich! Sie können sich jetzt anmelden.");
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void handleLogin() {
        try {
            System.out.print("E-Mail-Adresse: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Passwort: ");
            String password = scanner.nextLine().trim();
            
            currentUser = authService.login(username, password);
            System.out.println("Erfolgreich angemeldet!");
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void handleViewBalance() {
        try {
            // Refresh user data
            currentUser = userService.getUser(currentUser.getUsername());
            System.out.println("\n--- Ihr Guthaben ---");
            System.out.println("€" + currentUser.getBalance());
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        }
    }
    
    private void handleDeposit() {
        try {
            System.out.print("Betrag zum Einzahlen: €");
            String amountStr = scanner.nextLine().trim();
            BigDecimal amount = new BigDecimal(amountStr);
            
            transactionService.deposit(currentUser.getUsername(), amount);
            currentUser = userService.getUser(currentUser.getUsername());
            System.out.println("Einzahlung erfolgreich! Neues Guthaben: €" + currentUser.getBalance());
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ungültiger Betrag!");
        } catch (IllegalArgumentException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void handleWithdraw() {
        try {
            System.out.print("Betrag zum Auszahlen: €");
            String amountStr = scanner.nextLine().trim();
            BigDecimal amount = new BigDecimal(amountStr);
            
            transactionService.withdraw(currentUser.getUsername(), amount);
            currentUser = userService.getUser(currentUser.getUsername());
            System.out.println("Auszahlung erfolgreich! Neues Guthaben: €" + currentUser.getBalance());
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ungültiger Betrag!");
        } catch (IllegalArgumentException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void handleTransfer() {
        try {
            System.out.print("Empfänger (E-Mail): ");
            String recipient = scanner.nextLine().trim();
            
            System.out.print("Betrag: €");
            String amountStr = scanner.nextLine().trim();
            BigDecimal amount = new BigDecimal(amountStr);
            
            System.out.print("Beschreibung: ");
            String description = scanner.nextLine().trim();
            
            transactionService.transfer(currentUser.getUsername(), recipient, amount, description);
            currentUser = userService.getUser(currentUser.getUsername());
            System.out.println("Überweisung erfolgreich! Neues Guthaben: €" + currentUser.getBalance());
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ungültiger Betrag!");
        } catch (IllegalArgumentException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void handleMassTransfer() {
        try {
            System.out.print("Pfad zur CSV-Datei: ");
            String filePath = scanner.nextLine().trim();
            
            List<String> errors = transactionService.massTransfer(currentUser.getUsername(), filePath);
            
            if (errors.isEmpty()) {
                currentUser = userService.getUser(currentUser.getUsername());
                System.out.println("Massenüberweisung erfolgreich! Neues Guthaben: €" + currentUser.getBalance());
            } else {
                System.out.println("\nFehler bei der Massenüberweisung:");
                for (String error : errors) {
                    System.out.println("  - " + error);
                }
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Dateifehler: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void handleViewOwnWall() {
        try {
            List<WallComment> comments = wallService.getWallComments(currentUser.getUsername());
            
            System.out.println("\n--- Meine Pinnwand ---");
            if (comments.isEmpty()) {
                System.out.println("Keine Kommentare vorhanden.");
            } else {
                for (WallComment comment : comments) {
                    System.out.println("\nVon: " + comment.getCommenterUsername());
                    System.out.println("Am: " + comment.getCreatedAt());
                    System.out.println("Nachricht: " + comment.getCommentText());
                    System.out.println("---");
                }
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        }
    }
    
    private void handleSearchAndViewWall() {
        try {
            System.out.print("Benutzer suchen: ");
            String searchTerm = scanner.nextLine().trim();
            
            List<User> users = userService.searchUsers(searchTerm);
            
            if (users.isEmpty()) {
                System.out.println("Keine Benutzer gefunden.");
                return;
            }
            
            System.out.println("\nGefundene Benutzer:");
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i).getUsername());
            }
            
            System.out.print("\nBenutzer auswählen (Nummer): ");
            int choice = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (choice < 0 || choice >= users.size()) {
                System.out.println("Ungültige Auswahl!");
                return;
            }
            
            User selectedUser = users.get(choice);
            List<WallComment> comments = wallService.getWallComments(selectedUser.getUsername());
            
            System.out.println("\n--- Pinnwand von " + selectedUser.getUsername() + " ---");
            if (comments.isEmpty()) {
                System.out.println("Keine Kommentare vorhanden.");
            } else {
                for (WallComment comment : comments) {
                    System.out.println("\nVon: " + comment.getCommenterUsername());
                    System.out.println("Am: " + comment.getCreatedAt());
                    System.out.println("Nachricht: " + comment.getCommentText());
                    System.out.println("---");
                }
            }
            
            System.out.print("\nMöchten Sie einen Kommentar hinterlassen? (j/n): ");
            String answer = scanner.nextLine().trim();
            
            if (answer.equalsIgnoreCase("j")) {
                System.out.print("Ihr Kommentar: ");
                String comment = scanner.nextLine().trim();
                wallService.postComment(selectedUser.getUsername(), currentUser.getUsername(), comment);
                System.out.println("Kommentar erfolgreich gepostet!");
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ungültige Eingabe!");
        } catch (IllegalArgumentException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void handleSearchAndSendMessage() {
        try {
            System.out.print("Benutzer suchen: ");
            String searchTerm = scanner.nextLine().trim();
            
            List<User> users = userService.searchUsers(searchTerm);
            
            if (users.isEmpty()) {
                System.out.println("Keine Benutzer gefunden.");
                return;
            }
            
            System.out.println("\nGefundene Benutzer:");
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i).getUsername());
            }
            
            System.out.print("\nBenutzer auswählen (Nummer): ");
            int choice = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (choice < 0 || choice >= users.size()) {
                System.out.println("Ungültige Auswahl!");
                return;
            }
            
            User selectedUser = users.get(choice);
            
            System.out.print("Ihre Nachricht: ");
            String message = scanner.nextLine().trim();
            
            messagingService.sendMessage(currentUser.getUsername(), selectedUser.getUsername(), message);
            System.out.println("Nachricht erfolgreich gesendet!");
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ungültige Eingabe!");
        } catch (IllegalArgumentException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
    
    private void handleViewInbox() {
        try {
            List<DirectMessage> messages = messagingService.getInbox(currentUser.getUsername());
            
            System.out.println("\n--- Posteingang ---");
            if (messages.isEmpty()) {
                System.out.println("Keine Nachrichten vorhanden.");
            } else {
                for (DirectMessage msg : messages) {
                    System.out.println("\nVon: " + msg.getSenderUsername());
                    System.out.println("An: " + msg.getRecipientUsername());
                    System.out.println("Am: " + msg.getSentAt());
                    System.out.println("Nachricht: " + msg.getMessageText());
                    System.out.println("---");
                }
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        }
    }
    
    private void handleExportWall() {
        try {
            System.out.print("Pfad für Export-Datei: ");
            String filePath = scanner.nextLine().trim();
            
            wallService.exportWallComments(currentUser.getUsername(), filePath);
            System.out.println("Pinnwandnachrichten erfolgreich exportiert nach: " + filePath);
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Dateifehler: " + e.getMessage());
        }
    }
    
    private void handleExportMessages() {
        try {
            System.out.print("Benutzer (E-Mail) für Konversationsexport: ");
            String otherUser = scanner.nextLine().trim();
            
            System.out.print("Pfad für Export-Datei: ");
            String filePath = scanner.nextLine().trim();
            
            messagingService.exportConversation(currentUser.getUsername(), otherUser, filePath);
            System.out.println("Direktnachrichten erfolgreich exportiert nach: " + filePath);
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Dateifehler: " + e.getMessage());
        }
    }
    
    private void handleExportTransactions() {
        try {
            System.out.print("Pfad für Export-Datei: ");
            String filePath = scanner.nextLine().trim();
            
            transactionService.exportTransactions(currentUser.getUsername(), filePath);
            System.out.println("Transaktionen erfolgreich exportiert nach: " + filePath);
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Dateifehler: " + e.getMessage());
        }
    }
    
    private void handleViewTransactions() {
        try {
            List<Transaction> transactions = transactionService.getTransactionHistory(currentUser.getUsername());
            
            System.out.println("\n--- Transaktionshistorie ---");
            if (transactions.isEmpty()) {
                System.out.println("Keine Transaktionen vorhanden.");
            } else {
                BigDecimal total = BigDecimal.ZERO;
                for (Transaction t : transactions) {
                    System.out.println("\nDatum: " + t.getTransactionDate());
                    System.out.println("Typ: " + t.getTransactionType());
                    System.out.println("Betrag: €" + t.getAmount());
                    
                    if (t.getSenderUsername() != null) {
                        System.out.println("Sender: " + t.getSenderUsername());
                        if (t.getSenderUsername().equals(currentUser.getUsername())) {
                            total = total.subtract(t.getAmount());
                        }
                    }
                    
                    if (t.getRecipientUsername() != null) {
                        System.out.println("Empfänger: " + t.getRecipientUsername());
                        if (t.getRecipientUsername().equals(currentUser.getUsername())) {
                            total = total.add(t.getAmount());
                        }
                    }
                    
                    if (t.getDescription() != null && !t.getDescription().isEmpty()) {
                        System.out.println("Beschreibung: " + t.getDescription());
                    }
                    System.out.println("---");
                }
                
                System.out.println("\nSumme aller Transaktionen: €" + total);
                System.out.println("Aktuelles Guthaben: €" + currentUser.getBalance());
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
        }
    }
}
