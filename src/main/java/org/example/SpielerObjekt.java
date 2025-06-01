package org.example;

import org.example.stein.Spielstein;

public class SpielerObjekt {
    private Feld spawnFeld;
    private Spielstein[] Figuren;
    private int id;

    public SpielerObjekt(Feld spawnFeld, int spielerId, Runde dazugehoerendeRunde) {
        this.spawnFeld = spawnFeld;
        this.Figuren = new Spielstein[5];
        this.id = spielerId;

        for (int figurNummer = 0; figurNummer<5; figurNummer++) {
            Spielstein neueFigur = new Spielstein(figurNummer, dazugehoerendeRunde, spielerId);
            this.Figuren[figurNummer] = neueFigur;
        }
    }

    public Spielstein getFigur(int figurNummer) {
        return this.Figuren[figurNummer];
    }

    public int getId() {
        return this.id;
    }
    public Feld getSpawnFeld() {
        return this.spawnFeld;
    }
}
