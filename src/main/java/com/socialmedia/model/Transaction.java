package com.socialmedia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Integer id;
    private LocalDateTime transactionDate;
    private String senderUsername;
    private String recipientUsername;
    private String description;
    private BigDecimal amount;
    private TransactionType transactionType;
    
    public enum TransactionType {
        EINZAHLUNG,
        AUSZAHLUNG,
        UEBERWEISUNG
    }
    
    public Transaction() {}
    
    public Transaction(String senderUsername, String recipientUsername, 
                      String description, BigDecimal amount, TransactionType transactionType) {
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.description = description;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionDate = LocalDateTime.now();
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public String getSenderUsername() {
        return senderUsername;
    }
    
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
    
    public String getRecipientUsername() {
        return recipientUsername;
    }
    
    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + transactionDate +
                ", sender='" + senderUsername + '\'' +
                ", recipient='" + recipientUsername + '\'' +
                ", amount=" + amount +
                ", type=" + transactionType +
                '}';
    }
}
