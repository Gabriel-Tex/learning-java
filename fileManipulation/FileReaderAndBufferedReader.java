package fileManipulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class FileReaderAndBufferedReader {
    void main() {
        String path = Paths.get(System.getProperty("user.dir"), "assets", "in.txt").toString();

        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);

            String line = br.readLine();

            while (line != null) {
                IO.println(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            IO.println("Error: " + e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.getStackTrace();
            }

        }
    }

}
