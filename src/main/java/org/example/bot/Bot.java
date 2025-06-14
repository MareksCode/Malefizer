package org.example.bot;

import org.example.Feld;
import org.example.Runde;
import org.example.SpielerObjekt;

import java.util.ArrayList;
import java.util.Random;


public abstract class Bot extends SpielerObjekt {

    public Bot(Feld spawnFeld, int spielerId, Runde dazugehoerendeRunde) {
        super(spawnFeld, spielerId, dazugehoerendeRunde);
    }


    public Feld randomNeuesFeld(ArrayList<Feld> moeglicheFelder){
        Random rand = new Random();
        Feld chosenFeld = null;
        int chosenID = 0;

        while (chosenFeld == null) {
            chosenID = 1 + rand.nextInt(111);
            chosenFeld = feldvaledirung(chosenID,moeglicheFelder);
        }

        return chosenFeld;
    }

}
