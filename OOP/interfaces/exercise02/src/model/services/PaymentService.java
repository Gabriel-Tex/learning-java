package OOP.interfaces.exercise02.src.model.services;

public interface PaymentService {
    public Double getInterestValue(Double value, Integer mounth);
    public Double getTaxValue(Double value);
}
