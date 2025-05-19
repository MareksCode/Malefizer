package org.example.stein;

import org.example.Feld;
import org.example.Runde;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Sperrstein extends Stein {
    private Feld feld;
    public Sperrstein(int id, Runde dazugehoerendeRunde) {

        //this.feld = neuesFeld;
        super(id, dazugehoerendeRunde);
    }

    public void setFeld(Feld neuesFeld) {
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

        Feld newFeld = steinZiehe(findeMoegicheFelder(this.dazugehoerendeRunde.startFeld), this);
        this.setFeld(newFeld);
        System.out.println("setFeld bei sperrstein hat geklappt");
    }
    @Override
    public String toString() {
        return "Sperrstein:"+id;
    }
}
