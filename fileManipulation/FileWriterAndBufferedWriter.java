package fileManipulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class FileWriterAndBufferedWriter {
    void main() {

        String path = Paths.get(System.getProperty("user.dir"), "assets", "out.txt").toString();

        String lines[] = new String[] {"Good morning", "Good Afternoon", "Good night"};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            
            for(String line : lines){
                bw.write(line);
                bw.newLine();
            }

            IO.println("Linhas escritas com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
