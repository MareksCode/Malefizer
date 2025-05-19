package org.example.stein;
import org.example.Feld;
import org.example.Runde;

public abstract class Stein {
    protected int id;
    protected Runde dazugehoerendeRunde;
    public Stein(int id, Runde dazugehoerendeRunde) {
        this.id = id;
        this.dazugehoerendeRunde = dazugehoerendeRunde;
    }
    public abstract boolean kannDrueber();
    public abstract void setFeld(Feld neuesFeld);
    public abstract void schlagen() throws Exception;

}