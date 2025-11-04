package com.socialmedia.dao;

import com.socialmedia.model.DirectMessage;
import com.socialmedia.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DirectMessageDAO {
    
    /**
     * Creates a new direct message
     * @param message The message to create
     * @throws SQLException if database error occurs
     */
    public void createMessage(DirectMessage message) throws SQLException {
        String sql = "INSERT INTO direct_messages (sender_username, recipient_username, message_text, sent_at) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, message.getSenderUsername());
            pstmt.setString(2, message.getRecipientUsername());
            pstmt.setString(3, message.getMessageText());
            pstmt.setTimestamp(4, Timestamp.valueOf(message.getSentAt()));
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                }
            }
        }
    }
    
    /**
     * Gets all messages for a user (sent or received)
     * @param username The username
     * @return List of messages
     * @throws SQLException if database error occurs
     */
    public List<DirectMessage> getMessagesByUser(String username) throws SQLException {
        String sql = "SELECT * FROM direct_messages " +
                     "WHERE sender_username = ? OR recipient_username = ? " +
                     "ORDER BY sent_at DESC";
        List<DirectMessage> messages = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Gets messages between two users (conversation)
     * @param user1 First user
     * @param user2 Second user
     * @return List of messages
     * @throws SQLException if database error occurs
     */
    public List<DirectMessage> getConversation(String user1, String user2) throws SQLException {
        String sql = "SELECT * FROM direct_messages " +
                     "WHERE (sender_username = ? AND recipient_username = ?) " +
                     "   OR (sender_username = ? AND recipient_username = ?) " +
                     "ORDER BY sent_at ASC";
        List<DirectMessage> messages = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user1);
            pstmt.setString(2, user2);
            pstmt.setString(3, user2);
            pstmt.setString(4, user1);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
        }
        return messages;
    }
    
    /**
     * Maps a ResultSet row to a DirectMessage object
     * @param rs The ResultSet
     * @return The DirectMessage object
     * @throws SQLException if database error occurs
     */
    private DirectMessage mapResultSetToMessage(ResultSet rs) throws SQLException {
        DirectMessage message = new DirectMessage();
        message.setId(rs.getInt("id"));
        message.setSenderUsername(rs.getString("sender_username"));
        message.setRecipientUsername(rs.getString("recipient_username"));
        message.setMessageText(rs.getString("message_text"));
        
        Timestamp sentAt = rs.getTimestamp("sent_at");
        if (sentAt != null) {
            message.setSentAt(sentAt.toLocalDateTime());
        }
        
        return message;
    }
}
