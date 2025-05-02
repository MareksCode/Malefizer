package org.example;

public class Main {
    public static void main(String[] args) throws Exception {
        //XMLSender.sendXmlFileToServer(new File("out.xml"), "http://localhost:8000/api/upload");
        Runde runde = new Runde(2);
        runde.start();
    }
}