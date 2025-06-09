package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        String hostAdress = IntInputDialog.getInputWithMessage("die ip des servers: ");
        SwingUtilities.invokeLater(() -> new GameLauncher(hostAdress).setVisible(true));
    }
}