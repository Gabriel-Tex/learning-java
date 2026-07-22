package fileManipulation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class ReadingFilesWithFileClass {
    void main() {
        // instanciar classe File para abstrair o arquivo a ser lido
        // realizar a leitura do arquivo por meio da classe Scanner
        // tratar IO exception
        // fechar a classe scanner
        
        String path = Paths.get(System.getProperty("user.dir"), "assets", "in.txt").toString();

        File file = new File(path);
        Scanner sc = null;

        try {
            sc = new Scanner(file);
            while (sc.hasNextLine()) {
                IO.println(sc.nextLine());
            }
        } catch (IOException e) {
            IO.println("Error: " + e.getMessage());
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }
}
