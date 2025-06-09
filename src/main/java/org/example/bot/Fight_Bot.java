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

        int[] muster = {1};

        int chosenNumber = muster[pointer];
        pointer = (pointer + 1) % muster.length;

        chosenNumber -= 1; //von leslicher menschlicher 1 zur gigachad array 0 😎

        return chosenNumber;
    }

    public Feld fightBotZiehe( int start, int ziel, ArrayList<Feld> moeglicheFelder, Spielstein figur, Wuerfel gewuerfelt) throws IOException {       //statt spielerZiehe


        List<Integer> kuerzersterPfad = findeKuerzestenPfad(SpielfeldHeinz.feldMap,start,ziel);      //todo finde möglichen

        if (kuerzersterPfad == null || kuerzersterPfad.isEmpty()) {
            return fallbackFeldWahl(moeglicheFelder);
        }

        for (Feld moeglichesFeld : moeglicheFelder) {
            for (Integer pfadPunkt : kuerzersterPfad) {
                if (moeglichesFeld.getId() == pfadPunkt) {
                    // Direkt auf dem Pfad - beste Option
                    return moeglichesFeld;
                }
            }
        }
        // Wenn kein direkter Pfadpunkt gefunden, nimm das erste mögliche Feld
        if (!moeglicheFelder.isEmpty()) {
            return moeglicheFelder.get(0);
        }
        // Letzte Fallback-Option
        return fallbackFeldWahl(moeglicheFelder);
    }
    private Feld fallbackFeldWahl(ArrayList<Feld> moeglicheFelder) {
        int chosenID = 0;
        for (Feld feld : moeglicheFelder) {
            int temp = feld.getId();
            if(temp > chosenID){
                chosenID = temp;
            }
        }
        for (Feld feld : moeglicheFelder) {
            if (feld.getId() == chosenID) {
                return feld;
            }
        }

        return moeglicheFelder.isEmpty() ? null : moeglicheFelder.get(0);
    }

}