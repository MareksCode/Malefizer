package org.example;

import org.example.bot.Bot;
import org.example.bot.Fight_Bot;
import org.example.bot.Niki_Bot;
import org.example.bot.Smart_Bot;
import org.example.stein.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;


public class Runde implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean spielGewonnen;
    Map<Integer, Feld> spawns;
    private boolean isStartAllowed = true;
    public Feld startFeld;
    private int amZug;
    public GUIface gui = null;
    public final int MAX_SPIELER=4;
    private int spielerAnzahl;
    private int botSchwierigkeit;
    public SpielerObjekt[] spielerListe;

    public Runde(int spieler, int botSchwierigkeit) {
        this.spielGewonnen = false;
        this.amZug = -1;
        this.spielerAnzahl = spieler;
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
                SpielerObjekt sp = new SpielerObjekt(spawns.get(i), i, this);
                spawns.get(i).setSpielerSpawnInhaberId(i);
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
            for ( int i = spielerAnzahl; i < MAX_SPIELER+1; i++){

                SpielerObjekt spBot = new SpielerObjekt(spawns.get(i), i, this);
                spawns.get(i).setSpielerSpawnInhaberId(i);
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
        gui = new FeldGUI(startFeld, this);
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

    private int spielerZugAlt() throws IOException {
        System.out.println("Please choose a Spielstein (Type 1-5)");
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        String s = r.readLine();

        int chosenNumber;
        try {
            chosenNumber = Integer.parseInt(s);
            if(chosenNumber<1 || chosenNumber>5) { //kein Spielabbruch bei falscher Zahleingabe
                return spielerZugAlt();
            }
        }
        catch (NumberFormatException e) {
            return spielerZugAlt(); //wenn eingabe falsch ist, neu prompten
        }

        chosenNumber -= 1; //von leslicher menschlicher 1 zur gigachad array 0 😎

        return chosenNumber;
    }
    private int spielerZug(SpielerObjekt spieler) throws IOException {
        Feld chosenFeld;
        int spielerNummer = spieler.spielerId;

        try {
            chosenFeld = gui.selectFeld();
        } catch (InterruptedException ie) {
            System.err.println(ie.getMessage());
            gui.showNotification("Etwas ist schiefgelaufen. Probiere es erneut.", 2000);
            return spielerZug(spieler);
        }
        Stein besetzung = chosenFeld.getBesetzung();

        if (chosenFeld.istSpielerSpawn()) {
            if (chosenFeld.getSpielerSpawnInhaberId() == spielerNummer) {
                //find not used spielstein
                Spielstein[] spielerSpielsteine = spieler.getSpielsteinListe();
                for (int i = 0; i < spielerSpielsteine.length; i++) {
                    if (spielerSpielsteine[i].getCurrentFeld() == null) {
                        return i;
                    }
                }
                gui.showNotification("Es sind keine Spielsteine im Spawn übrig. Wähle einen auf dem Feld.", 2000);
                return spielerZug(spieler); //spieler hat keinen spielstein den er sich ausm arsch ziehen kann :(
            }
        }
        if (chosenFeld.getBesetzung() == null) {
            gui.showNotification("Auf dem Feld steht kein Spielstein, der gewählt werden kann.", 2000);
            return spielerZug(spieler);
        }

        if (Objects.equals(besetzung.getType(), "Spielstein")) {
            Spielstein selectedBesetzung = (Spielstein) besetzung;
            if (selectedBesetzung.getSpielerId() == spielerNummer) {
                return selectedBesetzung.getId();
            }
        }

        gui.showNotification("Dieser Spielstein ist nicht auswählbar. Wähle einen der eigenen.", 2000);
        return spielerZug(spieler);
    }

    private int spielerWuerfel(Wuerfel wuerfel) throws IOException {
        int ergebnis = wuerfel.Roll();

        gui.zeigeWurfDialog(ergebnis);

        return ergebnis;
    }


    private Feld spielerZieheAlt(ArrayList<Feld> moeglicheFelder, Spielstein figur) throws IOException {
        System.out.println("Please choose what Spielfeld you want to bewegen on (using the ID)");
        BufferedReader r = new BufferedReader(
                new InputStreamReader(System.in));

        String s = r.readLine();


        int chosenID;

        try {
            chosenID = Integer.parseInt(s);
            if (chosenID == 1000) {
                try {
                    SERWorker.speichern(this,gui, "Test.ser");
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
    private Feld spielerZiehe(ArrayList<Feld> moeglicheFelder, Spielstein figur) throws IOException {
        Feld chosenFeld;

        try {
            chosenFeld = gui.selectFeld();
        } catch (InterruptedException ie) {
            System.err.println(ie.getMessage());
            gui.showNotification("Etwas ist schiefgelaufen. Probiere es erneut.", 2000);
            return spielerZiehe(moeglicheFelder, figur);
        }

        if (chosenFeld == null) {
            gui.showNotification("Etwas ist schiefgelaufen. Probiere es erneut.", 2000);
            return spielerZiehe(moeglicheFelder, figur);
        }

        boolean feldIstImArray = false;
        for (Feld feld : moeglicheFelder) {
            if (feld.getId() == chosenFeld.getId()) {
                feldIstImArray = true;
            }
        }

        if (!feldIstImArray) {
            gui.showNotification("Dieser Zug ist nicht möglich. Probiere es erneut", 2000);
            return spielerZiehe(moeglicheFelder, figur);
        }

        return chosenFeld;
    }


    public void start() throws Exception {
        //neuen würfel kreieren
        if(!isStartAllowed) return;
        SpielfeldHeinz heinz = SpielfeldHeinz.getInstance(this);
        Feld startFeld = SpielfeldHeinz.getStartfeld();

        this.startFeld = startFeld;

        spawns = SpielfeldHeinz.getSpawnFelder();


        gui = new FeldGUI(startFeld, this);

         //spielerliste erstellen
        spielerListe = new SpielerObjekt[MAX_SPIELER];


            for (int spielerNum = 0; spielerNum < this.spielerAnzahl; spielerNum++) { //spielerspawns erstellen
                //vielleicht in eine zeile packen
                SpielerObjekt spieler = new SpielerObjekt(spawns.get(spielerNum), spielerNum, this);
                spawns.get(spielerNum).setSpielerSpawnInhaberId(spielerNum);
                spielerListe[spielerNum] = spieler; //in spawn array hinzufügen
            }

        if(this.spielerAnzahl!=MAX_SPIELER) {
            switch (botSchwierigkeit) {

                case 1:
                    for (int spielerNum = this.spielerAnzahl; spielerNum < MAX_SPIELER; spielerNum++) { //spielerspawns für Bots erstellen
                        spawns.get(spielerNum).setSpielerSpawnInhaberId(spielerNum);
                        spielerListe[spielerNum] = new Niki_Bot(spawns.get(spielerNum), spielerNum, this);
                    }
                    break;

                case 2:
                    for (int spielerNum = this.spielerAnzahl; spielerNum < MAX_SPIELER; spielerNum++) {
                        spawns.get(spielerNum).setSpielerSpawnInhaberId(spielerNum);
                        spielerListe[spielerNum] = new Smart_Bot(spawns.get(spielerNum), spielerNum, this);
                    }
                    break;

                case 3:
                    for (int spielerNum = this.spielerAnzahl; spielerNum < MAX_SPIELER; spielerNum++) {
                        spawns.get(spielerNum).setSpielerSpawnInhaberId(spielerNum);
                        spielerListe[spielerNum] = new Fight_Bot(spawns.get(spielerNum), spielerNum, this);
                    }
                    break;
            }
        }
        spielloop();
    }

    public SpielerObjekt spieler;

    public void spielloop() throws Exception {
        Wuerfel wuerfel = new Wuerfel();
        while (!this.spielGewonnen) { //spiel loop, bis gewonnen wurde
            System.out.println("\n\n\n\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n\n\n");
            gui.update(startFeld);
            gui.setObjective("");

            System.out.println("Bitte oeffne die Gui um zu spielen.");

            //System.out.println(spielerAnzahl);

            this.amZug = (this.amZug + 1) % this.MAX_SPIELER;
            spieler = spielerListe[this.amZug];

            gui.showNotification("Spieler " + (this.amZug + 1) + " ist nun am Zug", 4000);
            gui.setCurrentlyAmZug(this.amZug);
            System.out.println("Spieler " + (this.amZug + 1) + " ist am Zug");

            //spieler tätigt sinnvolle eingaben um das spiel meisterhaft zu gewinnen!!
            //ooooooder der ultimate bot zelegt ihn

            gui.setObjective("Spieler "+this.amZug+" würfelt.");
            int wuerfelErgebnis = (spieler instanceof Bot) ? wuerfel.Roll() : spielerWuerfel(wuerfel);

            if (!(spieler instanceof Bot))
            {
                //gui.showMessage("Zhe Bot würfeled: "+wuerfelErgebnis);
                System.out.println("you würfeled: " + wuerfelErgebnis);
            }

            gui.setObjective("Wähle eine deiner Figuren aus. Klicke auf deinen Spawn um eine neue rauszuholen.");

             int figurNummer = switch (spieler) {
                    case Niki_Bot nikiBot -> nikiBot.botZug();
                    case Smart_Bot smartBot -> smartBot.smartBotZug();
                    case Fight_Bot fightBot -> fightBot.fightBotZug();
                    default -> spielerZug(spieler);
            };

            Spielstein figur = spieler.getFigur(figurNummer);
            Feld currentFeld = figur.getCurrentFeld();

            if (currentFeld == null)
            {
                currentFeld = spieler.getSpawnFeld();
            }

            ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(currentFeld, wuerfelErgebnis);

            while (moeglicheFelder.isEmpty()) {

                    switch (spieler) {
                        case Niki_Bot nikiBot -> figurNummer = nikiBot.botZug(figurNummer);
                        case Smart_Bot smartBot -> figurNummer = smartBot.smartBotZug(figurNummer);
                        case Fight_Bot fightBot -> figurNummer = fightBot.fightBotZug(figurNummer);
                        default -> {
                            gui.showMessage("Diese Figur kann nicht bewegt werden. Wähle eine neue.");
                            figurNummer = spielerZug(spieler);
                        }
                    }

                    figur = spieler.getFigur(figurNummer);
                    currentFeld = figur.getCurrentFeld();
                    if (currentFeld == null) {
                        currentFeld = spieler.getSpawnFeld();
                    }

                    moeglicheFelder = findeMoegicheFelder(currentFeld, wuerfelErgebnis);
            }

            if (!moeglicheFelder.isEmpty())
            {
                Feld chosenFeld;

                gui.showNotification("Wähle ein Feld, auf das die ausgewählte Figur ziehen soll.", 4000);
                gui.setObjective("Wähle ein Feld, auf das die ausgewählte Figur ziehen soll.");

                chosenFeld = switch (spieler) {
                        case Niki_Bot nikiBot -> nikiBot.nikiBotZiehe(moeglicheFelder, figur);
                        case Smart_Bot smartBot ->
                                smartBot.smartBotZiehe(currentFeld.getId(), Krone.getKronenId(), moeglicheFelder, figur, wuerfel);
                        case Fight_Bot fightBot ->
                                fightBot.fightBotZiehe(currentFeld.getId(), Krone.getKronenId(), moeglicheFelder, figur, wuerfel);
                        default -> spielerZiehe(moeglicheFelder, figur);
                };

                // 1) remove figur
                currentFeld.removeBesetzung();

                // 2) figur schlagen
                figur.setFeld(chosenFeld);

                // 3) ui update
                gui.update(chosenFeld);
            }
        }

        gui.showMessage("spiel gewonnen von spieler " + (this.amZug));
        //System.out.println("spiel gewonnen von spieler " + (this.amZug+1));
    }

    public void end() {
        this.spielGewonnen = true;
    }

    public void saveAsXml(String absolutePath) throws Exception{
        XMLWorker.toXML(startFeld, this, absolutePath);
        System.out.println("Gespeichert!");
    }

    public void saveAsSer(String absolutePath) throws Exception{
        SERWorker.speichern(this, gui, absolutePath);
        System.out.println("Gespeichert! 2");
    }
}
