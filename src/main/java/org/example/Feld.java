package org.example;

import org.example.stein.Stein;

import java.io.Serializable;
import java.util.ArrayList;

public class Feld implements Serializable {
    private static final long serialVersionUID = 1L;
    //private int faerbung;
    private ArrayList<Feld> nachbarn;
    private Stein besetzung;
    private int id = -1;
    private boolean spielerSpawn;
    private Position position;
    private int spawnInhaber;
    private int[] ersteZeile;

    private boolean tempGefaerbt;
    private int tiefe;

    public Feld(ArrayList<Feld> nachbarn) {
        //this.faerbung = faerbung;
        this.nachbarn = nachbarn;
        this.spielerSpawn = false;
        this.tempGefaerbt = false;
        this.tiefe = 0;
        this.position = new Position(-1,-1);
        this.spawnInhaber = 0;
        this.ersteZeile = new int[]{89,88,86,85,84,82,81,80,78,77,76,110,109};
        //this.besetzung = besetzung;
    }

    public void resetTempVars() {
        this.tempGefaerbt = false;
        this.tiefe = 0;
    }

    public Position getPosition() {return this.position;}
    public void setPosition(Position position) {this.position = position;}

    public int getTiefe() {
        return this.tiefe;
    }
    public void setTiefe(int tiefe) {
        this.tiefe = tiefe;
    }

    public boolean getGefaerbt() {
        return this.tempGefaerbt;
    }
    public void setGefaerbt(boolean tempGefaerbt) {
        this.tempGefaerbt = tempGefaerbt;
    }

    public void setSpielerSpawn(boolean spielerSpawn) {
        this.spielerSpawn = spielerSpawn;
    }
    public boolean istSpielerSpawn() {
        return this.spielerSpawn;
    }
    public boolean getErsteZeile() {
        for (int i : this.ersteZeile) {
            if (this.id == i) {
                return true;
            }
        }
        return false;
    }

    public void setSpielerSpawnInhaberId(int spielerid) {
        this.spawnInhaber = spielerid;
    }
    public int getSpielerSpawnInhaberId() {
        return this.spawnInhaber;
    }

    public void setBesetzung(Stein besetzung) {
        this.besetzung = besetzung;
    }
    public void removeBesetzung() {
        this.besetzung = null;
    }
    public Stein getBesetzung() {
        return this.besetzung;
    }

    public void addNachbarn(Feld nachbar) {
        nachbarn.add(nachbar);
    }
    public ArrayList<Feld> getNachbarn() {
        return nachbarn;
    }

    public void setId(int id) {this.id = id;}
    public int getId() {return this.id;}

    public boolean kannDrueber() {
        if (this.besetzung == null) {return true;}

        return this.besetzung.kannDrueber();
    }


    public void schlagen() throws Exception {
        if (this.besetzung == null) {return;}

        this.besetzung.schlagen();
    }

    @Override
    public String toString() {
        return getPosition().toString() + " @id " + id;
    }
}
