public abstract class Stein {
    protected int id;
    protected Runde dazugehoerendeRunde;
    public Stein(int id, Runde dazugehoerendeRunde) {
        this.id = id;
        this.dazugehoerendeRunde = dazugehoerendeRunde;
    }
    abstract boolean kannDrueber();
    abstract void schlagen();
}