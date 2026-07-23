package fileManipulation;

import java.io.File;
import java.util.Scanner;

public class PathInfos{
    void main(){

        Scanner sc = new Scanner(System.in);

        IO.println("===========================");
        IO.print("Enter a folder path: ");

        String strPath = sc.nextLine();

        File path = new File(strPath);

        IO.println("");
        IO.println("getName: " + path.getName());
        IO.println("getParent: " + path.getParent());
        IO.println("getPath: " + path.getPath());
        IO.println("===========================");

        sc.close();
    }
}