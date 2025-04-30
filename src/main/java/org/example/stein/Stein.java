package org.example.stein;
import org.example.Runde;

public abstract class Stein {
    protected int id;
    protected Runde dazugehoerendeRunde;
    public Stein(int id, Runde dazugehoerendeRunde) {
        this.id = id;
        this.dazugehoerendeRunde = dazugehoerendeRunde;
    }
    public abstract boolean kannDrueber();
    public abstract void schlagen();
}