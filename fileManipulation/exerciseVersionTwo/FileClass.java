package fileManipulation.exerciseVersionTwo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileClass {
    private String SourceFilePath;
    private String OutFilePath;
    private List<Product> products = new ArrayList<Product>();

    // constructors
    public FileClass(String sourceFilePath, String outFilePath) {
        SourceFilePath = sourceFilePath;
        OutFilePath = outFilePath;
    }

    // getters e setters
    public String getSourceFilePath() {
        return SourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        SourceFilePath = sourceFilePath;
    }

    public String getOutFilePath() {
        return OutFilePath;
    }

    public void setOutFilePath(String outFilePath) {
        OutFilePath = outFilePath;
    }

    // methods

    // criando pasta out
    public void createOutFolder(){
        File filePath = new File(getOutFilePath());
        String folderPath = filePath.getParent();

        boolean success = new File(folderPath).mkdir();
        IO.println("Pasta criada com sucesso: " + success);
    }

    // leitura do arquivo de entrada e instanciação dos produtos
    public void ReadingFile() {

        try (BufferedReader br = new BufferedReader(new FileReader(getSourceFilePath()))) {

            String line = br.readLine();
            while (line != null) {

                this.products.add(new Product(line.split(",")[0], Double.valueOf(line.split(",")[1]),
                        Integer.valueOf(line.split(",")[2])));
                line = br.readLine();

            }

            IO.println("Arquivo lido com sucesso!");

        } catch (IOException e) {
            IO.println("Error: " + e.getMessage());
        }
    }

    // escrita do arquivo de saída
    public void WritingFile() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getOutFilePath()))) {

            for(Product product : this.products){
                bw.write(product.toString());
                bw.newLine();
            }

            IO.println("Arquivo escrito com sucesso!");
            
        } catch (IOException e) {
            IO.println("Error: " + e.getMessage());
        }
    }

}
