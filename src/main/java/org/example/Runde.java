package org.example;

import org.example.client.SocketService;
import org.example.stein.*;

import java.util.ArrayList;

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
                    verarbeiteWurf(spielerObjekt.getId(), wurf);
                })
                .exceptionally(e -> {
                    System.out.println("Fehler beim Würfeln: " + e.getMessage());
                    return null;
                });
    }

    private void verarbeiteWurf(int spielerId, int wurf)  {
        //dieser berecich muss angepasst werden, sobald die gui da ist.
        //es werden inputs benötigt, die nachfolgend simuliert werden.
        //es wird davon ausgegangen, das jeder spieler aktuell mindestens einen zug amchen kann, sonst crashen wir!

        figurInputNummer = 0; //range 0 bis 4
        //testen ob spielstein schon im feld sonst spawn
        Spielstein sp = spielerObjekt.getFigur(figurInputNummer);
        //if(sp.getCurrentFeld() == null) sp.setFeld(spielerObjekt.getSpawnFeld());
        ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(sp.getCurrentFeld() == null ? spielerObjekt.getSpawnFeld() : sp.getCurrentFeld(), wurf);

        //select feld aus den möglichen feldern
        //if (sp.getCurrentFeld() == null) sp.setFeld(spielerObjekt.getSpawnFeld());
        socket.spielerZiehe((sp.getCurrentFeld() == null ? spielerObjekt.getSpawnFeld() : sp.getCurrentFeld()), moeglicheFelder.getFirst().getId());
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






