package com.socialmedia.service;

import com.socialmedia.dao.UserDAO;
import com.socialmedia.model.User;
import com.socialmedia.util.PasswordHasher;
import com.socialmedia.util.ValidationUtil;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Registers a new user
     * @param username The username (must be a valid email)
     * @param password The password
     * @throws IllegalArgumentException if username is not a valid email or user already exists
     * @throws SQLException if database error occurs
     */
    public void register(String username, String password) throws SQLException {
        if (!ValidationUtil.isValidEmail(username)) {
            throw new IllegalArgumentException("Benutzername muss eine gültige E-Mail-Adresse sein");
        }
        
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Passwort muss mindestens 6 Zeichen lang sein");
        }
        
        if (userDAO.exists(username)) {
            throw new IllegalArgumentException("Benutzer existiert bereits");
        }
        
        String passwordHash = PasswordHasher.hashPassword(password);
        User user = new User(username, passwordHash);
        userDAO.createUser(user);
    }
    
    /**
     * Logs in a user
     * @param username The username
     * @param password The password
     * @return The user if login successful
     * @throws IllegalArgumentException if credentials are invalid
     * @throws SQLException if database error occurs
     */
    public User login(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);
        
        if (user == null) {
            throw new IllegalArgumentException("Ungültiger Benutzername oder Passwort");
        }
        
        if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Ungültiger Benutzername oder Passwort");
        }
        
        return user;
    }
}
