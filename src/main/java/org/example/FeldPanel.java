package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.*;
class FeldPanel extends JPanel {
    private Set<Feld> felder;
    private final int feldRadius = 40;      // Größe der Kreise
    private final int spacing = 70;        // Abstand zwischen Feldern

    public FeldPanel(Feld startFeld) {
        this.felder = collectFelder(startFeld);
        setPreferredSize(new Dimension(1000, 1000));
        setBackground(Color.WHITE);
    }

    public void repaintNewFields(Feld feld) {
        felder = collectFelder(feld);
        repaint();
    }

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
                g2d.setColor(new Color(144, 238, 144)); // Light green
            } else {
                g2d.setColor(Color.WHITE);
            }
            g2d.fillOval(x, y, feldRadius, feldRadius);

            // Field border
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, feldRadius, feldRadius);

            // Field ID
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString(String.valueOf(feld.getId()), x + feldRadius/2 - 3, y + feldRadius/2 + 3);

            // Field content
            if (feld.getBesetzung() != null) {
                String content = feld.getBesetzung().toString();
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(content);
                g2d.setColor(Color.BLUE);
                g2d.drawString(content, x + feldRadius/2 - textWidth/2, y + feldRadius/2 + 20);
            }
        }
    }
}
