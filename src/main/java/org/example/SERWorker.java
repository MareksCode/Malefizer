package org.example;

import java.io.*;

public class SERWorker {
    public static Runde readSER (String filename)throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Runde) ois.readObject();
        }
    }

    public static void speichern (Runde runde, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(runde);
            System.out.println("SER-Datei gespeichert unter: " + filename);

        }
    }
}
