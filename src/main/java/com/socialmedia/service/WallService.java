package com.socialmedia.service;

import com.socialmedia.dao.WallCommentDAO;
import com.socialmedia.model.WallComment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WallService {
    private final WallCommentDAO wallCommentDAO;
    
    public WallService() {
        this.wallCommentDAO = new WallCommentDAO();
    }
    
    /**
     * Posts a comment on a user's wall
     * @param wallOwnerUsername The wall owner
     * @param commenterUsername The commenter
     * @param commentText The comment text
     * @throws SQLException if database error occurs
     */
    public void postComment(String wallOwnerUsername, String commenterUsername, String commentText) throws SQLException {
        if (commentText == null || commentText.trim().isEmpty()) {
            throw new IllegalArgumentException("Kommentar darf nicht leer sein");
        }
        
        WallComment comment = new WallComment(wallOwnerUsername, commenterUsername, commentText);
        comment.setCreatedAt(LocalDateTime.now());
        wallCommentDAO.createComment(comment);
    }
    
    /**
     * Gets all comments on a user's wall
     * @param wallOwnerUsername The wall owner
     * @return List of comments
     * @throws SQLException if database error occurs
     */
    public List<WallComment> getWallComments(String wallOwnerUsername) throws SQLException {
        return wallCommentDAO.getCommentsByWallOwner(wallOwnerUsername);
    }
    
    /**
     * Exports wall comments to CSV file
     * @param wallOwnerUsername The wall owner
     * @param filePath The output file path
     * @throws SQLException if database error occurs
     * @throws IOException if file error occurs
     */
    public void exportWallComments(String wallOwnerUsername, String filePath) throws SQLException, IOException {
        List<WallComment> comments = wallCommentDAO.getCommentsByWallOwner(wallOwnerUsername);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("Zeitpunkt der Nachricht;Sender;Empfänger;Nachricht");
            
            // Write comments
            for (WallComment c : comments) {
                String timestamp = c.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String sender = c.getCommenterUsername();
                String recipient = c.getWallOwnerUsername();
                String message = c.getCommentText();
                
                writer.println(String.format("%s;%s;%s;%s", timestamp, sender, recipient, message));
            }
        }
    }
}
