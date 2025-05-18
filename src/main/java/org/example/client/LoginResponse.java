package org.example.client;

public class LoginResponse {
    private String host; //eigene ip adresse aus sicht des servers
    private int port; //maby default value?
    private String token;

    public LoginResponse(String host, int port, String token) {
        this.host = host;
        this.port = port;
        this.token = token;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getToken() {
        return token;
    }
}
