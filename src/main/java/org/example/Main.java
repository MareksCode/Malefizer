package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class Main  {
    public static void main(String[] args) throws Exception {


        System.out.println("new Game = 1\nload Game from xml = 2");

        int auswahl;
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));

        try{
            auswahl = Integer.parseInt(read.readLine());
        } catch (NumberFormatException e){
            return;
        }

        switch (auswahl) {
            case 1:     //new game
                int spieleranzahl=0;
                Menu menu = new Menu();
                menu.Menu(spieleranzahl);

                break;

            case 2:     //load Game
                Runde rundeLoad = new Runde("test.xml");
                rundeLoad.spielloop();
                break;

            default:
                System.out.println("not an option");
        }



        //Runde geladeneRunde = SERWorker.readSER("Test.ser");
        //geladeneRunde.spielloop();

        // Bestätigen, dass es geklappt hat:
        //System.out.println("Spielstand geladen!");1
        //geladeneRunde.start();

    }

}
