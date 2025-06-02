package org.example.bot;

import org.example.Feld;
import org.example.Runde;
import org.example.stein.Spielstein;
import org.example.bot.Tiefensuche;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.bot.Tiefensuche.findeKuerzestenPfad;

public class Smart_Bot extends Bot{

    public Feld startFeld;
    public static int pointer=0;

    public Smart_Bot(Feld spawnFeld, int spielerId, Runde dazugehoerendeRunde) {
        super(spawnFeld, spielerId, dazugehoerendeRunde);
    }


    private int botZug() throws IOException {       //statt spielerZug

        int[] muster = {1, 1, 4, 3, 1, 2, 5, 2, 5, 2};

        int chosenNumber = muster[pointer];
        pointer = (pointer + 1) % muster.length;

        chosenNumber -= 1; //von leslicher menschlicher 1 zur gigachad array 0 😎

        return chosenNumber;
    }

    private Feld smartBotZiehe(Map<Integer, List<Integer>> graph, int start, int ziel,ArrayList<Feld> moeglicheFelder, Spielstein figur) throws IOException {       //statt spielerZiehe

        int chosenID = 0;

        int anzahl = moeglicheFelder.size();

        findeKuerzestenPfad(graph,start,ziel);      //todo finde möglichen 

        for (Feld feld : moeglicheFelder) {
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
            return smartBotZiehe(graph,start,ziel,moeglicheFelder, figur);
        }

        System.out.println("Bot has chosen: "+chosenFeld.getId());
        return chosenFeld;
    }

}