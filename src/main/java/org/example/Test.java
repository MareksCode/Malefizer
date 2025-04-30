package org.example;

import org.example.client.AuthClient;
import org.example.client.LoginResponse;
import org.example.client.SocketService;

public class Test {
    public static void main(String[] args) {
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
