package org.example;

import java.io.IOException;

public class Main  {
    public static void main(String[] args) throws Exception {
       //org.example.Runde runde = new org.example.Runde(4);
       //runde.start();

        Runde runde = new Runde("test.xml");
        runde.spielloop();

        //Runde geladeneRunde = SERWorker.readSER("Test.ser");
        //geladeneRunde.spielloop();

            // Bestätigen, dass es geklappt hat:
            //System.out.println("Spielstand geladen!");1
            //geladeneRunde.start();

    }

}
