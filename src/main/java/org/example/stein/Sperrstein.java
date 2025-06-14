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

    private Feld steinZiehe(ArrayList<Feld> moeglicheFelder, Sperrstein figur) throws IOException {
        System.out.println("Please choose what Spielfeld you want to bewegen zhe Sperrstein on (using the ID)");
        BufferedReader r = new BufferedReader(
                new InputStreamReader(System.in));

        String s = r.readLine();
        int chosenID = Integer.parseInt(s);
        Feld chosenFeld = null;

        for (Feld feld : moeglicheFelder) {
            if (feld.getId() == chosenID) {

                if(chosenID == 87 || chosenID == 83 || chosenID == 79 ||            //checken ob es ein span feld ist
                        chosenID == 75 || chosenID == 9){
                    System.out.println("invalid choose, the Sperrstein can`t be on the first line");
                    return steinZiehe(moeglicheFelder, figur);
                }
                else {
                    for (Feld moeglichesFeld : moeglicheFelder) {
                        if (moeglichesFeld.getId() == chosenID) {
                            if (moeglichesFeld.getBesetzung() == null) {
                                chosenFeld = moeglichesFeld;
                                return chosenFeld;
                            }
                        }
                    }
                }
                    chosenFeld = feld;
            }
        }

        if (chosenFeld == null) {
            return steinZiehe(moeglicheFelder, figur);
        }

        return chosenFeld;
    }

    public void schlagen() throws Exception {

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
