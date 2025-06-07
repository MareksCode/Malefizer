package org.example;

import org.example.stein.Spielstein;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.*;

public class XMLWorker {
    public static Document readXML(String filename) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new File(filename));
    }

    public static void toXML(Feld startFeld, Runde runde, String filename ) throws ParserConfigurationException, TransformerException {
        Document doc = createDocument(startFeld, runde);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        System.out.println(System.getProperty("user.dir"));

        transformer.transform(new DOMSource(doc), new StreamResult(new File(System.getProperty("user.dir") + filename)));
        System.out.println("XML gespeichert unter: " + filename);
    }

    private static Document createDocument(Feld startFeld,Runde runde) throws ParserConfigurationException {
        Map<Integer, Feld> felder = new HashMap<>();
        Set<String> kanten = new HashSet<>();
        collectToMap(startFeld, felder, kanten, new HashSet<>());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element graphElement = doc.createElement("graph");
        doc.appendChild(graphElement);
        Element felderElement = doc.createElement("felder");
        Element kantenElement = doc.createElement("kanten");
        Element spawnsElement = doc.createElement("spawns");
        graphElement.appendChild(spawnsElement);
        graphElement.appendChild(felderElement);
        graphElement.appendChild(kantenElement);

        for (String kante : kanten) {
            String[] parts = kante.split("->");
            Element kanteElement = doc.createElement("kante");
            kanteElement.setAttribute("from", parts[0]);
            kanteElement.setAttribute("to", parts[1]);
            kantenElement.appendChild(kanteElement);
        }

        for (Feld feld : felder.values()) {
            Element feldElement = doc.createElement("feld");
            feldElement.setAttribute("id", Integer.toString(feld.getId()));
            String data;
            if(feld.getBesetzung() == null) data = "null";
            else data = feld.getBesetzung().toString();
            if(data.contains("Spielstein")) {
                String datan[] = data.split(":");
                String spId = String.valueOf(((Spielstein) feld.getBesetzung()).getSpielerId());
                data = datan[0] + ":" + spId + ":" + datan[1];
            }
            feldElement.setAttribute("data", data);
            feldElement.setAttribute("posX", String.valueOf(feld.getPosition().x));
            feldElement.setAttribute("posY", String.valueOf(feld.getPosition().y));
            felderElement.appendChild(feldElement);
        }

        Element playersElemets = doc.createElement("players");
        graphElement.appendChild(playersElemets);

        int walkingIdSpawns = 0;
        for(SpielerObjekt sp: runde.spielerListe){
            Element e = doc.createElement("spawn");
            e.setAttribute("id", String.valueOf(walkingIdSpawns++));
            e.setAttribute("feldId", String.valueOf(sp.getSpawnFeld().getId()));
            spawnsElement.appendChild(e);
        }


        int playerid = 0;
        for(SpielerObjekt spieler: runde.spielerListe ){
            Element playerElement = doc.createElement("player");
            playerElement.setAttribute("id", Integer.toString(playerid++));
            playersElemets.appendChild(playerElement);
            for(Spielstein spielstein: spieler.getSpielsteinListe()){
                Element spElement = doc.createElement("spielstein");
                String id;
                if(spielstein.getCurrentFeld() != null){
                    id = Integer.toString(spielstein.getCurrentFeld().getId());
                } else id = "-1";
                spElement.setAttribute("feldId",  id);
                playerElement.appendChild(spElement);
            }
        }

        Element amZugElement = doc.createElement("amZug");
        amZugElement.setAttribute("zugCount", Integer.toString(runde.getAmZug()));
        graphElement.appendChild(amZugElement);

        return doc;
    }

    private static void collectToMap(Feld feld, Map<Integer, Feld> felder, Set<String> kanten, Set<Integer> marked) {
        if (marked.contains(feld.getId())) return;
        marked.add(feld.getId());
        felder.put(feld.getId(), feld);

        for (Feld nachbar : feld.getNachbarn()) {
            if(kanten.contains(nachbar.getId() + "->" + feld.getId())) continue;
            kanten.add(feld.getId() + "->" + nachbar.getId());
            if (!marked.contains(nachbar.getId())) {
                collectToMap(nachbar, felder, kanten, marked);
            }
        }
    }
}
