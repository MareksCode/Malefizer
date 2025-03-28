import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Runde {
    private boolean spielGewonnen;
    private int amZug;
    private int spielerAnzahl;
    public Runde(int spieler) {
        this.spielGewonnen = false;
        this.amZug = -1;
        this.spielerAnzahl = spieler;
    }

    private ArrayList<Feld> findeMoegicheFelder(Feld startFeld, int laufLaenge) {
        ArrayList<Feld> ergebnis = new ArrayList<>();
        ArrayList<Feld> angeschauteFelder = new ArrayList<>();
        ArrayList<Feld> queue = new ArrayList<>();
        queue.add(startFeld);

        while (!queue.isEmpty()) {
            Feld currentFeld = queue.removeFirst();
            angeschauteFelder.add(currentFeld);

            int tiefe = currentFeld.getTiefe();

            if (tiefe == laufLaenge) {
                ergebnis.add(currentFeld);
            }

            if (!currentFeld.kannDrueber()) {continue;} //wenn man nicht drüber kann, nicht zu den nachbarn schauen

            ArrayList<Feld> nachbarn = currentFeld.getNachbarn();
            for (Feld nachbar : nachbarn) {
                if (tiefe+1 > laufLaenge) {continue;} //wenn zu tief
                if (nachbar.getGefaerbt()) {continue;} //wenn schon drübergegangen

                nachbar.setGefaerbt(true);
                nachbar.setTiefe(tiefe+1);
            }
        }

        for (Feld angeschautesFeld : angeschauteFelder) { //garbage cleaning
            angeschautesFeld.resetTempVars();
        }

        return ergebnis;
    }

    public void start() throws FileNotFoundException {
        Wuerfel wuerfel = new Wuerfel(); //neuen würfel kreieren

        SpielfeldHeinz heinz = new SpielfeldHeinz(this);

        Feld startFeld = heinz.createSpielfeld();

        SpielerObjekt[] spielerListe; //spielerliste erstellen
        spielerListe = new SpielerObjekt[this.spielerAnzahl];

        for (int spielerNum = 0; spielerNum < this.spielerAnzahl; spielerNum++) { //spielerspawns erstellen
            SpielerObjekt spieler = new SpielerObjekt(new Feld(new ArrayList<Feld>()), spielerNum, this); //todo: connect
            spielerListe[spielerNum] = spieler; //in spawn array hinzufügen
        }

        while (this.spielGewonnen == false) { //spiel loop, bis gewonnen wurde
            this.amZug = (this.amZug + 1) % this.spielerAnzahl;
            SpielerObjekt spieler = spielerListe[this.amZug];

            //player selection (emulated)
            int figurNummer = 0; // spieler klickt figur 0 an
            int wuerfelErgebnis = wuerfel.Roll(); // spieler würfelt

            Spielstein figur = spieler.getFigur(figurNummer);
            ArrayList<Feld> moeglicheFelder = findeMoegicheFelder(figur.getCurrentFeld(), wuerfelErgebnis);

            if (!moeglicheFelder.isEmpty()) {
                Feld chosenFeld = moeglicheFelder.getFirst();
                figur.setFeld(chosenFeld);
            }
        }
    }

    public void end() {
        this.spielGewonnen = true;
    }
}
