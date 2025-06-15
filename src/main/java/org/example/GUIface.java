package org.example;

import java.io.Serializable;

public interface GUIface extends Serializable {
    static final long serialVersionUID = 1L;
    void update(Feld startFeld);
    Feld selectFeld() throws InterruptedException;
    void setObjective(String objective);
    void setCurrentlyAmZug(int amZug);
    void showMessage(String message);
    void zeigeWurfDialog(int Wuerfelergebnis);
}