package OOP.interfaces.exercise02.src.model.services;

import java.time.LocalDate;

import OOP.interfaces.exercise02.src.model.entities.Contract;

public class ProcessContractService {
    private Integer numberInstallments;
    private Contract contract;

    PaymentService paymentService;

    public ProcessContractService(Integer numberInstallments, Contract contract, PaymentService paymentService) {
        this.numberInstallments = numberInstallments;
        this.contract = contract;
        this.paymentService = paymentService;
    }

    public Integer getNumberInstallments() {
        return numberInstallments;
    }

    public void setNumberInstallments(Integer numberInstallments) {
        this.numberInstallments = numberInstallments;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public void processContract() {
        // pegar o valor da primeira parcela sem juros e taxa
        /*
         * loop -> i:
         * parcela = parcela * (juros * (i+1)) + parcela
         * parcela = (parcela * taxa) + parcela
         * 
         * guardar parcela em uma lista
         */
        double totalValue = contract.getTotalValue();
        double grossInstallment = totalValue / getNumberInstallments();

        double newInstallment;

        LocalDate dateInstallment = contract.getDate();

        for (int i = 0; i < getNumberInstallments(); ++i) {
            newInstallment = grossInstallment;
            // processando o valor das parcelas
            newInstallment = newInstallment * (paymentService.getInterest() * (i + 1)) + newInstallment;
            newInstallment = (newInstallment * paymentService.getTax()) + newInstallment;

            contract.getInstallments().add(newInstallment);

            // processando a data das parcelas
            dateInstallment = dateInstallment.plusMonths(1);
            contract.getInstallmentsDates().add(dateInstallment);
        }
    }
}
