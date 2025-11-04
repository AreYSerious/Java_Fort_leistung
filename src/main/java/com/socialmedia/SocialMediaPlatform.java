package com.socialmedia;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialMediaPlatform implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Map<String, User> users;
    private List<Post> posts;
    
    public SocialMediaPlatform() {
        this.users = new HashMap<>();
        this.posts = new ArrayList<>();
    }
    
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }
    
    public User getUser(String username) {
        return users.get(username);
    }
    
    public void addPost(Post post) {
        posts.add(post);
    }
    
    public List<Post> getPosts() {
        return new ArrayList<>(posts);
    }
    
    public Map<String, User> getUsers() {
        return new HashMap<>(users);
    }
    
    public boolean purchasePost(String username, Post post) {
        User user = users.get(username);
        if (user != null && user.deductFunds(post.getPrice())) {
            // Transfer funds to post author
            User author = users.get(post.getAuthor());
            if (author != null) {
                author.addFunds(post.getPrice());
            }
            return true;
        }
        return false;
    }
    
    /**
     * Export the platform data to a file at the specified path.
     * @param path The file path where data should be saved
     * @throws IOException if an I/O error occurs
     */
    public void export(String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(this);
        }
    }
    
    /**
     * Load (upload) platform data from a file at the specified path.
     * @param path The file path from where data should be loaded
     * @return The loaded SocialMediaPlatform instance
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class cannot be found during deserialization
     */
    public static SocialMediaPlatform upload(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (SocialMediaPlatform) ois.readObject();
        }
    }
    
    @Override
    public String toString() {
        return "SocialMediaPlatform{users=" + users.size() + ", posts=" + posts.size() + "}";
    }
}
