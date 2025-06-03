package org.example.client;

import org.example.Feld;
import org.example.IntInputDialog;
import org.example.Runde;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class SocketService {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int rundenNummer;
    private final Map<String, CompletableFuture<Integer>> pending = new ConcurrentHashMap<>();

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
        Thread listenerThread = new Thread(this::listenToServer);
        listenerThread.start();

        // Client darf nach Bedarf schreiben (z.B. auf Anfrage)
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            String input = scanner.nextLine();
//            if (input.equalsIgnoreCase("EXIT")) break;
//            out.println(input); // WARNUNG: Nur wenn erlaubt senden!
//        }

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
                        runde.setSpieler(args[1], args[2]);
                        break;
                    case "MOV":
                        runde.bewege(args[1], args[2]);
                        break;
                    case "TURN":
                        runde.macheZug();
                        break;
                    case "FAIL":
                        System.out.println("Fehler gesendet vom server: " + serverMsg);
                        break;
                    case "ROLL":
                        returnWuerfelErgebniss(args[1], args[2]);
                        break;
                    case "EXIT":
                        closeConnection();
                        break;
                    case "PING":
                        break;
                    case "CRTSTN":
                        runde.createNewSpielsteinOnFeld(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                        break;
                    case "SETMAP": //wichtig das erste element zu trennen aber nciht nur index 1 zu nehmen, da in der xml auch mit : gearbeitet wird
                        String xmlStr = String.join(":", Arrays.copyOfRange(args, 1, args.length));
                        runde = new Runde(this, xmlStr);
                        break;
                    case "WAIT":
                        if(Objects.equals(args[1], "SPERR")) {
                            System.out.println("bewege den blocker hierunter");
                            try {
                                sleep(300); //delay, da der thread erstellt werden muss und die rückgabe automatisch sit
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            out.printf("BLOCKER_MOVE:" + IntInputDialog.show("Wohin soll der Sperrstein bewegt werden (feldId): ") + "\n");
                            System.out.println("blocker wurde gesendet?");
                        }
                        break;
                    case "WIN":
                        System.out.println("Spieler " + args[1] + " hat gewonnen");
                        out.println("EXIT");
                        closeConnection();
                        break;
                    default:
                        System.out.println("🔴 Unbekannter Server-Befehl: " + serverMsg);
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

    private void returnWuerfelErgebniss(String correlationId, String wurfelzahl) {
        int zahl = Integer.parseInt(wurfelzahl);
        CompletableFuture<Integer> future = pending.remove(correlationId);

        if (future != null) {
            future.complete(zahl); // Antwort dem wartenden Thread geben
        } else {
            System.out.println("🔴 Unbekannter Wurfel-Request: " + correlationId);
        }
    }


    public CompletableFuture<Integer> requestRoll(int playerId) {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Integer> fut = new CompletableFuture<>();

        pending.put(correlationId, fut);      // ungeprüft cast ok

        out.printf("WURFEL:%s:%d%n", correlationId, playerId);      // z.B.  ROLL:<cid>:<player>
        return fut;
    }

    public void bewegeSperrstein(String feldIdTo){
        out.printf("BLOCKER_MOVE:"+feldIdTo+"\n");
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
