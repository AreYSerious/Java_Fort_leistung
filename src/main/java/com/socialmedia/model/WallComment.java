package com.socialmedia.model;

import java.time.LocalDateTime;

public class WallComment {
    private Integer id;
    private String wallOwnerUsername;
    private String commenterUsername;
    private String commentText;
    private LocalDateTime createdAt;
    
    public WallComment() {}
    
    public WallComment(String wallOwnerUsername, String commenterUsername, String commentText) {
        this.wallOwnerUsername = wallOwnerUsername;
        this.commenterUsername = commenterUsername;
        this.commentText = commentText;
        this.createdAt = LocalDateTime.now();
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getWallOwnerUsername() {
        return wallOwnerUsername;
    }
    
    public void setWallOwnerUsername(String wallOwnerUsername) {
        this.wallOwnerUsername = wallOwnerUsername;
    }
    
    public String getCommenterUsername() {
        return commenterUsername;
    }
    
    public void setCommenterUsername(String commenterUsername) {
        this.commenterUsername = commenterUsername;
    }
    
    public String getCommentText() {
        return commentText;
    }
    
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "WallComment{" +
                "commenter='" + commenterUsername + '\'' +
                ", text='" + commentText + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
