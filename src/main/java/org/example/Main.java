package org.example;

import org.example.client.AuthClient;
import org.example.client.LoginResponse;
import org.example.client.SocketService;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            AuthClient loginService = new AuthClient();
            LoginResponse loginResponse = loginService.logIntoServer("h", "h", "localhost", 8443);

            SocketService socketService = new SocketService();
            socketService.connectAndListen(loginResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}