package org.example.client;

import org.example.Feld;
import org.example.Runde;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SocketService {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int rundenNummer;
    private final Map<String, CompletableFuture<String>> pending = new ConcurrentHashMap<>();

    private Runde runde;

    public SocketService(int rundenId){
        this.rundenNummer = rundenId;
    }

    public void onExit(){
        System.out.println("🟡 Verbindung beenden...");
        out.println("EXIT");
    }

    public void connectAndListen(LoginResponse login) throws Exception {
        //socket = new Socket(login.getHost(), login.getPort());

        SSLSocket socket = (SSLSocket) createInsecureSSLSocket(login.getHost(), login.getPort());

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("🟢 Lokaler Server erstellt auf: " + login.getHost() + ":" + login.getPort());

        // Auth-Token senden
        out.println(login.getToken());
        out.println("SETGAME:" + rundenNummer);

        // Server-Listener-Thread starten
        new Thread(this::listenToServer).start();

        // Client darf nach Bedarf schreiben (z.B. auf Anfrage)
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("EXIT")) break;
            out.println(input); // WARNUNG: Nur wenn erlaubt senden!
        }

        closeConnection();
    }

    private void listenToServer() {
        try {
            String serverMsg;
            while ((serverMsg = in.readLine()) != null) {
                System.out.println("🔹 Server: " + serverMsg);

                // Optional: Automatische Antwortlogik
                String args[] = serverMsg.split(":");
                switch (args[0]) {
                    case "SETSPWN":
                        runde.setSpieler(args[1]);
                        break;
                    case "MOV":
                        runde.macheZug();
                        break;
                    case "TURN":
                        break;
                    case "FAIL":
                        System.out.println("Fehler gesendet vom server: " + args[1]);
                        break;
                    case "ROLL":
                        returnWuerfelErgebniss(args[1], args[2]);
                        break;
                    case "EXIT":
                        closeConnection();
                        break;
                    case "PING":
                        out.println("PONG");
                        break;
                    case "SETMAP": //wichtig das erste element zu trennen aber nciht nur index 1 zu nehmen, da in der xml auch mit : gearbeitet wird
                        String xmlStr = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                        runde = new Runde(this, xmlStr);
                        break;
                }


                if (serverMsg.contains("EXIT")) {
                    closeConnection();
                }
            }
        } catch (IOException e) {
            System.out.println("🔴 Verbindung zum Server verloren." + e);
        }
    }


    /**feld von woher, feldnummer zu welchem
     * source -> target
    **/
    public void spielerZiehe(Feld feld, int feldnummer) {
        out.printf("MOV:%d:%d%n", feld.getId(), feldnummer);
    }

    private void returnWuerfelErgebniss(String correlationId, String id) {
        CompletableFuture<String> future = pending.remove(id);
        if (future != null) {
            future.complete(correlationId); // Antwort dem wartenden Thread geben
        }
    }


    public CompletableFuture<Integer> requestRoll(int playerId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Integer> fut = new CompletableFuture<>();
        pending.put(correlationId, (CompletableFuture) fut);      // ungeprüft cast ok
        out.printf("ROLL:%s:%d%n", correlationId, playerId);      // z.B.  ROLL:<cid>:<player>
        return fut;
    }

    private void closeConnection() throws IOException {
        socket.close();
        System.out.println("Verbindung geschlossen.");
    }

    public static Socket createInsecureSSLSocket(String host, int port) throws Exception {
        // TrustManager, der ALLE Zertifikate akzeptiert
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
        };

        // SSL-Kontext mit unsicherem TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        // Socket erstellen
        SSLSocketFactory factory = sslContext.getSocketFactory();
        return factory.createSocket(host, port);
    }

}
