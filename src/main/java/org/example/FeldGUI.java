package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class FeldGUI implements GUIface {
    private FeldPanel feldPanel;
    private JFrame frame;

    private Feld selectedFeld = null;
    private final Object feldLock = new Object(); // für synchronisierten Zugriff

    public FeldGUI(Feld startFeld) {
        feldPanel = new FeldPanel(startFeld, this);
        frame = new JFrame("Spielbrett");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.add(feldPanel);

        frame.pack(); // schwarze magie
        frame.setVisible(true);
    }

    public Feld selectFeld() throws InterruptedException {
        System.out.println("1");
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

    // Wird vom FeldPanel aufgerufen, wenn ein Feld ausgewählt wurde
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
