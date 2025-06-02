package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Menu {


    public int Menu(int spieleranzahl) throws Exception {

        System.out.println("How many players want to play? max. 4 players");

        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));


        try{
            spieleranzahl = Integer.parseInt(read.readLine());
            if(spieleranzahl<1 || spieleranzahl>4) { //kein Spielabbruch bei falscher Zahleingabe
                return Menu(spieleranzahl);
            }
        } catch (NumberFormatException e){
            return Menu(spieleranzahl);
        }

        Runde runde = new Runde(spieleranzahl);
        runde.start();
        return spieleranzahl;
    }
}
