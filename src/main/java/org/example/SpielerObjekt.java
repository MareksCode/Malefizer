package org.example;

import org.example.stein.Spielstein;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class SpielerObjekt implements Serializable {
    private static final long serialVersionUID = 1L;
    private Feld spawnFeld;
    public Spielstein[] Figuren;
    public int spielerId ;
    private Color Color;

    public SpielerObjekt(Feld spawnFeld, int spielerId, Runde dazugehoerendeRunde) {
        this.spawnFeld = spawnFeld;
        this.Figuren = new Spielstein[5];
        this.spielerId = spielerId;

        for (int figurNummer = 0; figurNummer<5; figurNummer++) {
            Spielstein neueFigur = new Spielstein(figurNummer, dazugehoerendeRunde, spielerId);
            this.Figuren[figurNummer] = neueFigur;
        }
    }

    public Color getColor() {
        return this.Color;
    }
    public void setColor(Color color) {
        this.Color = color;
    }

    public Spielstein getFigur(int figurNummer) {
        return this.Figuren[figurNummer];
    }

    public Spielstein[] getSpielsteinListe(){return Figuren;}

    public void setSpielstein(Spielstein sp){
        for(int i = 0; i < 5; i++){
            if(Figuren[i] != null){
                Figuren[i] = sp;
                return;
            }
        }
    }

    public Feld getSpawnFeld() {
        return this.spawnFeld;
    }

    public Feld feldvaledirung(int feldval, ArrayList<Feld> moeglicheFelder){
        Feld chosenFeld = null;
        if(feldval == 87 || feldval == 83 || feldval == 79 ||            //checken ob es ein span feld ist
                feldval == 75 || feldval == 9){
            return null;
        }
        else {
            for (Feld moeglichesFeld : moeglicheFelder) {
                if (moeglichesFeld.getId() == feldval) {
                    if (moeglichesFeld.getBesetzung() == null) {
                        chosenFeld = moeglichesFeld;
                        return chosenFeld;
                    }
                }
            }
        }
        if (chosenFeld == null){
            chosenFeld = feldvaledirung(feldval+1, moeglicheFelder);
        }
        return chosenFeld;
    }
}


