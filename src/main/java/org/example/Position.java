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

    @Override
    public String toString() {
        return "Position [x=" + x + ", y=" + y + "]";
    }

}
