import java.sql.Array;
import java.util.*;

public class Feld {
    //private int faerbung;
    private ArrayList<Feld> nachbarn;
    private Stein besetzung;
    private int id;
    private boolean spielerSpawn;

    public Feld(ArrayList<Feld> nachbarn) {
        //this.faerbung = faerbung;
        this.nachbarn = nachbarn;
        this.spielerSpawn = false;
        //this.besetzung = besetzung;
    }

    public void setSpielerSpawn(boolean spielerSpawn) {
        this.spielerSpawn = spielerSpawn;
    }
    public boolean istSpielerSpawn() {
        return spielerSpawn;
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
