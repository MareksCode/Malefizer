package org.example;

import org.example.stein.Spielstein;

import java.io.Serializable;

public class SpielerObjekt implements Serializable {
    private static final long serialVersionUID = 1L;
    private Feld spawnFeld;
    private Spielstein[] Figuren;

    public SpielerObjekt(Feld spawnFeld, int spielerId, Runde dazugehoerendeRunde) {
        this.spawnFeld = spawnFeld;
        this.Figuren = new Spielstein[5];

        for (int figurNummer = 0; figurNummer<5; figurNummer++) {
            Spielstein neueFigur = new Spielstein(figurNummer, dazugehoerendeRunde, spielerId);
            this.Figuren[figurNummer] = neueFigur;
        }
    }

    public Spielstein getFigur(int figurNummer) {
        return this.Figuren[figurNummer];
    }

    public Feld getSpawnFeld() {
        return this.spawnFeld;
    }
}
