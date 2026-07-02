# Java_Fort_leistung - Social Media Platform

Eine vollständige Social-Media-Plattform mit Bezahlfunktionalität, entwickelt in Java mit JDBC-Datenbankanbindung.

## Funktionsübersicht

### Benutzer-Funktionen
- **Registrierung**: Registrierung mit E-Mail-Adresse und Passwort (Hash-basierte Speicherung mit SHA-256)
- **Anmeldung**: Sichere Authentifizierung
- **Guthaben-Verwaltung**: Anzeige, Einzahlung und Auszahlung

### Transaktions-Funktionen
- **Einzahlung**: Geld auf das Konto einzahlen
- **Auszahlung**: Geld vom Konto auszahlen
- **Einzelüberweisung**: Geld an andere Benutzer überweisen
- **Massenüberweisung**: CSV-Datei importieren für mehrere Überweisungen
- **Transaktionshistorie**: Alle Geldbewegungen einsehen
- **Export**: Transaktionen als CSV exportieren

### Soziale Funktionen
- **Pinnwand**: Kommentare auf Benutzer-Pinnwänden hinterlassen
- **Direktnachrichten**: Private Nachrichten an andere Benutzer senden
- **Posteingang**: Alle erhaltenen Nachrichten einsehen
- **Benutzersuche**: Nach Benutzern suchen
- **Export**: Pinnwand- und Direktnachrichten als CSV exportieren

## Technische Details

### Verwendete Technologien
- **Java 17**
- **H2 Database** (JDBC)
- **SHA-256** für Passwort-Hashing mit Salt
- **RegEx** für Validierung (E-Mail, CSV, Beträge)
- **CSV** für Datei-Import und -Export

### Datenbankschema
- `users`: Benutzerdaten mit Passwort-Hash und Guthaben
- `transactions`: Alle Finanztransaktionen mit Integritätsprüfungen
- `wall_comments`: Kommentare auf Benutzer-Pinnwänden
- `direct_messages`: Private Nachrichten zwischen Benutzern

### Sicherheitsmerkmale
- Passwörter werden mit SHA-256 und zufälligem Salt gehasht
- Kein Kontoüberziehen möglich (CHECK Constraints)
- Transaktionsintegrität durch Datenbankebene gesichert
- E-Mail-Validierung per RegEx
- Self-Transfer-Prävention

## Installation und Ausführung

### Voraussetzungen
- Java 17 oder höher
- H2 Database JAR (bereits im `lib/` Verzeichnis enthalten)

### Kompilierung
```bash
./build.sh
```

Oder manuell:
```bash
mkdir -p build/classes
javac -cp "lib/*" -d build/classes $(find src/main/java -name "*.java")
cp src/main/resources/* build/classes/
```

### Anwendung starten
```bash
./run.sh
```

Oder manuell:
```bash
java -cp "build/classes:lib/*" com.socialmedia.Application
```

### Tests ausführen
```bash
./test.sh
```

Oder manuell:
```bash
java -cp "build/classes:lib/*" -ea com.socialmedia.TestRunner
```

## Verwendung

### Beispiel-Workflow

1. **Registrierung**
   - Wählen Sie Option 1 im Anmeldungsmenü
   - Geben Sie eine gültige E-Mail-Adresse ein
   - Wählen Sie ein Passwort (mind. 6 Zeichen)

2. **Anmeldung**
   - Wählen Sie Option 2 im Anmeldungsmenü
   - Geben Sie Ihre Anmeldedaten ein

3. **Geld einzahlen**
   - Nach der Anmeldung wählen Sie Option 2
   - Geben Sie den Betrag ein

4. **Überweisung**
   - Wählen Sie Option 4
   - Geben Sie Empfänger-E-Mail, Betrag und Beschreibung ein

5. **Massenüberweisung**
   - Erstellen Sie eine CSV-Datei im Format:
     ```
     Empfänger;Betrag;Beschreibung
     user@example.com;50.00;Zahlung 1
     other@example.com;25.50;Zahlung 2
     ```
   - Wählen Sie Option 5
   - Geben Sie den Pfad zur CSV-Datei ein

### CSV-Formate

#### Massenüberweisung (Import)
```
Empfänger;Betrag;Beschreibung
anna@admin.com;28.44;Essen
bob@test.com;15.00;Kaffee
```

#### Transaktions-Export
```
Transaktionsdatum;Empfänger;Sender;Beschreibung;Betrag;Transaktionstyp
2024-12-03;rainer@zufall.com;anna@admin.com;Essen;182.44;UEBERWEISUNG
2024-12-03;;rainer@zufall.com;;182.44;EINZAHLUNG
2024-12-03;rainer@zufall.com;;;182.44;AUSZAHLUNG
```

#### Nachrichten-Export
```
Zeitpunkt der Nachricht;Sender;Empfänger;Nachricht
2024-12-03 14:33:02;rainer@zufall.com;anna@admin.com;Du bist die Beste!
```

## Projektstruktur

```
src/
├── main/
│   ├── java/com/socialmedia/
│   │   ├── model/          # Datenmodelle (User, Transaction, etc.)
│   │   ├── dao/            # Data Access Objects
│   │   ├── service/        # Business Logic Layer
│   │   ├── util/           # Hilfsfunktionen (Hashing, Validierung, DB)
│   │   ├── ui/             # Konsolen-UI
│   │   └── Application.java # Hauptklasse
│   └── resources/
│       └── schema.sql      # Datenbankschema
└── test/
    └── java/com/socialmedia/
        ├── service/        # Service-Tests
        ├── util/           # Utility-Tests
        └── TestRunner.java # Test-Ausführung
```

## Validierungen

- **E-Mail-Format**: RegEx-basierte Validierung
- **CSV-Format**: RegEx-basierte Zeilenvalidierung
- **Beträge**: Müssen > 0 sein
- **Self-Transfer**: Wird verhindert
- **Kontostand**: Keine negativen Werte erlaubt
- **Benutzerexistenz**: Prüfung vor Überweisungen

## Datenbankintegrität

- **CHECK Constraints**: Kontostand >= 0, korrekte Transaktionstypen
- **FOREIGN KEY Constraints**: Referentielle Integrität
- **Transaction Logic**: ACID-Eigenschaften durch Transaktionen

## Tests

Das Projekt enthält umfassende Tests für:
- Passwort-Hashing und -Verifizierung
- E-Mail und CSV-Validierung
- Benutzerregistrierung und -Anmeldung
- Alle Transaktionstypen
- Massenüberweisungen mit verschiedenen Fehlerfällen
- Nachrichten und Pinnwand-Funktionen
- Kontostand-Konsistenz

## Autor

Entwickelt als Fortgeschrittenen-Projekt für Java-Programmierung