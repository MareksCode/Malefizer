# Dokumentation des Build-tooling Prozesses !
## Ziel

- Projekt wird automatisch über Build-Tool Maven gesteuert 
- Verwaltung von Abhängigkeiten (Bibliotheken) und Plugins durch pom.xml 
- automatische erstellung einer ausführbaren .jar-Datei

------
## Werkzeuge 

- Build Tool Maven 4.0.0
- Programmiersprache Java 24

------

## Vorgehen 

1. Maven Projektstruktur erstellen 
2. Abhängigkeitsverwaltung durch pom.xml 
   - definieren von dependencies (externe Bibliotheken) 
   - Plugins für spezielle Aufgaben wie Testurchführung und Kompilierung 
   - Properties für globale Einstellungen die in Projekt genutzt werden (Version ...) 
3. Build-Schritte durch Maven automatisieren 
   - automatisches kompilieren, testen ...
4. .jar-Datei (executable) erstellen 
5. Build starten durch ausführen der .jar-Datei 
