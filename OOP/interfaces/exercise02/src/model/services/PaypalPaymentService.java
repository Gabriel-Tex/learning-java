package OOP.interfaces.exercise02.src.model.services;

public class PaypalPaymentService implements PaymentService{

    private Double interestRate = 0.01;
    private Double tax = 0.02;

    public Double getInterestValue(Double value, Integer mounth) {
        
        double newValue = value * (interestRate * mounth);

        return newValue;
    }

    public Double getTaxValue(Double value) {

        double newValue = value * tax;

        return newValue;
    }

}
