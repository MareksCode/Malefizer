package org.example;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TerminalAusgabe implements GUIface {
    Feld stfld;
    Set<Integer> abgelatscht = new HashSet<>();

    public void update(Feld startFeld){
        stfld = startFeld;
    }

    public void showMessage(String message){}

    private void repaint (){
        int x =0;
        int y=0;
        searchNachbar(x,y,stfld);

        char[][] spielArray;
        char[][] speilfeld={{'╔','═', '╗'},{'║', 'X', '║'},{'╚', '═', '╝'}};
    }

    private void searchNachbar(int x, int y, Feld feld){
        for(Feld nachbar: feld.getNachbarn()){
            if(!abgelatscht.contains(feld.getId())){
                int xnew = x*4;
                int ynew = y*4;
                abgelatscht.add(feld.getId());
                if(x<feld.getPosition().x) x=feld.getPosition().x;
                if(y<feld.getPosition().y) y=feld.getPosition().y;

                searchNachbar(x,y,nachbar);
            }
        }


    }

}
