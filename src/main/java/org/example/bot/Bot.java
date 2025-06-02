package org.example.bot;

import org.example.Feld;
import org.example.Runde;
import org.example.SpielerObjekt;
import org.example.Wuerfel;
import org.example.stein.Spielstein;


public abstract class Bot extends SpielerObjekt {
    public int botId;
    private Spielstein[] Figuren;

    public Bot(Feld spawnFeld, int spielerId, Runde dazugehoerendeRunde) {
        super(spawnFeld, spielerId, dazugehoerendeRunde, true);
        this.botId = spielerId;
    }

}
