package fileManipulation;

import java.io.File;
import java.util.Scanner;

public class FileManipulation {
    void main() {

        Scanner sc = new Scanner(System.in);
        String strPath = sc.nextLine();

        File path = new File(strPath);

        // listando subpastas
        File[] folders = path.listFiles(File::isDirectory);

        IO.println("");

        IO.println("FOLDERS:");
        if (folders != null) {
            for (File folder : folders) {
                IO.println(folder);
            }
        } else {
            IO.println("Caminho inválido ou não é um diretório.");
        }

        IO.println("");

        // listando arquivos
        File[] files = path.listFiles(File::isFile);

        IO.println("FILES:");
        if (files != null) {
            for (File file : files) {
                IO.println(file);
            }
        } else {
            IO.println("Caminho inválido ou não é um diretório.");
        }

        IO.println("");

        // criando pasta
        boolean success = new File(strPath + "/folderCreated").mkdir();
        IO.println("Directory created successfully: " + success);

        IO.println("");

        sc.close();
    }
}
