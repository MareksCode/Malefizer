package org.example;

import org.example.client.SocketService;
import org.example.stein.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Thread.sleep;


public class Runde {
    private boolean spielGewonnen;
    SpielfeldHeinz heinz;
    public Feld startFeld; //ToDo: pfusch ändern
    private int amZug;
    private SocketService socket;

    private SpielerObjekt spielerObjekt;
    GUIface gui = null;
    int figurInputNummer = 1;

    private int tempCountOverflow = 0;


    public Runde(SocketService socket, String xmlStr) {
        this.spielGewonnen = false;
        this.amZug = -1;
        this.socket = socket;
        heinz = SpielfeldHeinz.getInstance(this, xmlStr);
        startFeld = SpielfeldHeinz.getStartfeld();
        gui = new FeldGUI(startFeld);
        gui.update(startFeld);
    }

    private ArrayList<Feld> findeMoegicheFelder(Feld startFeld, int laufLaenge) {
        ArrayList<Feld> ergebnis = new ArrayList<>();
        ArrayList<Feld> angeschauteFelder = new ArrayList<>();
        ArrayList<Feld> queue = new ArrayList<>();
        queue.add(startFeld);
        startFeld.setGefaerbt(true);

        while (!queue.isEmpty()) {
            Feld currentFeld = queue.removeFirst();
            angeschauteFelder.add(currentFeld);

            int tiefe = currentFeld.getTiefe();

            if (tiefe == laufLaenge) {
                ergebnis.add(currentFeld);
            }

            if (!currentFeld.kannDrueber()) {
                continue;
            } //wenn man nicht drüber kann, nicht zu den nachbarn schauen

            ArrayList<Feld> nachbarn = currentFeld.getNachbarn();
            for (Feld nachbar : nachbarn) {
                if (tiefe + 1 > laufLaenge) {
                    continue;
                } //wenn zu tief
                if (nachbar.getGefaerbt()) {
                    continue;
                } //wenn schon drübergegangen
                queue.add(nachbar);
                nachbar.setGefaerbt(true);
                nachbar.setTiefe(tiefe + 1);
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

    public void macheZug() {
        //würfeln
        // 1.  Würfel anfordern
        socket.requestRoll(spielerObjekt.getId())
                .thenAcceptAsync(wurf -> {                    // Callback sobald Ergebnis kommt
                    System.out.println(wurf);
                    try {
                        System.out.println("Ergebnis: " + wurf);
                        if (wurf == -1 && tempCountOverflow < 3) {
                            tempCountOverflow++;
                            sleep(100); //buffer neue anfrage um 100ms
                            macheZug();
                        } else if (tempCountOverflow >= 3 && wurf == -1) {
                            System.out.println("Zug abgelehnt, das sollte nciht mehr passieren das ist explizit nicht gut");
                            gui.update(startFeld);
                            tempCountOverflow = 0;
                            return;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        verarbeiteWurf(spielerObjekt.getId(), wurf);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(e -> {
                    System.out.println("Fehler beim Würfeln: " + e.getMessage());
                    return null;
                });
    }

    private boolean checkMovesPossible(SpielerObjekt spieler, int wuerfelErgebnis) {
        Feld figurFeld = spieler.getSpawnFeld();
        for(Spielstein spielstein : spieler.getFiguren()) {

            if(spielstein.getCurrentFeld() != null){
                figurFeld = spielstein.getCurrentFeld();
            }

            ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(figurFeld, wuerfelErgebnis);

            if(!moeglicheFelder.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void verarbeiteWurf(int spielerId, int wurf) throws Exception {
        //dieser berecich muss angepasst werden, sobald die gui da ist.
        //es werden inputs benötigt, die nachfolgend simuliert werden.
        //es wird davon ausgegangen, das jeder spieler aktuell mindestens einen zug amchen kann, sonst crashen wir!

        //zug validieren, prüfen ob bewegung möglich ist
        if(!checkMovesPossible(spielerObjekt, wurf)) {
            socket.skipTurn();
            return;
        }
        //hier passiert gui magie oder so, obacht!

        gui.update(startFeld);

        gui.setCurrentlyAmZug(spielerObjekt.getId());

        gui.setObjective("Du bist am Zug!");

        gui.setObjective("Wähle eine deiner Figuren aus. Klicke auf deinen Spawn um eine neue rauszuholen.");

        int figurnummer = 0;

        figurnummer = spielerZug();


        Spielstein figur = spielerObjekt.getFigur(figurnummer);
        Feld currentFeld = figur.getCurrentFeld();

        if (currentFeld == null)
        {
            currentFeld = spielerObjekt.getSpawnFeld();
        }

        ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(currentFeld, wurf);
        while(moeglicheFelder.isEmpty()) {
            gui.showMessage("Diese Figur kann nicht bewegt werden. Wähle eine neue.");

            figurnummer = spielerZug();

            figur = spielerObjekt.getFigur(figurnummer);
            currentFeld = figur.getCurrentFeld();
            if (currentFeld == null) {
                currentFeld = spielerObjekt.getSpawnFeld();
            }

            moeglicheFelder = findeMoegicheFelder(currentFeld, wurf);
        }

        socket.spielerZiehe(currentFeld, spielerZiehe(moeglicheFelder, figur).getId());

//        figurInputNummer = 0; //range 0 bis 4
//        //testen ob spielstein schon im feld sonst spawn
//        Spielstein sp = spielerObjekt.getFigur(figurInputNummer);
//        //if(sp.getCurrentFeld() == null) sp.setFeld(spielerObjekt.getSpawnFeld());
//        ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(sp.getCurrentFeld() == null ? spielerObjekt.getSpawnFeld() : sp.getCurrentFeld(), wurf);
//
//        //select feld aus den möglichen feldern
//        //if (sp.getCurrentFeld() == null) sp.setFeld(spielerObjekt.getSpawnFeld());
//        socket.spielerZiehe((sp.getCurrentFeld() == null ? spielerObjekt.getSpawnFeld() : sp.getCurrentFeld()), moeglicheFelder.getFirst().getId());
    }

    private Feld spielerZiehe(ArrayList<Feld> moeglicheFelder, Spielstein figur) throws IOException {
        Feld chosenFeld;

        try {
            chosenFeld = gui.selectFeld();
        } catch (InterruptedException ie) {
            System.err.println(ie.getMessage());
            gui.showMessage("Bitte versuche es erneut.");
            return spielerZiehe(moeglicheFelder, figur);
        }

        if (chosenFeld == null) {
            gui.showMessage("Bitte versuche es erneut.");
            return spielerZiehe(moeglicheFelder, figur);
        }

        boolean feldIstImArray = false;
        for (Feld feld : moeglicheFelder) {
            if (feld.getId() == chosenFeld.getId()) {
                feldIstImArray = true;
            }
        }

        if (!feldIstImArray) {
            gui.showMessage("Bitte versuche es erneut.");
            return spielerZiehe(moeglicheFelder, figur);
        }

        return chosenFeld;
    }
    private int spielerZug() throws IOException {
        Feld chosenFeld;
        int spielerNummer = spielerObjekt.getId();
        SpielerObjekt spieler = spielerObjekt;

        try {
            chosenFeld = gui.selectFeld();
        } catch (InterruptedException ie) {
            System.err.println(ie.getMessage());
            gui.showMessage("Bitte versuche es erneut.");
            return spielerZug();
        }
        Stein besetzung = chosenFeld.getBesetzung();

        if (besetzung == null) {
            if (chosenFeld.istSpielerSpawn()) {
                if (chosenFeld.getSpielerSpawnInhaberId() == spielerNummer) {
                    //find not used spielstein
                    Spielstein[] spielerSpielsteine = spieler.getFiguren();
                    for (int i = 0; i < spielerSpielsteine.length; i++) {
                        if (spielerSpielsteine[i].getCurrentFeld() == null) {
                            return i;
                        }
                    }
                    gui.showMessage("Bitte versuche es erneut.");
                    return spielerZug(); //spieler hat keinen spielstein den er sich ausm arsch ziehen kann :(
                }
            }
            gui.showMessage("Bitte versuche es erneut.");
            return spielerZug();
        }
        if (Objects.equals(besetzung.getType(), "Spielstein")) {
            Spielstein selectedBesetzung = (Spielstein) besetzung;
            if (selectedBesetzung.getSpielerId() == spielerNummer) {
                return selectedBesetzung.getId();
            }
        }

        gui.showMessage("Bitte versuche es erneut.");
        return spielerZug();
    }

    public void createNewSpielsteinOnFeld(int spielerNummer, int figurNummer, int feldId) {
        Feld feldObjekt = SpielfeldHeinz.feldMap.get(String.valueOf(feldId));
        if(feldObjekt.getBesetzung() != null){
            try {
                feldObjekt.schlagen();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("sp obj nr: " + spielerObjekt.getId() + " create sp nr " + spielerNummer + " fig nr " + figurNummer + " feld nr " + feldId);

        //wenn eigener spielstein, bewege stein aus dem spielerobjekt heraus mit eigener id, sonst übernheme id des servers auf neue figur
        if(spielerObjekt.getId() == spielerNummer) {
            Spielstein sp = spielerObjekt.getFigur(figurInputNummer);
            sp.setFeld(feldObjekt);
            feldObjekt.setBesetzung(sp);

        }
        else feldObjekt.setBesetzung(new Spielstein(figurNummer, this, spielerNummer));

        gui.update(startFeld);
    }

    public void bewege(String feldIdFrom, String feldIdTo) {
        Feld sourceField = SpielfeldHeinz.feldMap.get(feldIdFrom);
        Feld destinatonField = SpielfeldHeinz.feldMap.get(feldIdTo);
        if(sourceField.getBesetzung() != null) {
            sourceField.getBesetzung().setFeld(destinatonField);
            sourceField.setBesetzung(null);
        }
        gui.update(startFeld);
    }

    public void setSpieler(String feldid, String playerId) {
        Feld spawn = SpielfeldHeinz.feldMap.get(feldid);
        int pid = Integer.parseInt(playerId);

        //gui.setPlayerId(pid);
        spielerObjekt = new SpielerObjekt(spawn, pid, this);

        for(Feld feld : SpielfeldHeinz.feldMap.values()){
            if(feld.getBesetzung() != null &&
                    feld.getBesetzung() instanceof Spielstein &&
                    ((Spielstein)feld.getBesetzung()).getSpielerId() == pid){
                int steinId = ((Spielstein)feld.getBesetzung()).getId();
                //feld.removeBesetzung();
                //feld.setBesetzung(spielerObjekt.getFigur(steinId));
                spielerObjekt.getFigur(steinId).setFeld(feld);
            }
        }
    }

    public void end() {
        return;
    }
}






