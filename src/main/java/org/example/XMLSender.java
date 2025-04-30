package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class XMLSender {

    public static void sendXmlFileToServer(File xmlFile, String targetUrl) throws IOException {
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)
        ) {
            // Dateiteil
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + xmlFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: application/xml").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(xmlFile.toPath(), output);
            output.flush();
            writer.append(CRLF).flush();

            // Ende
            writer.append("--" + boundary + "--").append(CRLF).flush();
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Server Response: " + responseCode + " - " + connection.getResponseMessage());

        // Antwort lesen (optional)
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                System.out.println(responseLine);
            }
        }
    }
}
