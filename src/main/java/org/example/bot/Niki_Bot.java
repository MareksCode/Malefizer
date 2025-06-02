package org.example.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.example.*;
import org.example.stein.Spielstein;
import org.example.Runde;

public abstract class Niki_Bot extends Bot{

    public Feld startFeld;
    public static int pointer=0;

    public Niki_Bot(Feld spawnFeld, int botId, Runde dazugehoerendeRunde) {
        super(spawnFeld, botId, dazugehoerendeRunde);
    }


    private int botZug() throws IOException {       //statt spielerZug

        int[] muster = {1, 1, 4, 3, 1, 2, 5, 2, 5, 2};

            int chosenNumber = muster[pointer];
            pointer = (pointer + 1) % muster.length;

        chosenNumber -= 1; //von leslicher menschlicher 1 zur gigachad array 0 😎

        return chosenNumber;
    }


    private Feld nikiBotZiehe(ArrayList<Feld> moeglicheFelder, Spielstein figur) throws IOException {       //statt spielerZiehe

        int chosenID=0;

        int anzahl = moeglicheFelder.size();

        for (Feld feld : moeglicheFelder) {     //niki wählt immer die größte feldzahl aus
            int temp = feld.getId();

            if(temp > chosenID){
                chosenID=temp;
            }

        }


        System.out.println("Bot has chosen: "+chosenID);
        Feld chosenFeld = null;

        for (Feld feld : moeglicheFelder) {
            if (feld.getId() == chosenID) {
                chosenFeld = feld;
            }
        }

        if (chosenFeld == null) {
            return nikiBotZiehe(moeglicheFelder, figur);
        }

        System.out.println("Bot has chosen: "+chosenFeld.getId());
        return chosenFeld;
    }

}
