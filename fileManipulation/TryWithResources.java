package fileManipulation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class TryWithResources {
    void main() {
        String path = Paths.get(System.getProperty("user.dir"), "assets", "in.txt").toString();

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line = br.readLine();

            while (line != null) {
                IO.println(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            IO.println("Error: " + e.getMessage());
        }
    }
}
