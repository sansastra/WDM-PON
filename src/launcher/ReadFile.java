package launcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Sandeep on 10-Jul-17.
 */
public class ReadFile {
    private static Scanner input;

    /**
     * Constructor class
     *
     * @param filename file name
     */
    public ReadFile(String filename) {

        String path = ReadFile.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(path + "/../resources/" + filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        input = new Scanner(stream);
    }

    /**
     * Read next line
     *
     * @return string line
     */
    public static String readLine() {

        if (input.hasNext())
            return input.nextLine();
        else
            return null;
    }
}
