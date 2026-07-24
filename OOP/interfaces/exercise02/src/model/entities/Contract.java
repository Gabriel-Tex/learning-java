package OOP.interfaces.exercise02.src.model.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Contract {
    private String number;
    private LocalDate date;
    private Double totalValue;

    private List<Double> installments;
    private List<LocalDate> installmentsDates;

    public Contract(String number, LocalDate date, Double totalValue) {
        this.number = number;
        this.date = date;
        this.totalValue = totalValue;
        this.installments = new ArrayList<Double>();
        this.installmentsDates = new ArrayList<LocalDate>();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public List<Double> getInstallments() {
        return installments;
    }

    public List<LocalDate> getInstallmentsDates() {
        return installmentsDates;
    }

    public void printInstallments() {
        if (installments != null && installmentsDates != null) {

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            IO.println("PARCELAS: ");

            for (int i = 0; i < installments.size(); ++i) {
                IO.println(installmentsDates.get(i).format(fmt) + " - " + String.format("%.2f", installments.get(i)));
            }
        } else {
            IO.println("Parcelas não processadas.");
        }
    }
}
