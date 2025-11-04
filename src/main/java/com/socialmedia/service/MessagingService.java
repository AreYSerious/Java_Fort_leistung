package com.socialmedia.service;

import com.socialmedia.dao.DirectMessageDAO;
import com.socialmedia.model.DirectMessage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessagingService {
    private final DirectMessageDAO directMessageDAO;
    
    public MessagingService() {
        this.directMessageDAO = new DirectMessageDAO();
    }
    
    /**
     * Sends a direct message to another user
     * @param senderUsername The sender
     * @param recipientUsername The recipient
     * @param messageText The message text
     * @throws SQLException if database error occurs
     */
    public void sendMessage(String senderUsername, String recipientUsername, String messageText) throws SQLException {
        if (messageText == null || messageText.trim().isEmpty()) {
            throw new IllegalArgumentException("Nachricht darf nicht leer sein");
        }
        
        if (senderUsername.equals(recipientUsername)) {
            throw new IllegalArgumentException("Sie können sich nicht selbst eine Nachricht senden");
        }
        
        DirectMessage message = new DirectMessage(senderUsername, recipientUsername, messageText);
        message.setSentAt(LocalDateTime.now());
        directMessageDAO.createMessage(message);
    }
    
    /**
     * Gets all messages for a user (inbox)
     * @param username The username
     * @return List of messages
     * @throws SQLException if database error occurs
     */
    public List<DirectMessage> getInbox(String username) throws SQLException {
        return directMessageDAO.getMessagesByUser(username);
    }
    
    /**
     * Gets conversation between two users
     * @param user1 First user
     * @param user2 Second user
     * @return List of messages
     * @throws SQLException if database error occurs
     */
    public List<DirectMessage> getConversation(String user1, String user2) throws SQLException {
        return directMessageDAO.getConversation(user1, user2);
    }
    
    /**
     * Exports conversation with a specific user to CSV file
     * @param currentUser The current user
     * @param otherUser The other user in the conversation
     * @param filePath The output file path
     * @throws SQLException if database error occurs
     * @throws IOException if file error occurs
     */
    public void exportConversation(String currentUser, String otherUser, String filePath) throws SQLException, IOException {
        List<DirectMessage> messages = directMessageDAO.getConversation(currentUser, otherUser);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("Zeitpunkt der Nachricht;Sender;Empfänger;Nachricht");
            
            // Write messages
            for (DirectMessage m : messages) {
                String timestamp = m.getSentAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String sender = m.getSenderUsername();
                String recipient = m.getRecipientUsername();
                String message = m.getMessageText();
                
                writer.println(String.format("%s;%s;%s;%s", timestamp, sender, recipient, message));
            }
        }
    }
}
