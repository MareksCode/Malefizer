package org.example;

import java.io.Serializable;

// simple position klasse, hier wurden keine getter und setter verwendet für leichteres arbeiten..
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x;
    public int y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static double getDistanz(Position p1, Position p2) { //taktischer pythagoras 😎
        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;

        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    @Override
    public String toString() {
        return "Position [x=" + x + ", y=" + y + "]";
    }
}
