package com.socialmedia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    
    public User() {
        this.balance = BigDecimal.ZERO;
    }
    
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.balance = BigDecimal.ZERO;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "User{username='" + username + "', balance=" + balance + "}";
    }
}
