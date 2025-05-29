package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.io.IOException;


public class Main  {
    public static void main(String[] args) throws Exception {


        System.out.println("new Game = 1\n" +
                "load Game = 2");


        int auswahl=0;

        try{
            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            auswahl = Integer.parseInt(read.readLine());
        }
        catch (IOException e){
            auswahl = 0;
            return;
        }

        switch (auswahl) {
            case 1:
                // spawns null spiel kann nicht gestartet werden
                org.example.Runde runde = new org.example.Runde(4);
                runde.start();
                break;

            case 2:
                Runde rundeLoad = new Runde("test.xml");
                rundeLoad.spielloop();
                break;

        }



        //Runde geladeneRunde = SERWorker.readSER("Test.ser");
        //geladeneRunde.spielloop();

        // Bestätigen, dass es geklappt hat:
        //System.out.println("Spielstand geladen!");1
        //geladeneRunde.start();

    }

}
