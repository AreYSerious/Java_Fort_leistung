package com.socialmedia;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Social Media Platform - Export und Upload Demo");
        System.out.println("================================================\n");
        
        // Neue Plattform erstellen
        SocialMediaPlatform platform = new SocialMediaPlatform();
        
        // Benutzer erstellen
        User alice = new User("alice", "alice@example.com");
        alice.addFunds(100.0);
        
        User bob = new User("bob", "bob@example.com");
        bob.addFunds(50.0);
        
        platform.addUser(alice);
        platform.addUser(bob);
        
        System.out.println("Benutzer erstellt:");
        System.out.println("  " + alice);
        System.out.println("  " + bob);
        System.out.println();
        
        // Posts erstellen
        Post post1 = new Post("bob", "Willkommen auf meiner Plattform!", 5.0);
        Post post2 = new Post("alice", "Java Fortgeschritten - Tutorial", 15.0);
        
        platform.addPost(post1);
        platform.addPost(post2);
        
        System.out.println("Posts erstellt:");
        System.out.println("  " + post1);
        System.out.println("  " + post2);
        System.out.println();
        
        // Alice kauft Bobs Post
        System.out.println("Alice kauft Bobs Post für 5.0...");
        boolean success = platform.purchasePost("alice", post1);
        if (success) {
            System.out.println("  Kauf erfolgreich!");
            System.out.println("  Alice Guthaben: " + platform.getUser("alice").getBalance());
            System.out.println("  Bob Guthaben: " + platform.getUser("bob").getBalance());
        }
        System.out.println();
        
        // Export - nur Pfad wird angegeben
        String exportPath = "/tmp/socialmedia_platform.dat";
        try {
            System.out.println("Plattform wird exportiert nach: " + exportPath);
            platform.export(exportPath);
            System.out.println("  Export erfolgreich!");
            System.out.println();
            
            // Upload (Laden) - nur Pfad wird angegeben
            System.out.println("Plattform wird geladen von: " + exportPath);
            SocialMediaPlatform loadedPlatform = SocialMediaPlatform.upload(exportPath);
            System.out.println("  Upload erfolgreich!");
            System.out.println("  " + loadedPlatform);
            System.out.println();
            
            // Geladene Daten anzeigen
            System.out.println("Geladene Benutzer:");
            for (User user : loadedPlatform.getUsers().values()) {
                System.out.println("  " + user);
            }
            System.out.println();
            
            System.out.println("Geladene Posts:");
            for (Post post : loadedPlatform.getPosts()) {
                System.out.println("  " + post);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Fehler beim Export/Upload: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
