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

        return ergebnis;
    }

    public void start() {
        Wuerfel wuerfel = new Wuerfel(); //neuen würfel kreieren

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
            ArrayList<Feld> moeglicheFelder = findeMoegicheFelder();

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
