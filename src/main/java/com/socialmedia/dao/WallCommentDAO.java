package com.socialmedia.dao;

import com.socialmedia.model.WallComment;
import com.socialmedia.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WallCommentDAO {
    
    /**
     * Creates a new wall comment
     * @param comment The comment to create
     * @throws SQLException if database error occurs
     */
    public void createComment(WallComment comment) throws SQLException {
        String sql = "INSERT INTO wall_comments (wall_owner_username, commenter_username, comment_text, created_at) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, comment.getWallOwnerUsername());
            pstmt.setString(2, comment.getCommenterUsername());
            pstmt.setString(3, comment.getCommentText());
            pstmt.setTimestamp(4, Timestamp.valueOf(comment.getCreatedAt()));
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    comment.setId(rs.getInt(1));
                }
            }
        }
    }
    
    /**
     * Gets all comments for a user's wall
     * @param wallOwnerUsername The wall owner's username
     * @return List of comments
     * @throws SQLException if database error occurs
     */
    public List<WallComment> getCommentsByWallOwner(String wallOwnerUsername) throws SQLException {
        String sql = "SELECT * FROM wall_comments WHERE wall_owner_username = ? ORDER BY created_at DESC";
        List<WallComment> comments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, wallOwnerUsername);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapResultSetToComment(rs));
                }
            }
        }
        return comments;
    }
    
    /**
     * Maps a ResultSet row to a WallComment object
     * @param rs The ResultSet
     * @return The WallComment object
     * @throws SQLException if database error occurs
     */
    private WallComment mapResultSetToComment(ResultSet rs) throws SQLException {
        WallComment comment = new WallComment();
        comment.setId(rs.getInt("id"));
        comment.setWallOwnerUsername(rs.getString("wall_owner_username"));
        comment.setCommenterUsername(rs.getString("commenter_username"));
        comment.setCommentText(rs.getString("comment_text"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            comment.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return comment;
    }
}
