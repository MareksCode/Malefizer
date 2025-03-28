import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class getFileContent {
    private static String readFile() throws FileNotFoundException {
        String everything = "null";
        Scanner in = new Scanner(new FileReader("file.txt"));
        StringBuilder sb = new StringBuilder();
        while(in.hasNext()){
            sb.append(in.nextLine());
            sb.append("\n");
        }
        in.close();
        everything = sb.toString();
        return everything;

    }

    public static char[][] getFileAsArray() throws FileNotFoundException {
        String fileRead = readFile();
        String[] arrayZs1;
        arrayZs1 = fileRead.split("\n");
        char[][] ausgabe = new char[arrayZs1.length][arrayZs1[0].length()];

        for (int i = 0; i < arrayZs1.length; i++) {
            char[] zs = arrayZs1[i].toCharArray();
            System.arraycopy(zs, 0, ausgabe[i], 0, zs.length);
        }
        return ausgabe;
    }

}
