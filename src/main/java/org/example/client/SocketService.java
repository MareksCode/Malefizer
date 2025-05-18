package org.example.client;

import org.example.Feld;
import org.example.SpielfeldHeinz;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SocketService {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Map<String, CompletableFuture<String>> pending = new ConcurrentHashMap<>();

    public void connectAndListen(LoginResponse login) throws Exception {
        //socket = new Socket(login.getHost(), login.getPort());

        SSLSocket socket = (SSLSocket) createInsecureSSLSocket(login.getHost(), login.getPort());

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("🟢 Lokaler Server erstellt auf: " + login.getHost() + ":" + login.getPort());

        // Auth-Token senden
        out.println(login.getToken());

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
                    case "MOVE":
                        break;
                    case "FAIL":
                        break;
                    case "ROLL":
                        break;
                    case "EXIT":
                        closeConnection();
                        break;
                    case "PING":
                        out.println("PONG");
                        break;
                }


                if (serverMsg.contains("EXIT")) {
                    closeConnection();
                }

                if (serverMsg.contains("PING")) {
                    out.println("PONG");
                }
            }
        } catch (IOException e) {
            System.out.println("🔴 Verbindung zum Server verloren." + e);
        }
    }

    public void spielerZiehe(Feld feld, int feldnummer) throws IOException ) {

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
