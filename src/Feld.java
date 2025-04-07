import java.sql.Array;
import java.util.*;

public class Feld {
    //private int faerbung;
    private ArrayList<Feld> nachbarn;
    private Stein besetzung;
    private int id;
    private boolean spielerSpawn;
    private Position position;

    private boolean tempGefaerbt;
    private int tiefe;

    public Feld(ArrayList<Feld> nachbarn) {
        //this.faerbung = faerbung;
        this.nachbarn = nachbarn;
        this.spielerSpawn = false;
        this.tempGefaerbt = false;
        this.tiefe = 0;
        this.position = new Position(-1,-1);
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

    public boolean kannDrueber() {
        if (this.besetzung == null) {return true;}

        return this.besetzung.kannDrueber();
    }

    public void schlagen() {
        if (this.besetzung == null) {return;}

        this.besetzung.schlagen();
    }
}
