package org.example.client;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;

import java.nio.charset.StandardCharsets;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class RequestClient {

    private final HttpClient http;
    private final LoginResponse login;
    private final String baseUrl;

    public RequestClient(LoginResponse login, String baseUrl) {
        this.login   = login;
        this.baseUrl = baseUrl;
        this.http = null; // TODO
    }

    /**
     * Executes a GET request (HTTPS) for the given relative path and parses the XML result.
     *
     * @param relativePath something like "join.xml" or "games/mygames.xml"
     * @return parsed org.w3c.dom.Document
     */
    public Document requestXml(String relativePath)
            throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {

        disableSslVerification();

        URL url = new URL(baseUrl + relativePath);

        System.out.println("URL: " + url);

        HttpsURLConnection loginConn = (HttpsURLConnection) url.openConnection();
        loginConn.setRequestMethod("GET");
        loginConn.setDoOutput(true);
        loginConn.setRequestProperty("Cookie", "JSESSIONID=" + login.getToken());
        loginConn.setRequestProperty("Content-Type", "application/xml");

        int status = loginConn.getResponseCode();
        System.out.println(status);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();


        if(status >= 200 && status < 300) {
            //System.out.println("Response: " + loginConn.getInputStream());
            //try{System.out.println(readStreamToString(loginConn.getInputStream()));}catch (Exception e){}
            System.out.println("here");
        }
        Document d = db.parse(loginConn.getInputStream());
        System.out.println("also here");
        return d;
    }

    public void sendJoinRequest(int gameId) throws Exception {
        disableSslVerification();

        try {
            URL url = new URL(baseUrl + "/api/join");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Cookie setzen
            conn.setRequestProperty("Cookie", "JSESSIONID=" + login.getToken());
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Form-Body vorbereiten
            String form = "id=" + URLEncoder.encode(String.valueOf(gameId), StandardCharsets.UTF_8);

            // Schreiben
            try (OutputStream os = conn.getOutputStream()) {
                os.write(form.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            System.out.println("Status: " + status);

            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Hostname-Check deaktivieren (z. B. localhost ohne gültiges Zertifikat)
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
