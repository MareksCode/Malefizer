package org.example.stein;

import org.example.Feld;
import org.example.Runde;

public class Spielstein extends Stein {
    private int spielerId;
    private Feld feld;
    public Spielstein(int id, Runde dazugehoerendeRunde, int spielerId) {
        super(id, dazugehoerendeRunde);
        this.spielerId = spielerId;
    }


    public boolean kannDrueber() {
        return true; //figuren können übersprungen werden
    }

    public Feld getCurrentFeld() {
        return this.feld;
    }

    public void setFeld(Feld neuesFeld) {
        if (this.feld != null) {
            this.feld.removeBesetzung();
        }

        this.feld = neuesFeld;
        neuesFeld.setBesetzung(this);

        this.feld.schlagen();
    }

    public void schlagen() {

    }
    @Override
    public String toString() {
        return "stein.Spielstein:"+id;
    }
}
