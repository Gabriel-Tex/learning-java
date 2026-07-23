package fileManipulation.exerciseVersionTwo;

import java.util.Scanner;

public class Program {
    void main(){

        IO.print("Entre o caminho do arquivo .csv de entrada: ");
        Scanner sc = new Scanner(System.in);
        
        String sourceFilePath = sc.nextLine();
        String outFilePath = "./fileManipulation/exerciseVersionTwo/out/summary.csv";

        FileClass fileClass = new FileClass(sourceFilePath, outFilePath);

        fileClass.createOutFolder();

        fileClass.ReadingFile();

        fileClass.WritingFile();

        sc.close();
    }
}
