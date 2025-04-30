package org.example;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GameClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String baseUrl = "http://localhost:8000";

    private String token;

    public void authenticate(String username, String password) throws IOException, InterruptedException {
        String body = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            token = new JSONObject(response.body()).getString("token");
        } else {
            throw new RuntimeException("Authentication failed: " + response.body());
        }
    }

    public void joinGame(long gameId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/game/join?gameId=" + gameId))
                .header("Authorization", token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void sendMove(String xmlMove) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/game/move"))
                .header("Authorization", token)
                .header("Content-Type", "application/xml")
                .POST(HttpRequest.BodyPublishers.ofString(xmlMove))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String getOpenGames() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/xml/getopengames"))
                .header("Authorization", token)
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

}
