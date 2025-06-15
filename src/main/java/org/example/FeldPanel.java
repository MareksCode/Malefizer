package org.example;

import org.example.stein.Spielstein;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

class FeldPanel extends JPanel implements MouseListener {
    private FeldGUI feldGUI;

    private Set<Feld> felder;
    private final int feldRadius = 40;
    private final int spacing = 55;

    private int spielerKopfRadius = 15;
    private int spielerKoerperBreite = 25;
    private int spielerKoerperRundung = 13;
    private int spielerKoerperGroessse = 20;

    private int sperrsteinEinrueckung = 8;

    private final boolean DEBUG_MODE = true;

    public FeldPanel(Feld startFeld, FeldGUI feldGUI) {
        this.feldGUI = feldGUI;
        this.felder = collectFelder(startFeld);
        setPreferredSize(new Dimension(1000, 1000));
        setBackground(Color.WHITE);

        addMouseListener(this);
    }

    //Spielfeld Actions
    public void repaintNewFields(Feld feld) {
        felder = collectFelder(feld);
        repaint();
    }

    public Feld selectFeld(int x, int y) {
        Iterator<Feld> feldIterator = felder.iterator();
        double closestNum = Double.MAX_VALUE;
        Feld closestFeld = null;

        while (feldIterator.hasNext()) {
            Feld feldToCheck = feldIterator.next();
            Position feldPosition = feldToCheck.getPosition();

            Position umgerechneteFeldPosition = new Position(feldPosition.x * spacing + feldRadius/2, feldPosition.y * spacing + feldRadius/2);
            Position mausPosition = new Position(x, y);

            double distance = Position.getDistanz(mausPosition, umgerechneteFeldPosition);
            //System.out.println(feldToCheck.toString()  + " dist " + mausPosition.toString() + " = " + distance);

            if (distance < closestNum) {
                closestNum = distance;
                closestFeld = feldToCheck;
            }
        }
        //System.out.println("gefunden: "+closestFeld);
        if (closestFeld != null && closestNum < feldRadius) { //Spieler hat aimbot genutzt und das feld getroffen!
           // System.out.println("neues feld gefunden");
            return closestFeld;
        } else {
            feldGUI.showNotification("Bitte auf ein Feld klicken.", 2000);
            //System.out.println("nix gefunden");
            return null;
        }
    }

    //taktische breitensuche
    private Set<Feld> collectFelder(Feld start) {
        Set<Feld> besucht = new HashSet<>();
        Queue<Feld> queue = new LinkedList<>();
        queue.add(start);
        besucht.add(start);

        while (!queue.isEmpty()) {
            Feld current = queue.poll();
            for (Feld nachbar : current.getNachbarn()) {
                if (!besucht.contains(nachbar)) {
                    besucht.add(nachbar);
                    queue.add(nachbar);
                }
            }
        }

        return besucht;
    }

    //maus funktionen
    @Override
    public void mouseClicked(MouseEvent e) {
        Feld angeklickt = selectFeld(e.getX(), e.getY());
        if (angeklickt != null) {
            //System.out.println("gefunden");
            feldGUI.feldWurdeGewaehlt(angeklickt); // Rückgabe an GUI
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    //Icons
    private void drawPlayer(Graphics g, Position pos, int playerId) {
        Color playerColor = FeldGUI.playerColors[playerId];
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int xHead = pos.x * spacing + feldRadius/2 - spielerKopfRadius/2;
        int yHead = pos.y * spacing + feldRadius/2 - spielerKopfRadius;

        int xBody = pos.x * spacing + feldRadius/2 - spielerKoerperBreite/2;
        int yBody = pos.y * spacing + feldRadius/2;

        g2d.setColor(playerColor);

        g2d.fillOval(xHead, yHead, spielerKopfRadius, spielerKopfRadius);
        g2d.fillRoundRect(xBody, yBody, spielerKoerperBreite, spielerKoerperGroessse, spielerKoerperRundung, spielerKoerperRundung);
    }

    private void drawKrone(Graphics g, Position pos) {
        Color color = Color.YELLOW;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int xBody = pos.x * spacing + feldRadius/2 - spielerKoerperBreite/2;
        int yBody = pos.y * spacing + feldRadius/2 - spielerKoerperBreite/2;

        g2d.setColor(color);

        g2d.fillRoundRect(xBody, yBody, spielerKoerperBreite, spielerKoerperGroessse, spielerKoerperRundung, spielerKoerperRundung);
    }

    private void drawSperrstein(Graphics g, Position pos) {
        Color sperrsteinColor = Color.BLACK;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x1 = pos.x * spacing + sperrsteinEinrueckung;
        int y1 = pos.y * spacing + sperrsteinEinrueckung;

        int x2 = pos.x * spacing + feldRadius - sperrsteinEinrueckung;
        int y2 = pos.y * spacing + feldRadius - sperrsteinEinrueckung;

        g2d.setColor(sperrsteinColor);
        g2d.setStroke(new BasicStroke(8));

        g2d.drawLine(x1, y1, x2, y2);

        x1 = pos.x * spacing + feldRadius - sperrsteinEinrueckung;
        y1 = pos.y * spacing + sperrsteinEinrueckung;

        x2 = pos.x * spacing + sperrsteinEinrueckung;
        y2 = pos.y * spacing + feldRadius - sperrsteinEinrueckung;

        g2d.drawLine(x1, y1, x2, y2);
    }

    //spielfeld zeichnen
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges first
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(2));
        for (Feld feld : felder) {
            Position pos = feld.getPosition();
            int x1 = pos.x * spacing + feldRadius/2;
            int y1 = pos.y * spacing + feldRadius/2;

            for (Feld nachbar : feld.getNachbarn()) {
                Position nPos = nachbar.getPosition();
                int x2 = nPos.x * spacing + feldRadius/2;
                int y2 = nPos.y * spacing + feldRadius/2;
                g2d.drawLine(x1, y1, x2, y2);
            }
        }

        // Draw fields
        for (Feld feld : felder) {
            Position pos = feld.getPosition();
            int x = pos.x * spacing;
            int y = pos.y * spacing;

            // Field background
            if (feld.istSpielerSpawn()) {
                int feldInhaber = feld.getSpielerSpawnInhaberId();
                Color inhaberColor = FeldGUI.playerBackgroundColors[feldInhaber];

                g2d.setColor(inhaberColor);

            } else {
                g2d.setColor(new Color(220,220,220));
            }
            g2d.setStroke(new BasicStroke(2));

            g2d.fillOval(x, y, feldRadius, feldRadius);

            // Field border
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(x, y, feldRadius, feldRadius);

            // Field content
            if (feld.getBesetzung() != null) {
                String type = feld.getBesetzung().getType();

                if (type == "Spielstein") {
                    drawPlayer(g, feld.getPosition(), ((Spielstein)feld.getBesetzung()).getSpielerId());
                } else if (type == "Sperrstein") {
                    drawSperrstein(g, feld.getPosition());
                } else if (type == "Krone") {
                    drawKrone(g, feld.getPosition());
                }

                if (DEBUG_MODE) {
                    String content = feld.getBesetzung().toString();
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(content);
                    g2d.setColor(Color.BLUE);
                    g2d.drawString(content, x + feldRadius/2 - textWidth/2, y + feldRadius/2 + 30);
                }
            }

            // Field ID
            if (DEBUG_MODE) {
                g2d.setColor(Color.GRAY);
                g2d.drawString(String.valueOf(feld.getId()), x + feldRadius/2 - 3, y + feldRadius/2 + 3);
            }
        }
    }
}
