package org.example;

import org.example.stein.Spielstein;

import java.util.*;

public class TerminalAusgabe implements GUIface {
    Feld stfld;
    Set<Integer> abgelatscht = new HashSet<>();
    ArrayList<ArrayList<String>> spielArray = new ArrayList<>();


    String[][] spielfeld={{"╔","║", "╚"},{"════", "XXXX", "════"},{"╗", "║", "╝"}};

    public void update(Feld startFeld){

        stfld = startFeld;
        repaint(startFeld);
    }

    @Override
    public Feld selectFeld() throws InterruptedException {
        return null;
    }

    @Override
    public void setObjective(String objective) {

    }

    @Override
    public void setCurrentlyAmZug(int amZug) {

    }

    public void showMessage(String message){}

    @Override
    public void zeigeWurfDialog(int WuerfelErgebnis) {

    }

    @Override
    public void showNotification(String msg, int dauer) {

    }

    private void repaint (Feld startFeld){
        int x =0;
        int y=0;
        spielArray.clear();
        abgelatscht.clear();

        searchNachbar(x,y,startFeld);

        for(int i = 0; i < spielArray.size(); i++) {
            for(int j = 0; j < spielArray.get(i).size(); j++) {
                System.out.print(spielArray.get(i).get(j));
            }
            System.out.println(" ");
        }
    }

    private void searchNachbar(int x, int y, Feld feld){
        for(Feld nachbar: feld.getNachbarn()){

            int xnew = feld.getPosition().x * 4;
            int ynew = feld.getPosition().y * 4;


            String data = feld.getBesetzung() != null ? feld.getBesetzung().toString() : "null";

            String[] split = data.split(":");

            switch (split[0]) {
                case "Krone" -> spielfeld[1][1] = " K  ";
                case "Sperrstein" -> spielfeld[1][1] = " S  ";
                case "Spielstein" -> {
                    spielfeld[1][1] = "P" + (((Spielstein) feld.getBesetzung()).getSpielerId()+1) + "." + (Integer.parseInt(split[1])+1);
                }
                default -> spielfeld[1][1] = "    ";
            }

            String zahl;
            String zahlInt = String.valueOf(feld.getId());

            switch (zahlInt.length()) {
                case 1 -> zahl = "═══" + zahlInt;
                case 2 -> zahl = "══" + zahlInt;
                case 3 -> zahl = "═" + zahlInt;
                default -> zahl = zahlInt;
            }
            if (feld.istSpielerSpawn()) {
                spielfeld[1][0] = "SPWN";
            }

            // Dynamisches Schreiben ins große Ausgabearray
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int colIndex = xnew + i;
                    int rowIndex = ynew + j;

                    while (spielArray.size() <= rowIndex) {
                        spielArray.add(new ArrayList<>());
                    }

                    List<String> row = spielArray.get(rowIndex);

                    while (row.size() <= colIndex) {
                        row.add("  ");
                    }

                    row.set(colIndex, String.valueOf(spielfeld[i][j]));
                }
            }
            spielfeld[1][0] = "════";

            spielArray.get(ynew + 2).set(xnew + 1, zahl);
            if(!abgelatscht.contains(nachbar.getId())){
                abgelatscht.add(feld.getId());
                searchNachbar(feld.getPosition().x, feld.getPosition().y, nachbar);
            }
        }
    }

}
