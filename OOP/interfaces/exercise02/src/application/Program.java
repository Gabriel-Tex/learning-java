package OOP.interfaces.exercise02.src.application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import OOP.interfaces.exercise02.src.model.entities.Contract;
import OOP.interfaces.exercise02.src.model.services.PaypalPaymentService;
import OOP.interfaces.exercise02.src.model.services.ProcessContractService;

public class Program {
    void main(){

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Scanner sc = new Scanner(System.in);

        IO.println("Entre os dados do contrato: ");

        IO.print("Número: ");
        String number = sc.nextLine();
        
        IO.print("Data (dd/MM/yyyy): ");
        String dateString = sc.nextLine();
        LocalDate date = LocalDate.parse(dateString, fmt);
        
        IO.print("Valor do contrato: ");
        double value = sc.nextDouble();

        Contract contract = new Contract(number, date, value);

        IO.print("Número de parcelas: ");
        int numberInstallments = sc.nextInt();

        ProcessContractService process = new ProcessContractService(numberInstallments, contract, new PaypalPaymentService());

        process.processContract();

        contract.printInstallments();

        sc.close();
    }
}
