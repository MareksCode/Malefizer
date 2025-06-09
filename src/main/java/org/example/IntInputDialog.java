package org.example;

import javax.swing.*;
import java.awt.*;

public class IntInputDialog {
    private static int result = -1;
    private static String resultString = null;

    public static String getInputWithMessage(String message) {
        final JFrame frame = new JFrame("Eingabe");
        final JTextField inputField = new JTextField(10);
        final JButton button = new JButton("Go");

        final Object lock = new Object();

        button.addActionListener(e -> {
            try {
                resultString = inputField.getText();
                synchronized (lock) {
                    lock.notify();
                }
                frame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Bitte gültige Zahl eingeben.");
            }
        });

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        frame.add(new JLabel(message));
        frame.add(inputField);
        frame.add(button);
        frame.pack();
        frame.setLocationRelativeTo(null); // zentrieren
        frame.setVisible(true);

        synchronized (lock) {
            try {
                lock.wait(); // warte auf Button-Klick
            } catch (InterruptedException ignored) {}
        }

        return resultString;
    }

    public static int show(String message) {
        try {
            result = Integer.parseInt(getInputWithMessage(message));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Bitte gültige Zahl eingeben.");
            getInputWithMessage(message);
        }
        return result;
    }
}
