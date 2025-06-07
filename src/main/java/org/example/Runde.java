package org.example;

import org.example.bot.Bot;
import org.example.bot.Fight_Bot;
import org.example.bot.Niki_Bot;
import org.example.bot.Smart_Bot;
import org.example.stein.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.example.SpielerObjekt;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static org.example.bot.Tiefensuche.findeKuerzestenPfad;


public class Runde implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean spielGewonnen;
    Map<Integer, Feld> spawns;
    private boolean isStartAllowed = true;
    public Feld startFeld; //ToDo: pfusch ändern
    private int amZug;
    TerminalAusgabe gui = null;
    private final int MAX_SPIELER=4;
    private int spielerAnzahl;
    private int botAnzahl;
    private int botSchwierigkeit;
    SpielerObjekt[] spielerListe;

    public Runde(int spieler, int botSchwierigkeit) {
        this.spielGewonnen = false;
        this.amZug = -1;
        this.spielerAnzahl = spieler;
        this.botAnzahl = MAX_SPIELER-spieler;
        this.botSchwierigkeit=botSchwierigkeit;
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
            for (int i = 0; i < spielerAnzahl; i++) {

                SpielerObjekt sp = new SpielerObjekt(spawns.get(i), i, this,false);
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
            for ( int i = spielerAnzahl; i < MAX_SPIELER+1; i++){//todo bitte überprüfen ob logic sinnvoll vieleicht mit menü koillierent

                SpielerObjekt spBot = new SpielerObjekt(spawns.get(i), i, this,true);
                spielerListe[i] = spBot;
                Element e = (Element) spielers.item(i);
                NodeList childs = e.getElementsByTagName("spielstein");
                for(int j = 0; j < childs.getLength(); j++){
                    Element spielstein = (Element) childs.item(j);
                    int id = Integer.parseInt(spielstein.getAttribute("feldId"));
                    if (id == -1){
                        spBot.setSpielstein(new Spielstein(j,this, i));
                    } else {spBot.setSpielstein((Spielstein) feldMap.get(Integer.toString(id)).getBesetzung());}
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
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

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
        spielerListe = new SpielerObjekt[MAX_SPIELER];


            for (int spielerNum = 0; spielerNum < this.spielerAnzahl; spielerNum++) { //spielerspawns erstellen
                //vielleicht in eine zeile packen
                SpielerObjekt spieler = new SpielerObjekt(spawns.get(spielerNum), spielerNum, this, false);
                spielerListe[spielerNum] = spieler; //in spawn array hinzufügen
            }

        if(this.spielerAnzahl!=MAX_SPIELER) {
            switch (botSchwierigkeit) {

                case 1:
                    for (int spielerNum = this.spielerAnzahl; spielerNum < MAX_SPIELER; spielerNum++) { //spielerspawns für Bots erstellen

                        spielerListe[spielerNum] = new Niki_Bot(spawns.get(spielerNum), spielerNum, this);
                    }
                    break;

                case 2:
                    for (int spielerNum = this.spielerAnzahl; spielerNum < MAX_SPIELER; spielerNum++) { //spielerspawns für Bots erstellen

                        spielerListe[spielerNum] = new Smart_Bot(spawns.get(spielerNum), spielerNum, this);
                    }
                    break;

                case 3:
                    for (int spielerNum = this.spielerAnzahl; spielerNum < MAX_SPIELER; spielerNum++) { //spielerspawns für Bots erstellen

                        spielerListe[spielerNum] = new Fight_Bot(spawns.get(spielerNum), spielerNum, this);
                    }
                    break;
            }
        }

        spielloop();
    }

    public void spielloop() throws Exception {
        Wuerfel wuerfel = new Wuerfel();
        while (!this.spielGewonnen) { //spiel loop, bis gewonnen wurde
            System.out.println("\n\n\n\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n\n\n");
            gui.update(startFeld);

            //System.out.println(spielerAnzahl);

            this.amZug = (this.amZug + 1) % this.MAX_SPIELER;
            //this.amZug = (this.amZug + 1) % this.spielerAnzahl;
            SpielerObjekt spieler = spielerListe[this.amZug];       //würft fehler für bots
                                                                    //figuren ID bezeichnung ist jetzt player.Anzahlfiguren in der ausgabe
            System.out.println("Spieler " + (this.amZug + 1) + " ist am Zug");

            //spieler tätigt sinnvolle eingaben um das spiel meisterhaft zu gewinnen!!
            //ooooooder der ultimate bot zelegt ihn


            int figurNummer=0;
            int wuerfelErgebnis=0;
            if(spieler instanceof Bot) {
                if(spieler instanceof Niki_Bot) {
                    wuerfelErgebnis = wuerfel.Roll();
                    figurNummer = ((Niki_Bot)spieler).botZug();
                } else if(spieler instanceof Smart_Bot) {
                    wuerfelErgebnis = wuerfel.Roll();
                    figurNummer = ((Smart_Bot)spieler).smartBotZug();
                } else if(spieler instanceof Fight_Bot) {
                    wuerfelErgebnis = wuerfel.Roll();
                    figurNummer = ((Fight_Bot)spieler).fightBotZug();
                }
            } else {

                wuerfelErgebnis = spielerWuerfel(wuerfel);
                System.out.println("you würfeled: " + wuerfelErgebnis);
                figurNummer = spielerZug();

            }


            Spielstein figur = spieler.getFigur(figurNummer);
            Feld currentFeld = figur.getCurrentFeld();
            if (currentFeld == null) {
                currentFeld = spieler.getSpawnFeld();
            }

            ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(currentFeld, wuerfelErgebnis);
            //

            if (!moeglicheFelder.isEmpty()) {
                Feld chosenFeld;

                if(spieler instanceof Bot) {
                    if(spieler instanceof Niki_Bot) {
                        chosenFeld = ((Niki_Bot)spieler).nikiBotZiehe(moeglicheFelder, figur);
                    } else if(spieler instanceof Smart_Bot) {
                        chosenFeld = ((Smart_Bot)spieler).smartBotZiehe(currentFeld.getId(), Krone.getKronenId(), moeglicheFelder, figur, wuerfel);
                    } else if(spieler instanceof Fight_Bot) {
                        chosenFeld = ((Fight_Bot)spieler).fightBotZiehe(currentFeld.getId(), Krone.getKronenId(), moeglicheFelder, figur, wuerfel);
                    } else {
                        chosenFeld = spielerZiehe(moeglicheFelder, figur);
                    }
                } else {
                    chosenFeld = spielerZiehe(moeglicheFelder, figur);
                }

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
