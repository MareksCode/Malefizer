package org.example.stein;

import org.example.Feld;
import org.example.Runde;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SpielsteinTest {

    @Test
    void getCurrentFeld() {
        Spielstein spielstein=new Spielstein(1,new Runde(4),1);// Erstellen eines neuen Spielstein-Objekts mit ID 1, einer neuen Runde mit 4 Spielern und Spieler-ID 1
        Feld feld=new Feld(new ArrayList<>());
        spielstein.setFeld(feld);// Setzen des Feldes für den Spielstein
        assertEquals(feld,spielstein.getCurrentFeld());
    }

    @Test
    void setFeld() {
        Spielstein spielstein=new Spielstein(1,new Runde(4),1);
        Feld feld1= new Feld(new ArrayList<>());
        Feld feld2= new Feld(new ArrayList<>());

        spielstein.setFeld(feld1);
        assertEquals(feld1,spielstein.getCurrentFeld());// Überprüfen, ob das aktuelle Feld des Spielsteins das erste Feld ist
        spielstein.setFeld(feld2);
        assertEquals(feld2,spielstein.getCurrentFeld());
    }

}