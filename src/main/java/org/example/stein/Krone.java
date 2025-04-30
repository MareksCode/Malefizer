package org.example.stein;

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

    @Override
    public String toString() {
        return "Krone:"+id;
    }
}
