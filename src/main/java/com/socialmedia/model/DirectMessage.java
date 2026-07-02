package com.socialmedia.model;

import java.time.LocalDateTime;

public class DirectMessage {
    private Integer id;
    private String senderUsername;
    private String recipientUsername;
    private String messageText;
    private LocalDateTime sentAt;
    
    public DirectMessage() {}
    
    public DirectMessage(String senderUsername, String recipientUsername, String messageText) {
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.messageText = messageText;
        this.sentAt = LocalDateTime.now();
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    
    public String getMessageText() {
        return messageText;
    }
    
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    @Override
    public String toString() {
        return "DirectMessage{" +
                "from='" + senderUsername + '\'' +
                ", to='" + recipientUsername + '\'' +
                ", text='" + messageText + '\'' +
                ", sentAt=" + sentAt +
                '}';
    }
}
