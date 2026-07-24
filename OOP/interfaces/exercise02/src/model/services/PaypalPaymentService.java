package OOP.interfaces.exercise02.src.model.services;

public class PaypalPaymentService implements PaymentService{

    private static final double INTEREST_RATE = 0.01;
    private static final double TAX_PERCENTE = 0.02;

    @Override
    public Double getInterestValue(Double value, Integer mounth) {
        
        double newValue = value * (INTEREST_RATE * mounth);

        return newValue;
    }

    @Override
    public Double getTaxValue(Double value) {

        double newValue = value * TAX_PERCENTE;

        return newValue;
    }

}
