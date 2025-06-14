package org.example;

import javax.swing.*;
import java.awt.*;

public class FeldGUI implements GUIface {
    private FeldPanel feldPanel;
    private JFrame frame;

    public FeldGUI(Feld startFeld) {
        feldPanel = new FeldPanel(startFeld);
        frame = new JFrame("Spielbrett");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.add(feldPanel);

        frame.pack(); // schwarze magie
        frame.setVisible(true);
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
