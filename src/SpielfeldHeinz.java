import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpielfeldHeinz {
    static int[][] richtungen = {{-1,0},{1,0},{0,1},{0,-1}};
    static Map<String, Feld> feldMap = new HashMap<>();
    static int walkingId = 0;
    static int walkingIdKrown = 0;
    private static Runde runde;

    public SpielfeldHeinz(Runde runde) {
        SpielfeldHeinz.runde = runde;
    }

    public Feld createSpielfeld() throws FileNotFoundException {
        char[][] gameFileArray = getFileContent.getFileAsArray();

        String key = "";

        for (int i = 0; i < gameFileArray.length; i++) {
            for (int j = 0; j < gameFileArray[i].length; j++) {
                if((gameFileArray[i][j] != 'x' ) && !feldMap.containsKey(gameFileArray[i][j])) {
                    erstelleFeld(gameFileArray, j, i);
                    key = i + "." + j;
                }
            }
        }
        for (Feld feld : feldMap.values()) {
            System.out.println(feld + " -> " + feld.getNachbarn());
        }
        return feldMap.get(key);
    }

    private static Feld erstelleFeld(char[][] datei, int posX, int posY) {
        String key = posX + "." + posY;
        if(feldMap.containsKey(key)) return feldMap.get(key);

        Feld feld = new Feld(new ArrayList<>());
        feld.setPosition(new Position(posX, posY));
        if (datei[posX][posY] == 'S') {
            feld.setBesetzung(new Sperrstein(walkingId++, runde));
            System.out.println("hier muss noch ein sperrstein erstellt werden! hier spielfeld heinz mit feld " + posX + " " + posY);
        }

        if(datei[posX][posY] == 'T') {
            feld.setSpielerSpawn(true);
        }

        if(datei[posX][posY] == 'K') {
            feld.setBesetzung(new Krone(walkingIdKrown++, runde));
        }


        feldMap.put(key, feld);

        for (int[] richtung: richtungen) {
            int nx = posX + richtung[0];
            int ny = posY + richtung[1];

            if(nx >= 0 && ny >= 0 && nx < datei.length && ny < datei[0].length && (datei[nx][ny] == '0' || datei[nx][ny] == 'S')) {
                Feld nachbar = erstelleFeld(datei, nx, ny);
                feld.addNachbarn(nachbar);
            }
        }
        return feld;
    }
}
