package org.example.stein;

import org.example.Feld;
import org.example.Runde;

public class Krone extends Stein {
    public Krone(int id, Runde dazugehoerendeRunde) {
        super(id, dazugehoerendeRunde);
    }

    public boolean kannDrueber() {
        return false;
    }

    public void schlagen() throws Exception {
        this.dazugehoerendeRunde.end();
    }

    public void setFeld(Feld neuesFeld) {

    }

    @Override
    public String toString() {
        return "Krone:"+id;
    }
}
