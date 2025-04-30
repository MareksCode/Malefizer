package org.example;

import org.example.stein.*;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Runde {
    private boolean spielGewonnen;
    private int amZug;
    FeldGUI gui = null;
    private int spielerAnzahl;
    public Runde(int spieler) {
        this.spielGewonnen = false;
        this.amZug = -1;
        this.spielerAnzahl = spieler;
    }

    private ArrayList<Feld> findeMoegicheFelder(Feld startFeld, int laufLaenge) {
        ArrayList<Feld> ergebnis = new ArrayList<>();
        ArrayList<Feld> angeschauteFelder = new ArrayList<>();
        ArrayList<Feld> queue = new ArrayList<>();
        queue.add(startFeld);

        while (!queue.isEmpty()) {
            Feld currentFeld = queue.removeFirst();
            angeschauteFelder.add(currentFeld);

            int tiefe = currentFeld.getTiefe();

            if (tiefe == laufLaenge) {
                ergebnis.add(currentFeld);
            }

            if (!currentFeld.kannDrueber()) {continue;} //wenn man nicht drüber kann, nicht zu den nachbarn schauen

            ArrayList<Feld> nachbarn = currentFeld.getNachbarn();
            for (Feld nachbar : nachbarn) {
                if (tiefe+1 > laufLaenge) {continue;} //wenn zu tief
                if (nachbar.getGefaerbt()) {continue;} //wenn schon drübergegangen

                nachbar.setGefaerbt(true);
                nachbar.setTiefe(tiefe+1);
            }
        }

        for (Feld angeschautesFeld : angeschauteFelder) { //garbage cleaning
            angeschautesFeld.resetTempVars();
        }

        return ergebnis;
    }

    private int spielerZug() throws IOException {
        System.out.println("Please choose a Spielstein (Type 1-5)");
        BufferedReader r = new BufferedReader(
                new InputStreamReader(System.in));

        String s = r.readLine();

        int chosenNumber;
        try {
            chosenNumber = Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return spielerZug(); //wenn eingabe falsch ist, neu prompten
        }

        chosenNumber -= 1; //von leslicher menschlicher 1 zur gigachad array 0 😎

        return chosenNumber;
    }

    private int spielerWuerfel(Wuerfel wuerfel) throws IOException {
        System.out.println("Press any key to würfel your würfel");
        BufferedReader r = new BufferedReader(
                new InputStreamReader(System.in));

        r.readLine();

        return wuerfel.Roll();
    }

    public void start() throws Exception {
        Wuerfel wuerfel = new Wuerfel(); //neuen würfel kreieren

        SpielfeldHeinz heinz = SpielfeldHeinz.getInstance(this);

        Feld startFeld = SpielfeldHeinz.getStartfeld();

        gui = new FeldGUI(startFeld);

        //todo: spieler auf spawn zuweisen

        //XMLWorker.toXML(startFeld, "./out.xml");

        SpielerObjekt[] spielerListe; //spielerliste erstellen
        spielerListe = new SpielerObjekt[this.spielerAnzahl];

        for (int spielerNum = 0; spielerNum < this.spielerAnzahl; spielerNum++) { //spielerspawns erstellen
            SpielerObjekt spieler = new SpielerObjekt(new Feld(new ArrayList<Feld>()), spielerNum, this); //todo: connect
            spielerListe[spielerNum] = spieler; //in spawn array hinzufügen
        }

        while (this.spielGewonnen == false) { //spiel loop, bis gewonnen wurde
            this.amZug = (this.amZug + 1) % this.spielerAnzahl;
            SpielerObjekt spieler = spielerListe[this.amZug];

            //spieler tätigt sinnvolle eingaben um das spiel meisterhaft zu gewinnen!!
            int figurNummer = spielerZug();
            int wuerfelErgebnis = spielerWuerfel(wuerfel);

            System.out.println("you würfeled: " + wuerfelErgebnis);

            Spielstein figur = spieler.getFigur(figurNummer);
            ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(figur.getCurrentFeld(), wuerfelErgebnis);

            if (!moeglicheFelder.isEmpty()) {
                Feld chosenFeld = moeglicheFelder.getFirst();
                figur.setFeld(chosenFeld);
            }
        }
    }

    public void end() {
        this.spielGewonnen = true;
    }
}
