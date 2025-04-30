package org.example.stein;

import org.example.Runde;

public class Sperrstein extends Stein {
    public Sperrstein(int id, Runde dazugehoerendeRunde) {
        super(id, dazugehoerendeRunde);
    }

    public boolean kannDrueber() {
        return false;
    }

    public void schlagen() {
    }
    @Override
    public String toString() {
        return "Sperrstein:"+id;
    }
}
