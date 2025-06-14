package org.example.bot;

import org.example.*;
import org.example.stein.Sperrstein;
import org.example.stein.Spielstein;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static org.example.bot.Tiefensuche.findeKuerzestenPfad;

public class Fight_Bot extends Bot{

    public static int pointer=0;
    ArrayList <Integer> skipsterine = new ArrayList<Integer>();

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

    public int fightBotZug(int figur) throws IOException {       //statt spielerZug

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

    public Feld fightBotZiehe( int start, int ziel, ArrayList<Feld> moeglicheFelder, Spielstein figur, Wuerfel gewuerfelt) throws IOException {       //statt spielerZiehe


        List<Integer> kuerzersterPfad = findeKuerzestenPfad(SpielfeldHeinz.feldMap,start,ziel);

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
            if(temp < chosenID){
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

    public Feld fightSperrsteinZiehe(ArrayList<Feld> moeglicheFelder, Sperrstein figur, Feld feld, SpielerObjekt[] spielerliste) throws IOException {

        scannStein(spielerliste);
        Feld kurzsteinfeld = scannStein(spielerliste);

        int kleisntefeldid = findeFeld(kurzsteinfeld);

        Feld kleisntefeld = feldvaledirung(kleisntefeldid, moeglicheFelder);

        if (kleisntefeld == null) {
            skipsterine.add(kleisntefeldid);
            kleisntefeld = feldvaledirung(scannStein(spielerliste,skipsterine,moeglicheFelder).getId(), moeglicheFelder);
        }
        skipsterine.clear();
        return kleisntefeld;

    }

    private  Feld scannStein(SpielerObjekt[] spielerliste){
        Feld kurzsteinfeld = null;
        int leange = 0;
        for (var spieler : spielerliste) {
            if (spieler.spielerId != this.spielerId) {
                for (var stein : spieler.Figuren) {
                    if(stein.getCurrentFeld() == null){
                        continue;
                    }
                    List<Integer> kuerzersterPfadKrone = findeKuerzestenPfad(SpielfeldHeinz.feldMap, 9, stein.getCurrentFeld().getId());

                    if (kuerzersterPfadKrone.size() < leange || leange == 0) {
                        leange = kuerzersterPfadKrone.size();
                        kurzsteinfeld = stein.getCurrentFeld();
                    }
                }
            }
        }
        return kurzsteinfeld;
    }

    private  Feld scannStein(SpielerObjekt[] spielerliste, ArrayList <Integer> skipStein, ArrayList<Feld> moeglicheFelder){
        Feld kurzsteinfeld = null;
        int leange = 0;
        for (var spieler : spielerliste) {
            if (spieler.spielerId != this.spielerId) {
                for (var stein : spieler.Figuren) {
                    if(stein.getCurrentFeld() == null){
                        continue;
                    }
                    if(skipStein.contains(stein.getCurrentFeld().getId())){
                        continue;
                    }
                    List<Integer> kuerzersterPfadKrone = findeKuerzestenPfad(SpielfeldHeinz.feldMap, 9, stein.getCurrentFeld().getId());

                    if (kuerzersterPfadKrone.size() < leange || leange == 0) {
                        leange = kuerzersterPfadKrone.size();
                        kurzsteinfeld = stein.getCurrentFeld();
                    }
                }
            }
        }

        if (kurzsteinfeld == null) {
            return null;
        }

        int newfeldid = findeFeld(kurzsteinfeld);
        Feld newfeld = feldvaledirung(newfeldid, moeglicheFelder);

        if (newfeld == null && !skipStein.contains(newfeldid)){
            skipStein.add(newfeldid);
            return scannStein(spielerliste,skipStein,moeglicheFelder);
        }
        return newfeld != null ? newfeld : kurzsteinfeld;
    }

    private int findeFeld(Feld kurzsteinfeld){
        int start = kurzsteinfeld.getId();
        var nachbarn = kurzsteinfeld.getNachbarn();
        int kleisntefeldid = 0;
        if (nachbarn != null && !nachbarn.isEmpty()) {
            int tmp2 = 0;
            for (var tmp : nachbarn) {
                if (start < 8) { // 8 weil feld (╯°□°)╯︵ ┻━┻
                    if (tmp.getId() < tmp2) {
                        tmp2 = tmp.getId();
                    }
                } else {
                    if(tmp.getId()!=0) {
                        if (tmp.getId() > tmp2) {
                            tmp2 = tmp.getId();
                        }
                    }
                }
            }
            kleisntefeldid = tmp2;
        }
        return kleisntefeldid;
    }
}