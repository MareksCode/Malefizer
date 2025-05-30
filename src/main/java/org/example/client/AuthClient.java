package org.example.client;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

public class AuthClient {

    private LoginResponse logIntoServer(String username, String password) throws IOException {
        return logIntoServer(username, password, "localhost", 8443);
    }
    public LoginResponse logIntoServer(String username, String password, String ip) throws IOException {
        return logIntoServer(username, password, ip, 8443);
    }

    /** zum einloggen beim server. andere methoden geben standartwerte mit (function overload) **/
    public LoginResponse logIntoServer(String username, String password, String ip, int port) throws IOException {
        disableSslVerification();
        var URL = "https://" + ip + ":" + port + "/auth";

        URL loginUrl = new URL(URL);
        HttpsURLConnection loginConn = (HttpsURLConnection) loginUrl.openConnection();
        loginConn.setRequestMethod("POST");
        loginConn.setDoOutput(true);
        loginConn.setInstanceFollowRedirects(false); // Wichtig: Redirects manuell behandeln
        loginConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String body = "username="+ username +"&password=" + password;
        try (OutputStream os = loginConn.getOutputStream()) {
            os.write(body.getBytes());
        }

        // 2. Header auswerten
        System.out.println(loginConn.getHeaderFields());
        Map<String, List<String>> headers = loginConn.getHeaderFields();
        String setCookieHeader = loginConn.getHeaderField("Set-Cookie");
        String redirectLocation = loginConn.getHeaderField("Location");

        LoginResponse lRep = new LoginResponse("localhost", 31415, setCookieHeader);

        if (redirectLocation.contains("error")) lRep.authIsFailed();

        return lRep;
    }
    static void disableSslVerification() {
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
