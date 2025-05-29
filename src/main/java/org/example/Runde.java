package org.example;

import org.example.stein.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Runde implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean spielGewonnen;
    Map<Integer, Feld> spawns;
    private boolean isStartAllowed = true;
    public Feld startFeld; //ToDo: pfusch ändern
    private int amZug;
    TerminalAusgabe gui = null;
    private int spielerAnzahl;
    SpielerObjekt[] spielerListe;

    public Runde(int spieler) {
        this.spielGewonnen = false;
        this.amZug = -1;
        this.spielerAnzahl = spieler;

    }

    public Runde (String dateiname){
        isStartAllowed = false;
        try {
            SpielfeldHeinz heinz = SpielfeldHeinz.getInstance(this, dateiname);
            Feld startFeld = SpielfeldHeinz.getStartfeld();
            this.startFeld = startFeld;
            spawns = SpielfeldHeinz.getSpawnFelder();
            Map<String, Feld> feldMap = SpielfeldHeinz.feldMap;

            Document doc = XMLWorker.readXML(dateiname);
            Element root = doc.getDocumentElement();
            Element currentPlayer = (Element) root.getElementsByTagName("amZug").item(0);
            amZug = Integer.parseInt(currentPlayer.getAttribute("zugCount"));
            System.out.println(amZug);

            Element players = (Element) root.getElementsByTagName("players").item(0);
            NodeList spielers =  players.getElementsByTagName("player");
            spielerListe = new SpielerObjekt[spielers.getLength()];
            for (int i = 0; i < spielers.getLength(); i++) {
                spielerAnzahl++;
                SpielerObjekt sp = new SpielerObjekt(spawns.get(i), i, this);
                spielerListe[i] = sp;
                System.out.println(spielerListe.length);
                Element e = (Element) spielers.item(i);
                NodeList childs = e.getElementsByTagName("spielstein");
                for(int j = 0; j < childs.getLength(); j++){
                    Element spielstein = (Element) childs.item(j);
                    int id = Integer.parseInt(spielstein.getAttribute("feldId"));
                    if (id == -1){
                        sp.setSpielstein(new Spielstein(j,this, i));
                    } else {sp.setSpielstein((Spielstein) feldMap.get(Integer.toString(id)).getBesetzung());}
                }
            }
        } catch (Exception e){
            isStartAllowed = true;
            System.out.println(e);
        }
        gui = new TerminalAusgabe();
        this.spielGewonnen = false ;
    }
    public int getAmZug() {
        return amZug;
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
            if (chosenID == 1000) {
                try {
                    SERWorker.speichern(this, "Test.ser");
                    System.out.println("Spielstand gespeichert!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(chosenID == 2000){
                try{
                    XMLWorker.toXML(startFeld,this, "./test.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

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
        //neuen würfel kreieren
        if(!isStartAllowed) return;
        SpielfeldHeinz heinz = SpielfeldHeinz.getInstance(this);
        Feld startFeld = SpielfeldHeinz.getStartfeld();

        this.startFeld = startFeld;

        spawns = SpielfeldHeinz.getSpawnFelder();

        gui = new TerminalAusgabe();

         //spielerliste erstellen
        spielerListe = new SpielerObjekt[this.spielerAnzahl];


        for (int spielerNum = 0; spielerNum < this.spielerAnzahl; spielerNum++) { //spielerspawns erstellen

            SpielerObjekt spieler = new SpielerObjekt(spawns.get(spielerNum), spielerNum, this);
            spielerListe[spielerNum] = spieler; //in spawn array hinzufügen
        }
        spielloop();
    }

    public void spielloop() throws Exception {
        Wuerfel wuerfel = new Wuerfel();
        while (this.spielGewonnen == false) { //spiel loop, bis gewonnen wurde
            System.out.println("\n\n\n\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n\n\n");
            gui.update(startFeld);

            System.out.println(spielerAnzahl);

            this.amZug = (this.amZug + 1) % this.spielerAnzahl;
            SpielerObjekt spieler = spielerListe[this.amZug];
            System.out.println("Spieler " + (this.amZug+1) + " ist am Zug");

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

                // 2) figur schlagen
                figur.setFeld(chosenFeld);

                // 3) ui update
                gui.update(chosenFeld);
            } else {
                System.out.println("you can't move with this figure.");
            }
        }
        System.out.println("spiel gewonnen von spieler " + (this.amZug+1));
    }

    public void end() {
        this.spielGewonnen = true;
    }
}
