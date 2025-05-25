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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public static String toXML(Feld startFeld, Runde runde) throws ParserConfigurationException, TransformerException {
        Document doc = createDocument(startFeld,runde);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
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
            feldElement.setAttribute("data", feld.getBesetzung() != null ? feld.getBesetzung().toString() : "null");
            feldElement.setAttribute("posX", String.valueOf(feld.getPosition().x));
            feldElement.setAttribute("posY", String.valueOf(feld.getPosition().y));
            felderElement.appendChild(feldElement);
        }
        Element playersElemets = doc.createElement("players");
        graphElement.appendChild(playersElemets);
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


        return doc;
    }

    private static void collectToMap(Feld feld, Map<Integer, Feld> felder, Set<String> kanten, Set<Integer> marked) {
        if (marked.contains(feld.getId())) return;
        marked.add(feld.getId());
        felder.put(feld.getId(), feld);

        for(Feld nachbar : feld.getNachbarn()) {
            if (marked.contains(nachbar.getId())) continue;
            kanten.add(feld.getId() + "->" + nachbar.getId());
            collectToMap(nachbar, felder, kanten, marked);
        }
    }
}
