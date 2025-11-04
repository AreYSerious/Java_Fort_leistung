package com.socialmedia;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String email;
    private double balance;
    
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.balance = 0.0;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public void addFunds(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }
    
    public boolean deductFunds(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "User{username='" + username + "', email='" + email + "', balance=" + balance + "}";
    }
}
