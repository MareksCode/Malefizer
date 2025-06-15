package org.example.stein;

import org.example.Feld;
import org.example.Runde;
import org.example.bot.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;


public class Sperrstein extends Stein implements Serializable {
    private Feld feld;
    public Sperrstein(int id, Runde dazugehoerendeRunde) {

        //this.feld = neuesFeld;
        super(id, dazugehoerendeRunde);
    }

    public void setFeld(Feld neuesFeld) throws Exception {
        if (this.feld != null) {
            this.feld.removeBesetzung();
        }

        this.feld = neuesFeld;

        neuesFeld.setBesetzung(this);
    }
    public boolean kannDrueber() {
        return false;
    }

    private ArrayList<Feld> findeMoegicheFelder(Feld startFeld) {
        ArrayList<Feld> ergebnis = new ArrayList<>();
        ArrayList<Feld> angeschauteFelder = new ArrayList<>();
        ArrayList<Feld> queue = new ArrayList<>();
        queue.add(startFeld);

        while (!queue.isEmpty()) {
            Feld currentFeld = queue.removeFirst();
            angeschauteFelder.add(currentFeld);

            if (currentFeld.getBesetzung() == null) {
                ergebnis.add(currentFeld);
            }

            ArrayList<Feld> nachbarn = currentFeld.getNachbarn();
            for (Feld nachbar : nachbarn) {
                if (nachbar.getGefaerbt()) {continue;} //wenn schon drübergegangen
                queue.add(nachbar);
                nachbar.setGefaerbt(true);
            }
        }

        for (Feld angeschautesFeld : angeschauteFelder) { //garbage cleaning
            angeschautesFeld.resetTempVars();
        }

        return ergebnis;
    }

    private Feld feldvaledirung(ArrayList<Feld> moeglicheFelder, Sperrstein figur,Feld selectfeld ) throws IOException {
        Feld chosenFeld = null;
        for (Feld feld : moeglicheFelder) {
            if (feld.getId() == selectfeld.getId()) {
                if (feld.getBesetzung() == null || !feld.istSpielerSpawn() || selectfeld.getId() != 9) {
                    chosenFeld = feld;
                    return chosenFeld;
                }
                else{
                    System.out.println("invalid choose, the Sperrstein can`t be on the first line");
                    return steinZiehe(moeglicheFelder, figur);
                }
            }
            chosenFeld = feld;
        }

        if (chosenFeld == null) {
            return steinZiehe(moeglicheFelder, figur);
        }

        return chosenFeld;
    }

    private Feld steinZiehe(ArrayList<Feld> moeglicheFelder, Sperrstein figur) throws IOException {
        Feld chosenFeld;

        try {
            chosenFeld = this.dazugehoerendeRunde.gui.selectFeld();
        } catch (InterruptedException ie) {
            System.err.println(ie.getMessage());
            this.dazugehoerendeRunde.gui.showMessage("Bitte versuche es erneut.");
            return steinZiehe(moeglicheFelder, figur);
        }

        if (chosenFeld == null) {
            this.dazugehoerendeRunde.gui.showMessage("Bitte versuche es erneut.");
            return steinZiehe(moeglicheFelder, figur);
        }

        boolean feldIstImArray = false;
        for (Feld feld : moeglicheFelder) {
            if (feld.getId() == chosenFeld.getId()) {
                feldIstImArray = true;
            }
        }

        if (!feldIstImArray) {
            this.dazugehoerendeRunde.gui.showMessage("Bitte versuche es erneut.");
            return steinZiehe(moeglicheFelder, figur);
        }

        chosenFeld = feldvaledirung(moeglicheFelder,figur,chosenFeld);

        return chosenFeld;
    }

    public String getType() {
        return "Sperrstein";
    }

    public void schlagen() throws Exception {
        this.dazugehoerendeRunde.gui.setObjective("Wähle das Feld auf das der Sperrstein verschoben werden soll.");
        this.feld.removeBesetzung();
        Feld newFeld;
        newFeld = switch(dazugehoerendeRunde.spieler){
            case Niki_Bot nikibot -> nikibot.nikiSperrsteinZiehe(findeMoegicheFelder(this.dazugehoerendeRunde.startFeld), this);
            case Smart_Bot smartBot -> smartBot.smartSperrsteinZiehe(findeMoegicheFelder(this.dazugehoerendeRunde.startFeld), this,this.feld);
            case Fight_Bot fightBot -> fightBot.fightSperrsteinZiehe(findeMoegicheFelder(this.dazugehoerendeRunde.startFeld),this, this.feld,dazugehoerendeRunde.spielerListe);
            default -> steinZiehe(findeMoegicheFelder(this.dazugehoerendeRunde.startFeld), this);
        };

        try{
            this.setFeld(newFeld);
        }catch (NullPointerException e){
            System.out.println("Spielstein ist Null");
        }

        System.out.println("setFeld bei sperrstein hat geklappt");
    }
    @Override
    public String toString() {
        return "Sperrstein:"+id;
    }
}
