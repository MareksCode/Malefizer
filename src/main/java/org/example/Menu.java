package org.example;

import org.example.bot.Fight_Bot;
import org.example.bot.Niki_Bot;
import org.example.bot.Smart_Bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Menu {


    public int Menu() throws Exception {

        int spieleranzahl=0;

        System.out.println("How many players want to play? max. 4 players");

        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));


        try{
            spieleranzahl = Integer.parseInt(read.readLine());
            if(spieleranzahl>=1 && spieleranzahl<4) { //kein Spielabbruch bei falscher Zahleingabe
                    System.out.println("what difficulty should the bots have?\n" +
                            "1      easy\n" +
                            "2      mid\n" +
                            "3      hard");
                    BufferedReader schwierigkeitRead = new BufferedReader(new InputStreamReader(System.in));
                    int botSchwierigkeit = Integer.parseInt(schwierigkeitRead.readLine());

                    Runde runde = new Runde(spieleranzahl, botSchwierigkeit);

                    switch (botSchwierigkeit) {
                        case 1:     //easy

                            System.out.println("You have chosen easy difficulty for the bots.");

                            break;

                        case 2:     //mid

                            System.out.println("You have chosen mid difficulty for the bots.");

                            break;

                        case 3:     //hard

                            System.out.println("You have chosen hard difficulty for the bots.");

                            break;

                        default:
                            return Menu();
                    }
                    runde.start();

            }
        } catch (NumberFormatException e){

            return Menu();
        }

        Runde runde = new Runde(spieleranzahl, 0);
        runde.start();
        return spieleranzahl;
    }
}
