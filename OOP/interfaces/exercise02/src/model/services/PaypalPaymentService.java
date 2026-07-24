package OOP.interfaces.exercise02.src.model.services;

public class PaypalPaymentService implements PaymentService{

    private Double interest = 0.01;
    private Double tax = 0.02;

    public Double getInterest() {
        return interest;
    }
    public Double getTax() {
        return tax;
    }

}
