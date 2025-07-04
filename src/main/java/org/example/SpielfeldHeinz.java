package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.example.stein.Krone;
import org.example.stein.Sperrstein;
import org.example.stein.Spielstein;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpielfeldHeinz implements Serializable {
    private static final long serialVersionUID = 1L;

    static int[][] richtungen = {{-1,0},{1,0},{0,1},{0,-1}};
    public static Map<String, Feld> feldMap = new HashMap<>();
    static Map<Integer, Feld> spawnMap = new HashMap<>(); // <id, spawnfeld>
    @Deprecated
    public static int walkingId = 0;
    @Deprecated
    static int walkingIdKrown = 0;
    @Deprecated
    static int walkingIdFeld = 0;
    private static Runde runde;
    static Map<Integer, Feld> spawnFelder = new HashMap<>();


    public SpielfeldHeinz(Runde runde) {
        SpielfeldHeinz.runde = runde;
    }

    public static SpielfeldHeinz getInstance(Runde runde) throws Exception {
        return getInstance(runde, "spielfeld.xml");
    }

    public static SpielfeldHeinz getInstance(Runde runde, String filename) throws Exception {

        Document doc = XMLWorker.readXML(filename);
        Element root = doc.getDocumentElement();
        Element felder = (Element) root.getElementsByTagName("felder").item(0);
        Element kanten = (Element) root.getElementsByTagName("kanten").item(0);
        Element spawns = (Element) root.getElementsByTagName("spawns").item(0);
        
        NodeList fields = felder.getElementsByTagName("feld");
        NodeList edges = kanten.getElementsByTagName("kante");
        NodeList spawnList = spawns.getElementsByTagName("spawn");



        // create fields
        for (int i = 0; i < fields.getLength(); i++) {
            Element field = (Element) fields.item(i);
            String id = field.getAttribute("id");
            Feld feld = getFeld(runde, field, id);

            feldMap.put(id, feld);
        }

        // create spawnfelds
        for (int i = 0; i < spawnList.getLength(); i++) {

            Element e = (Element) spawnList.item(i);
            Feld feld = feldMap.get(e.getAttribute("feldId"));
            feld.setSpielerSpawn(true);
            spawnFelder.put(Integer.valueOf(i), feld);
            spawnMap.put(Integer.valueOf(e.getAttribute("id")), feldMap.get(String.valueOf(i)));
        }

        // create edges
        for (int i = 0; i < edges.getLength(); i++) {
            Element edge = (Element) edges.item(i);
            String from = edge.getAttribute("from");
            String to = edge.getAttribute("to");
            feldMap.get(from).addNachbarn(feldMap.get(to)); //doppelte verlinkung in beide richtungen wichtig, da sonst doof (kanten nur einfach gespeichert in xml)
            feldMap.get(to).addNachbarn(feldMap.get(from));
        }

        return new SpielfeldHeinz(runde);
    }

    private static Feld getFeld(Runde runde, Element field, String id) throws Exception {
        String data = field.getAttribute("data");

        int posX = Integer.parseInt(field.getAttribute("posX"));
        int posY = Integer.parseInt(field.getAttribute("posY"));
        Feld feld = new Feld(new ArrayList<>());
        feld.setPosition(new Position(posX, posY));
        feld.setId(Integer.parseInt(id));

        if(!data.isEmpty()){
            var dataSplit = data.split(":");
            if(dataSplit[0].equals("Krone")){
                feld.setBesetzung(new Krone(Integer.parseInt(dataSplit[1]), runde));
            }else if(dataSplit[0].equals("Sperrstein")){
                Sperrstein newSperrstein = new Sperrstein(Integer.parseInt(dataSplit[1]), runde);
                feld.setBesetzung(newSperrstein);
                newSperrstein.setFeld(feld);
                //Neu
            }else if(dataSplit[0].equals("Spielstein")) {
                Spielstein spielstein = new Spielstein(Integer.parseInt(dataSplit[2]), runde, Integer.parseInt(dataSplit[1]));
                feld.setBesetzung(spielstein);
                spielstein.setFeld(feld); // <-- wichtig: damit die Referenz zum Feld korrekt ist
            }

            //Neu

            //}else if(dataSplit[0].equals("Spielstein")){
                //feld.setBesetzung(new Spielstein(Integer.parseInt(dataSplit[1]), runde, -1));
           // }
        }
        return feld;
    }

    // Neu
    public static Collection<Feld> getAlleFelder() {
        return feldMap.values();
    }
    //Neu

    public static Feld getStartfeld() {
        return feldMap.get("0");
    }

    public static Map<Integer, Feld> getSpawnFelder() { return spawnFelder; }

    public static Map<Integer, Feld> getSpawnMap() {
        return spawnMap;
    }

    @Deprecated //remove n future update whenis secured never used again
    public Feld createSpielfeld() throws FileNotFoundException {
        return null;
    }

    private static Feld erstelleFeld(char[][] datei, int posX, int posY) throws Exception {
        String key = posX + "." + posY;
        if (feldMap.containsKey(key)) return feldMap.get(key);

        Feld feld = new Feld(new ArrayList<>());
        feld.setPosition(new Position(posX, posY));
        feld.setId(walkingIdFeld++);

        // <<< KORREKTER ZUGRIFF >>>
        switch (datei[posY][posX]) {
            case 'K' -> feld.setBesetzung(new Krone(walkingIdKrown++, runde));
            case 'S' -> {
                Sperrstein newSperrstein = new Sperrstein(walkingId++, runde);
                feld.setBesetzung(newSperrstein);
                newSperrstein.setFeld(feld);
            }
            case 'T' -> feld.setSpielerSpawn(true);
        }

        feldMap.put(key, feld);

        for (int[] richtung : richtungen) {
            int nx = posX + richtung[0];
            int ny = posY + richtung[1];

             if (nx >= 0 && ny >= 0 && ny < datei.length && nx < datei[ny].length &&
                    (datei[ny][nx] == '0' || datei[ny][nx] == 'S' || datei[ny][nx] == 'K' || datei[ny][nx] == 'T')) {

                Feld nachbar = erstelleFeld(datei, nx, ny);
                feld.addNachbarn(nachbar);
            }
        }
        return feld;
    }

}
