import java.util.Random;
public class Wuerfel {
    private final int MAX = 6;
    private final int MIN = 1;
    private Random rand = new Random();

    public int Roll() {
        return 1 + rand.nextInt(MAX+1);
    }
}
