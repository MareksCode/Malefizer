package org.example.bot;

import org.example.Feld;
import org.example.Runde;
import org.example.Wuerfel;
import org.example.stein.Sperrstein;
import org.example.stein.Spielstein;
import org.example.SpielfeldHeinz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.example.bot.Tiefensuche.findeKuerzestenPfad;

public class Smart_Bot extends Bot {

    public static int pointer = 0;

    public Smart_Bot(Feld spawnFeld, int spielerId, Runde dazugehoerendeRunde) {
        super(spawnFeld, spielerId, dazugehoerendeRunde);
    }


    public int smartBotZug() throws IOException {       //statt spielerZug

        int[] muster = {1};

        int chosenNumber = muster[pointer];
        pointer = (pointer + 1) % muster.length;

        chosenNumber -= 1; //von leslicher menschlicher 1 zur gigachad array 0 😎

        return chosenNumber;
    }

    public int smartBotZug(int figur) throws IOException {//statt spielerZug überladen

        int[] muster = {1, 2, 3, 4, 5};
        int chosenNumber = 0;
        for (int i = figur; i < muster.length; i++) {

            if (i != figur) {
                chosenNumber = muster[i];
                break;
            }
        }
        chosenNumber -= 1; //von leslicher menschlicher 1 zur gigachad array 0 😎
        return chosenNumber;
    }


    public Feld smartBotZiehe(int start, int ziel, ArrayList<Feld> moeglicheFelder, Spielstein figur, Wuerfel gewuerfelt) throws IOException {       //statt spielerZiehe


        List<Integer> kuerzersterPfad = findeKuerzestenPfad(SpielfeldHeinz.feldMap, start, ziel);

        if (kuerzersterPfad == null || kuerzersterPfad.isEmpty()) {
            return fallbackFeldWahl(moeglicheFelder);
        }

        for (Feld moeglichesFeld : moeglicheFelder) {
            for (Integer pfadPunkt : kuerzersterPfad) {
                if (moeglichesFeld.getId() == pfadPunkt) {

                    return moeglichesFeld;// Direkt auf dem Pfad beste Option
                }
            }
        }

        if (!moeglicheFelder.isEmpty()) {

            return moeglicheFelder.get(0);
        }

        return fallbackFeldWahl(moeglicheFelder);//wenn nichts möglich alte logic
    }

    private Feld fallbackFeldWahl(ArrayList<Feld> moeglicheFelder) {
        int chosenID = 0;
        for (Feld feld : moeglicheFelder) {
            int temp = feld.getId();
            if (temp > chosenID) {
                chosenID = temp;
            }
        }
        for (Feld feld : moeglicheFelder) {
            if (feld.getId() == chosenID) {
                return feld;
            }
        }
        System.out.println("Bot has chosen: " + chosenID);

        return moeglicheFelder.isEmpty() ? null : moeglicheFelder.get(0);
    }

    public Feld smartSperrsteinZiehe(ArrayList<Feld> moeglicheFelder, Sperrstein figur, Feld feld2) throws IOException {

        int start = feld2.getId();
        var nachbarn = feld2.getNachbarn();
        int groestesfeldid = 0;
        if (nachbarn != null && !nachbarn.isEmpty()){
            int tmp2 = 0;
            for (var tmp: nachbarn){
                if (start < 8){ // 8 weil feld (╯°□°)╯︵ ┻━┻
                    if (tmp.getId() < tmp2){
                        tmp2 = tmp.getId();
                    }
                }
                else {
                    if (tmp.getId() > tmp2){
                        tmp2 = tmp.getId();
                    }
                }
            }
            groestesfeldid = tmp2;
        }

        Feld groestesfeld =  feldvaledirung(groestesfeldid,moeglicheFelder);

        Feld chosenFeld = null;

        List<Integer> kuerzersterPfadKrone = findeKuerzestenPfad(SpielfeldHeinz.feldMap, start, 8);

        if (kuerzersterPfadKrone == null || kuerzersterPfadKrone.isEmpty()) {
            return fallbackFeldWahl(moeglicheFelder);
        }

        int nextFieldIdRichtungKrone = kuerzersterPfadKrone.get(1);

        if(groestesfeldid==nextFieldIdRichtungKrone || groestesfeld == null){

            List<Integer> kuerzersterPfad = findeKuerzestenPfad(SpielfeldHeinz.feldMap, start, 81);


            if (kuerzersterPfad == null || kuerzersterPfad.isEmpty()) {
                return fallbackFeldWahl(moeglicheFelder);
            }

            int nextFieldId = kuerzersterPfad.get(1);

            for (Feld moeglichesFeld : moeglicheFelder) {
                if (moeglichesFeld.getId() == nextFieldId) {
                    chosenFeld = feldvaledirung(nextFieldId,moeglicheFelder);

                    if (chosenFeld == null){
                        chosenFeld = randomNeuesFeld(moeglicheFelder);
                    }
                }
            }
        }else{
            chosenFeld = groestesfeld;

        }

        return chosenFeld;
    }
}

