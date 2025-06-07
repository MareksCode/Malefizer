package org.example.bot;

import org.example.Feld;
import org.example.Runde;
import org.example.SpielfeldHeinz;
import org.example.Wuerfel;
import org.example.stein.Spielstein;
import org.example.bot.Tiefensuche;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.bot.Tiefensuche.findeKuerzestenPfad;

public class Fight_Bot extends Bot{

    public Feld startFeld;
    public static int pointer=0;

    public Fight_Bot(Feld spawnFeld, int spielerId, Runde dazugehoerendeRunde) {
        super(spawnFeld, spielerId, dazugehoerendeRunde);
    }


    public int fightBotZug() throws IOException {       //statt spielerZug

        int[] muster = {1, 1, 4, 3, 1, 2, 5, 2, 5, 2};

        int chosenNumber = muster[pointer];
        pointer = (pointer + 1) % muster.length;

        chosenNumber -= 1; //von leslicher menschlicher 1 zur gigachad array 0 😎

        return chosenNumber;
    }

    public Feld fightBotZiehe(int start, int ziel, ArrayList<Feld> moeglicheFelder, Spielstein figur, Wuerfel gewuerfelt) throws IOException {       //statt spielerZiehe

        int chosenID = 0;

        int anzahl = moeglicheFelder.size();

        List<Integer> aktuellerPfad = findeKuerzestenPfad(SpielfeldHeinz.feldMap,start,ziel);      //todo finde möglichen
        // wenn sperrstein geschlagen immer auf das feld setzen von dem der bot gekommen ist
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
            return fightBotZiehe(start,ziel,moeglicheFelder, figur, gewuerfelt);
        }

        System.out.println("Bot has chosen: "+chosenFeld.getId());
        return chosenFeld;
    }

}