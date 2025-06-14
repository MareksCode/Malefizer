package org.example;

import java.io.Serializable;
import java.util.Random;
public class Wuerfel implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int MAX = 5;
    private Random rand = new Random();

    public int Roll() {
        return 1 + rand.nextInt(MAX+1);
    }
}
