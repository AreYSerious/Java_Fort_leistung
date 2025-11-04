package com.socialmedia.dao;

import com.socialmedia.model.User;
import com.socialmedia.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    /**
     * Creates a new user in the database
     * @param user The user to create
     * @throws SQLException if database error occurs
     */
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, balance) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setBigDecimal(3, user.getBalance());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }
    }
    
    /**
     * Finds a user by username
     * @param username The username to search for
     * @return The user, or null if not found
     * @throws SQLException if database error occurs
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all users
     * @return List of all users
     * @throws SQLException if database error occurs
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    /**
     * Updates a user's balance
     * @param username The username
     * @param newBalance The new balance
     * @throws SQLException if database error occurs
     */
    public void updateBalance(String username, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE users SET balance = ? WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setString(2, username);
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Checks if a username exists
     * @param username The username to check
     * @return true if exists, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean exists(String username) throws SQLException {
        return findByUsername(username) != null;
    }
    
    /**
     * Searches for users by username pattern
     * @param searchTerm The search term
     * @return List of matching users
     * @throws SQLException if database error occurs
     */
    public List<User> searchByUsername(String searchTerm) throws SQLException {
        String sql = "SELECT * FROM users WHERE username LIKE ? ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }
    
    /**
     * Maps a ResultSet row to a User object
     * @param rs The ResultSet
     * @return The User object
     * @throws SQLException if database error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setBalance(rs.getBigDecimal("balance"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return user;
    }
}
