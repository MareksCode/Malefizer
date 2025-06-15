package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FeldGUI implements GUIface {
    public static final Color[] playerBackgroundColors = {new Color(150,150,255), new Color(255,150,150), new Color(150,255,150), new Color(255,165,100)};
    public static final Color[] playerColors = {Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE};

    private FeldPanel feldPanel;
    private JPanel objectivePanel;
    private JPanel centerPanel;
    private JFrame frame;
    private JLabel objectiveLabel;
    private JLabel amZugLabel;
    private JLabel wurfLabel;
    private JPanel mainPanel;

    private Font objectiveFont = new Font("Arial", Font.BOLD, 20);
    private Font amZugFont = new Font("Arial", Font.PLAIN, 20);
    private Font wurfFont = new Font("Arial", Font.PLAIN, 20);

    private Feld selectedFeld = null;
    private final Object feldLock = new Object(); // für synchronisierten Zugriff
    private Runde dazugehoerigeRunde;
    private boolean debugMode;

    public FeldGUI(Feld startFeld, Runde dazugehoerigeRunde) {
        this.dazugehoerigeRunde = dazugehoerigeRunde;

        // DEBUG AKTIVIEREN FENSTER
        JFrame invisibleFrame = new JFrame();
        invisibleFrame.setAlwaysOnTop(true);
        invisibleFrame.setUndecorated(true);
        invisibleFrame.setType(JFrame.Type.UTILITY);
        invisibleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        invisibleFrame.setLocationRelativeTo(null); //zentriert
        invisibleFrame.setVisible(true);

        int result = JOptionPane.showConfirmDialog(
                invisibleFrame,
                "Debug-Modus aktivieren?",
                "Starteinstellungen",
                JOptionPane.YES_NO_OPTION);

        debugMode = (result == JOptionPane.YES_OPTION);
        invisibleFrame.dispose();

        // MAIN FENSTER
        frame = new JFrame("Spielbrett");

        //mainfenster body:
        //oben
        objectivePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        objectiveLabel = new JLabel("Aufgabe: ???", SwingConstants.CENTER);
        objectiveLabel.setFont(objectiveFont);
        objectivePanel.add(objectiveLabel);
        objectivePanel.setOpaque(true);

        //unten
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        amZugLabel = new JLabel("Am Zug: Spieler ?", SwingConstants.CENTER);
        amZugLabel.setFont(amZugFont);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 6, 2));

        wurfLabel = new JLabel("Würfelergebnis: ?", SwingConstants.CENTER);
        wurfLabel.setFont(wurfFont);
        bottomPanel.add(amZugLabel);
        bottomPanel.add(wurfLabel);

        //center
        feldPanel = new FeldPanel(startFeld, this, debugMode);
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(feldPanel, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        //main panel mit allem
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(objectivePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        //mainfenster head
        JMenuBar menuBar = new JMenuBar();
        JMenu dateiMenu = new JMenu("Datei");

        JMenuItem saveXmlItem = new JMenuItem("Als .xml speichern");
        JMenuItem saveSerItem = new JMenuItem("Als .ser speichern");

        saveXmlItem.addActionListener(e -> saveXmlDialog());
        saveSerItem.addActionListener(e -> saveSerDialog());

        dateiMenu.add(saveXmlItem);
        dateiMenu.add(saveSerItem);
        menuBar.add(dateiMenu);

        //alles zusammenfügen und ab geht er
        frame.setJMenuBar(menuBar);
        frame.setContentPane(mainPanel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setAutoRequestFocus(true);
        frame.setAlwaysOnTop(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setAlwaysOnTop(false); //fenster wieder "schließbar" machen
    }

    private void saveSerDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Als .ser speichern");
        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".ser")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".ser");
            }
            System.out.println("Speichern als .ser: " + fileToSave.getAbsolutePath());

            try {
                this.dazugehoerigeRunde.saveAsSer(fileToSave.getAbsolutePath());
                this.showNotification("Datei als .ser gespeichert!", 4000);
            } catch (Exception e) {
                this.showNotification("Etwas ist schiefgelaufen. Bitte probiere es erneut.", 4000);
            }
        }
    }

    private void saveXmlDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Als .xml speichern");
        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xml")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xml");
            }
            System.out.println("Speichern als .xml: " + fileToSave.getAbsolutePath());

            try {
                this.dazugehoerigeRunde.saveAsXml(fileToSave.getAbsolutePath());
                this.showNotification("Datei als .xml gespeichert!", 4000);
            } catch (Exception e) {
                this.showNotification("Etwas ist schiefgelaufen. Bitte probiere es erneut.", 4000);
            }
        }
    }

    public Feld selectFeld() throws InterruptedException {
        synchronized (feldLock) {
            selectedFeld = null;
            System.out.println("starting ui input");
            while (selectedFeld == null) {
                feldLock.wait();
            }
            System.out.println("Selected: "+selectedFeld.toString());
            return selectedFeld;
        }
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public void setWuerfelErgebnis(int erg) {
        SwingUtilities.invokeLater(() -> wurfLabel.setText("Würfelergebnis: " + erg));
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
    @Override
    public void geschlagenNotification(String msg, int dauer) {
        JWindow noti = new JWindow(frame);

        JLabel label = new JLabel(msg);
        label.setOpaque(true);
        label.setBackground(new Color(255, 0, 0, 140));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        label.setFont(new Font("", Font.PLAIN, 20));

        noti.getContentPane().add(label);
        noti.pack();

        int x = frame.getX() + frame.getWidth() / 2 - noti.getWidth() / 2;
        int y = frame.getY() + frame.getHeight() - noti.getHeight() - 100;
        noti.setLocation(x, y);

        noti.setAlwaysOnTop(true);
        noti.setVisible(true);

        new Timer(dauer, e -> noti.dispose()).start();
    }
    @Override
    public void setCurrentlyAmZug(int amZug) {
        SwingUtilities.invokeLater(() -> {
            String HexSpielerFarbe = toHex(playerColors[amZug]);
            amZugLabel.setText("<html>Am Zug: <span style='color:" + HexSpielerFarbe + "; font-weight:bold;'>Spieler " + (amZug+1) + "</span></html>");

            objectivePanel.setBackground(playerBackgroundColors[amZug]);
            objectivePanel.setBorder(BorderFactory.createLineBorder(playerColors[amZug], 4));
            objectivePanel.setOpaque(true);
            objectivePanel.repaint();
        });
    }
    @Override
    public void showNotification(String msg, int dauer) {
        JWindow noti = new JWindow(frame);

        JLabel label = new JLabel(msg);
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 170));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        label.setFont(new Font("Arial", Font.PLAIN, 20));

        noti.getContentPane().add(label);
        noti.pack();

        int x = frame.getX() + frame.getWidth() / 2 - noti.getWidth() / 2;
        int y = frame.getY() + frame.getHeight() - noti.getHeight() - 50;
        noti.setLocation(x, y);

        noti.setAlwaysOnTop(true);
        noti.setVisible(true);

        new Timer(dauer, e -> noti.dispose()).start();
    }
    @Override
    public void setObjective(String objective) {
        SwingUtilities.invokeLater(() -> objectiveLabel.setText("Aufgabe: " + objective));
    }
    @Override
    public void zeigeWurfDialog(int WuerfelErgebnis) {
        //popup 1
        JButton wuerfelnButton = new JButton("Würfeln");
        JPanel panel = new JPanel();
        panel.add(new JLabel("Ein neuer Spieler ist am Zug. Bitte würfeln!"));
        panel.add(wuerfelnButton);

        JDialog dialog = new JDialog(frame, "Würfeln", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        //popup 2
        wuerfelnButton.addActionListener(e -> {
            dialog.dispose(); //tschau popup

            showNotification("Es wurde eine " + WuerfelErgebnis + " gewürfelt! Wähle eine der Figuren aus, um sie zu bewegen.", 4000);
            setWuerfelErgebnis(WuerfelErgebnis);
        });

        dialog.setVisible(true);
    }
}