package OOP.interfaces.exercise.model.services;

import java.time.Duration;

import OOP.interfaces.exercise.model.entities.CarRental;
import OOP.interfaces.exercise.model.entities.Invoice;

public class RentalService {
    private Double valuePerHour;
    private Double valuePerDay;

    private TaxService taxService;
    
    public RentalService(Double valuePerHour, Double valuePerDay, TaxService tax) {
        this.valuePerHour = valuePerHour;
        this.valuePerDay = valuePerDay;
        this.taxService = tax;
    }

    public void processInvoice(CarRental cartRental){
        
        double durationMinutes = Duration.between(cartRental.getStart(), cartRental.getFinish()).toMinutes();
        double durationHours = durationMinutes / 60.0;
        double durationDays = durationHours / 24;

        Double basicPayment;

        if(durationHours > 12.0){
            basicPayment = Math.ceil(durationDays) * valuePerDay;
        }
        else{
            basicPayment = Math.ceil(durationHours) * valuePerHour;
        }

        double tax = taxService.tax(basicPayment);

        cartRental.setInvoice(new Invoice(basicPayment, tax));
    }
    
}
