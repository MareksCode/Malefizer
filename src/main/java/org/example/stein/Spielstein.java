package org.example.stein;

import org.example.Feld;
import org.example.Runde;

import java.io.Serializable;

public class Spielstein extends Stein implements Serializable {
    private int spielerId;
    private Feld feld;
    public Spielstein(int id, Runde dazugehoerendeRunde, int spielerId) {
        super(id, dazugehoerendeRunde);
        this.spielerId = spielerId;
    }


    public boolean kannDrueber() {
        return true; //figuren können übersprungen werden
    }
    public int getSpielerId() {
        return spielerId;
    }
    public Feld getCurrentFeld() {
        return this.feld;
    }

    public void setFeld(Feld neuesFeld) throws Exception {
        if (this.feld != null) {
            this.feld.removeBesetzung();
        }

        this.feld = neuesFeld;
        this.feld.schlagen();

        neuesFeld.setBesetzung(this);
    }

    public void schlagen() throws Exception {
        if (this.feld != null) {
            this.feld.removeBesetzung();
            this.feld = null;
        }
    }

    @Override
    public String toString() {
        return "Spielstein:"+id;
    }
}
