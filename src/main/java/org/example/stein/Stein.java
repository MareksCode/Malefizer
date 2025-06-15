package org.example.stein;
import org.example.Feld;
import org.example.Runde;

import java.io.Serializable;

public abstract class Stein implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int id;
    protected Runde dazugehoerendeRunde;
    public Stein(int id, Runde dazugehoerendeRunde) {
        this.id = id;
        this.dazugehoerendeRunde = dazugehoerendeRunde;
    }
    public abstract boolean kannDrueber();
    public abstract void setFeld(Feld neuesFeld);
    public abstract void schlagen() throws Exception;
    public abstract String getType();
}