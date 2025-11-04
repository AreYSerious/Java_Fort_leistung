package com.socialmedia.service;

import com.socialmedia.dao.UserDAO;
import com.socialmedia.model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Gets a user by username
     * @param username The username
     * @return The user, or null if not found
     * @throws SQLException if database error occurs
     */
    public User getUser(String username) throws SQLException {
        return userDAO.findByUsername(username);
    }
    
    /**
     * Searches for users by username pattern
     * @param searchTerm The search term
     * @return List of matching users
     * @throws SQLException if database error occurs
     */
    public List<User> searchUsers(String searchTerm) throws SQLException {
        return userDAO.searchByUsername(searchTerm);
    }
    
    /**
     * Gets all users
     * @return List of all users
     * @throws SQLException if database error occurs
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }
}
