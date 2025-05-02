package org.example;

import org.example.stein.*;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Runde {
    private boolean spielGewonnen;

    public Feld startFeld; //ToDo: pfusch ändern
    private int amZug;
    TerminalAusgabe gui = null;
    FeldGUI testgui = null;
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
                queue.add(nachbar);
                nachbar.setGefaerbt(true);
                nachbar.setTiefe(tiefe+1);
            }

        }
        System.out.print("Mögliche Felder: ");
        for (Feld feld : ergebnis) {
            System.out.print(feld.getId() + " ");
        }
        System.out.println();

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
            if(chosenNumber<1 || chosenNumber>5) { //kein Spielabbruch bei falscher Zahleingabe
                return spielerZug();
            }
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

    private Feld spielerZiehe(ArrayList<Feld> moeglicheFelder, Spielstein figur) throws IOException {
        System.out.println("Please choose what Spielfeld you want to bewegen on (using the ID)");
        BufferedReader r = new BufferedReader(
                new InputStreamReader(System.in));

        String s = r.readLine();

        int chosenID;

        try {
            chosenID = Integer.parseInt(s);

            if(chosenID<0 || chosenID>111) { //kein Spielabbruch bei falsche ID
                return spielerZiehe(moeglicheFelder, figur);
            }
        }
        catch (NumberFormatException e) {
            return spielerZiehe(moeglicheFelder, figur); //wenn eingabe falsch ist, neu prompten
        }

        System.out.println("You have chosen: "+chosenID);
        Feld chosenFeld = null;

        for (Feld feld : moeglicheFelder) {
            if (feld.getId() == chosenID) {
                chosenFeld = feld;
            }
        }

        if (chosenFeld == null) {
            return spielerZiehe(moeglicheFelder, figur);
        }

        System.out.println("You have chosen: "+chosenFeld.getId());
        return chosenFeld;
    }

    public void start() throws Exception {
        Wuerfel wuerfel = new Wuerfel(); //neuen würfel kreieren

        SpielfeldHeinz heinz = SpielfeldHeinz.getInstance(this);

        Feld startFeld = SpielfeldHeinz.getStartfeld();
        this.startFeld = startFeld;

        //todo: remove graphics used for testing and is not terminal

        //testgui = new FeldGUI(startFeld);
        gui = new TerminalAusgabe();

        SpielerObjekt[] spielerListe; //spielerliste erstellen
        spielerListe = new SpielerObjekt[this.spielerAnzahl];

        Map<Integer, Feld> spawns = SpielfeldHeinz.getSpawnFelder();

        for (int spielerNum = 0; spielerNum < this.spielerAnzahl; spielerNum++) { //spielerspawns erstellen

            SpielerObjekt spieler = new SpielerObjekt(spawns.get(spielerNum), spielerNum, this); //todo: connect : new Feld & spawnfeldliste replacen
            spielerListe[spielerNum] = spieler; //in spawn array hinzufügen
        }

        while (this.spielGewonnen == false) { //spiel loop, bis gewonnen wurde
            System.out.println("\n\n\n\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n\n\n");
            gui.update(startFeld);

            this.amZug = (this.amZug + 1) % this.spielerAnzahl;
            SpielerObjekt spieler = spielerListe[this.amZug];
            System.out.println("Spieler " + this.amZug + " ist am Zug");

            //spieler tätigt sinnvolle eingaben um das spiel meisterhaft zu gewinnen!!
            int figurNummer = spielerZug();
            int wuerfelErgebnis = spielerWuerfel(wuerfel);

            System.out.println("you würfeled: " + wuerfelErgebnis);

            Spielstein figur = spieler.getFigur(figurNummer);
            Feld currentFeld = figur.getCurrentFeld();
            if (currentFeld == null) {
                currentFeld = spieler.getSpawnFeld();
            }
            ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(currentFeld, wuerfelErgebnis);

            if (!moeglicheFelder.isEmpty()) {
                Feld chosenFeld = spielerZiehe(moeglicheFelder, figur);
                // 1) remove figur
                currentFeld.removeBesetzung();

                // figur schlage
                figur.setFeld(chosenFeld);

                //testgui.update(startFeld);
                gui.update(chosenFeld);
            } else {
                System.out.println("you can't move with this figure.");
            }
        }
        System.out.println("spiel gewonnen von spieler " + this.amZug);
    }

    public void end() {
        this.spielGewonnen = true;
    }
}
