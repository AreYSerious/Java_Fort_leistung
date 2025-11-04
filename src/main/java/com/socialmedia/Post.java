package com.socialmedia;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String author;
    private String content;
    private LocalDateTime timestamp;
    private double price;
    
    public Post(String author, String content, double price) {
        this.author = author;
        this.content = content;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "Post{author='" + author + "', content='" + content + "', price=" + price + ", timestamp=" + timestamp + "}";
    }
}
