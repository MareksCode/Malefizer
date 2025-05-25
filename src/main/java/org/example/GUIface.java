package org.example;

import java.io.Serializable;

public interface GUIface extends Serializable {
    static final long serialVersionUID = 1L;
    void update(Feld startFeld);
    
    void showMessage(String message);
}