package fileManipulation.exerciseVersionOne;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Program {
    void main() {

        /*
         * Passos:
         * - Ler o arquivo source com buffered reader
         * - Separar cada atributo com split
         * - Calcular valor total
         * - Escrever nome dos produtos e seus valores totais com buffered writer em
         * out/summary.csv
         */

        // caminho do arquivo de entrada
        File pathIn = new File("./fileManipulation/exerciseVersionOne/in/source.csv");
        // caminho do arquivo de saída
        File pathOut = new File("./fileManipulation/exerciseVersionOne/out/summary.csv");

        // leitura do arquivo de entrada
        try (BufferedReader br = new BufferedReader(new FileReader(pathIn))) {

            String line = br.readLine();

            // vetor de linhas do arquivo de saída
            ArrayList<String> linesOut = new ArrayList<String>();

            while (line != null) {

                String[] parts = line.split(",");
                String item = parts[0];
                float value = Float.parseFloat(parts[1]);
                int quantity = Integer.parseInt(parts[2]);

                // cálculo do valor total
                float totalValue = value * quantity;

                // salvando linha (string) do arquivo de saída no vetor linesOut
                linesOut.add(item + "," + String.valueOf(totalValue));

                // lendo próxima linha
                line = br.readLine();
            }

            // escrita no arquivo de saída
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(pathOut))) {

                for (String lineOut : linesOut) {
                    bw.write(lineOut);
                    bw.newLine();
                }

                IO.println("Leitura e escrita realizadas com sucesso!");

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
