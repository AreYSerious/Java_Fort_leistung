# Java_Fort_leistung

## Social Media Platform mit Bezahlfunktion

Eine Social-Media-Plattform mit Bezahlfunktionalität, Export und Upload (Speichern/Laden) von Daten.

### Funktionen

- **Benutzer-Verwaltung**: Erstellen und Verwalten von Benutzern mit E-Mail und Guthaben
- **Post-Verwaltung**: Erstellen von Posts mit Preis
- **Bezahlfunktion**: Kauf von Posts durch Übertragung von Guthaben zwischen Benutzern
- **Export**: Speichern der Plattform-Daten in eine Datei (nur Pfad erforderlich)
- **Upload**: Laden der Plattform-Daten aus einer Datei (nur Pfad erforderlich)

### Kompilierung

```bash
mkdir -p build/classes
javac -d build/classes src/main/java/com/socialmedia/*.java
javac -cp build/classes -d build/classes src/test/java/com/socialmedia/*.java
```

### Tests ausführen

```bash
java -cp build/classes -ea com.socialmedia.SocialMediaPlatformTest
```

### Verwendung

```java
// Neue Plattform erstellen
SocialMediaPlatform platform = new SocialMediaPlatform();

// Benutzer hinzufügen
User user = new User("username", "email@example.com");
user.addFunds(100.0);
platform.addUser(user);

// Post erstellen
Post post = new Post("username", "Inhalt", 10.0);
platform.addPost(post);

// Daten exportieren (nur Pfad erforderlich)
platform.export("/pfad/zur/datei.dat");

// Daten laden/hochladen (nur Pfad erforderlich)
SocialMediaPlatform loadedPlatform = SocialMediaPlatform.upload("/pfad/zur/datei.dat");
```

### Klassen

- **User**: Repräsentiert einen Benutzer mit Benutzername, E-Mail und Guthaben
- **Post**: Repräsentiert einen Post mit Autor, Inhalt, Preis und Zeitstempel
- **SocialMediaPlatform**: Hauptklasse mit Export- und Upload-Funktionalität