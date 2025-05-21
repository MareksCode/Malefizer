package org.example;

import org.example.client.SocketService;
import org.example.stein.*;

import java.util.ArrayList;


public class Runde{
    private boolean spielGewonnen;
    SpielfeldHeinz heinz = SpielfeldHeinz.getInstance(this);
    public Feld startFeld = SpielfeldHeinz.getStartfeld(); //ToDo: pfusch ändern
    private int amZug;
    private SocketService socket;

    private SpielerObjekt spielerObjekt;
    TerminalAusgabe gui = null;

    
    public Runde(SocketService socket) throws Exception {
        this.spielGewonnen = false;
        this.amZug = -1;
        this.socket = socket;
    }

    public void end(){
        System.out.println("Spiel beendet.");
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

    public void macheZug(){
        //würfeln
        // 1.  Würfel anfordern
        socket.requestRoll(0)
                .thenAcceptAsync(wurf -> {                    // Callback sobald Ergebnis kommt
                    try {
                        verarbeiteWurf(0, wurf);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void verarbeiteWurf(int spielerId, int wurf) throws Exception {
        //dieser berecich muss angepasst werden, sobald die gui da ist.
        //es werden inputs benötigt, die nachfolgend simuliert werden.
        //es wird davon ausgegangen, das jeder spieler aktuell mindestens einen zug amchen kann, sonst crashen wir!

        int simulateInputFigurnummer = 1;
        ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(startFeld, wurf);

        //select feld aus den möglichen feldern
        Spielstein sp = spielerObjekt.getFigur(simulateInputFigurnummer);
        if(sp.getCurrentFeld() == null) sp.setFeld(spielerObjekt.getSpawnFeld());
        socket.spielerZiehe(sp.getCurrentFeld(), moeglicheFelder.getFirst().getId());
    }


    public void bewege(String feldIdFrom, String feldIdTo){
        Feld sourceField = SpielfeldHeinz.feldMap.get(feldIdFrom);
        Feld destinatonField = SpielfeldHeinz.feldMap.get(feldIdFrom);
        sourceField.getBesetzung().setFeld(destinatonField);
    }

    public void setSpieler(String feldid){
        Feld spawn = SpielfeldHeinz.feldMap.get(feldid);
        spielerObjekt = new SpielerObjekt(spawn, 0, this);
    }



    //alte main auskommentiert für referenz
    /*
    public void start() throws Exception {
        SpielfeldHeinz heinz = SpielfeldHeinz.getInstance(this);

        Feld startFeld = SpielfeldHeinz.getStartfeld();
        this.startFeld = startFeld;

        gui = new TerminalAusgabe();


        Map<Integer, Feld> spawns = SpielfeldHeinz.getSpawnFelder();


        while (this.spielGewonnen == false) { //spiel loop, bis gewonnen wurde
            System.out.println("\n\n\n\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n\n\n");
            gui.update(startFeld);

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

        */
    }






