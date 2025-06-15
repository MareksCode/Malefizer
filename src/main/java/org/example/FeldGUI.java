package org.example;

import javax.swing.*;
import java.awt.*;

public class FeldGUI implements GUIface {
    private FeldPanel feldPanel;
    private JFrame frame;
    private JLabel objectiveLabel;
    private JLabel amZugLabel;

    private Font objectiveFont = new Font("Arial", Font.BOLD, 25);
    private Font amZugFont = new Font("Arial", Font.ITALIC, 18);

    private Feld selectedFeld = null;
    private final Object feldLock = new Object(); // für synchronisierten Zugriff

    public FeldGUI(Feld startFeld) {
        feldPanel = new FeldPanel(startFeld, this);
        frame = new JFrame("Spielbrett");

        //top panel mit labels
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        objectiveLabel = new JLabel("Aufgabe: AAAAAAAAAAAAAAAAAAAAAA", SwingConstants.LEFT);
        objectiveLabel.setFont(objectiveFont);
        amZugLabel = new JLabel("Am Zug: Spieler ?", SwingConstants.RIGHT);
        amZugLabel.setFont(amZugFont);

        topPanel.add(objectiveLabel);
        topPanel.add(amZugLabel);

        //Hauptlayout
        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(feldPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack(); // schwarze magie
        frame.setVisible(true);
    }

    public Feld selectFeld() throws InterruptedException {
        synchronized (feldLock) {
            selectedFeld = null; // zurücksetzen
            System.out.println("starting ui input");
            while (selectedFeld == null) {
                feldLock.wait(); // warte auf Benutzereingabe
            }
            //System.out.println("ended");
            System.out.println("Selected: "+selectedFeld.toString());
            return selectedFeld;
        }
    }

    public void zeigeWurfDialog(int WuerfelErgebnis) {
        //popup 1
        JButton wuerfelnButton = new JButton("Würfeln");
        JPanel panel = new JPanel();
        panel.add(new JLabel("Bitte würfeln!"));
        panel.add(wuerfelnButton);

        JDialog dialog = new JDialog(frame, "Würfeln", true); // modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        //popup 2
        wuerfelnButton.addActionListener(e -> {
            dialog.dispose(); //tschau erstes popup

            JOptionPane.showMessageDialog(frame, "Du hast eine " + WuerfelErgebnis + " gewürfelt!", "Wurfergebnis", JOptionPane.INFORMATION_MESSAGE);
        });

        dialog.setVisible(true);
    }

    @Override
    public void setObjective(String objective) {
        SwingUtilities.invokeLater(() -> objectiveLabel.setText("Aufgabe: " + objective));
    }

    @Override
    public void setCurrentlyAmZug(int amZug) {
        SwingUtilities.invokeLater(() -> amZugLabel.setText("Am Zug: Spieler " + amZug));
    }

    //wird vom FeldPanel aufgerufen, wenn ein feld ausgewaehlt wurde
    public void feldWurdeGewaehlt(Feld feld) {
        //System.out.println("wurde gewaehlt");
        synchronized (feldLock) {
            selectedFeld = feld;
            feldLock.notifyAll();
        }
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

//try {
//        System.out.println("select feld NOWWWW");
//                gui.selectFeld();
//            } catch (InterruptedException ie) {
//        System.err.println(ie.getMessage());
//        }
