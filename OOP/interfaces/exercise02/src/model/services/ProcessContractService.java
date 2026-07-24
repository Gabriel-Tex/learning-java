package OOP.interfaces.exercise02.src.model.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import OOP.interfaces.exercise02.src.model.entities.Contract;
import OOP.interfaces.exercise02.src.model.entities.Installment;

public class ProcessContractService {
    private Integer numberOfInstallments;
    private Contract contract;

    PaymentService paymentService;

    public ProcessContractService(Integer numberOfInstallments, Contract contract, PaymentService paymentService) {
        this.numberOfInstallments = numberOfInstallments;
        this.contract = contract;
        this.paymentService = paymentService;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public void processContract() {
        double totalValue = contract.getTotalValue();
        double grossInstallment = totalValue / getNumberOfInstallments();

        double newInstallment;

        LocalDate dateInstallment = contract.getDate();

        for (int i = 0; i < getNumberOfInstallments(); ++i) {
            newInstallment = grossInstallment;
            // processando o valor das parcelas
            newInstallment = paymentService.getInterestValue(newInstallment, (i+1)) + newInstallment;
            newInstallment = paymentService.getTaxValue(newInstallment) + newInstallment;

            // processando a data das parcelas
            dateInstallment = dateInstallment.plusMonths(1);

            contract.getInstallments().add(new Installment(dateInstallment, newInstallment));
        }
    }

    public void printInstallment(){
        if(contract.getInstallments() != null){

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            IO.println("PARCELAS: ");
            for(Installment installment : contract.getInstallments()){
                
                IO.println(installment.getDueDate().format(fmt) + " - " + String.format("%.2f", installment.getValue()));
            }
        }
        else{
            IO.println("Contrato ainda não foi processado.");
        }
    }
}
