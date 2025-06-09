package org.example;

import javax.swing.*;

public class FeldGUI implements GUIface {
    private FeldPanel feldPanel;
    private JFrame frame;

    public FeldGUI(Feld startFeld, int playerId) {
        feldPanel = new FeldPanel(startFeld, playerId);
        frame = new JFrame("Spielbrett");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(feldPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public void setPlayerId(int playerId) {
        feldPanel.setPlayerIdPanel(playerId);
    }

    @Override
    public void update(Feld feld) {
        feldPanel.repaintNewFields(feld);  // Neuzeichnen
    }

    @Override
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(frame, msg);
    }

}
