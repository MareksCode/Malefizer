package org.example;

import java.io.*;

public class SERWorker {
    public static Runde readSER (String filename)throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Runde runde = (Runde) ois.readObject();
            runde.gui = new FeldGUI(runde.startFeld); // Create new GUI instance
            return runde;
        }
    }

    public static void speichern (Runde runde, GUIface gui, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            runde.gui = gui;
            oos.writeObject(runde);
            System.out.println("SER-Datei gespeichert unter: " + filename);
            if (runde.gui != null) {
                runde.gui.showMessage("Spiel erfolgreich gespeichert unter: " + filename);
            }
        }
    }
}
