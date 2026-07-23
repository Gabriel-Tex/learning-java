package fileManipulation.exerciseVersionTwo;

public class Product {
    private String item;
    private Double value;
    private Integer quantity;
    
    public Product(String item, Double value, Integer quantity) {
        this.item = item;
        this.value = value;
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double totalValue(){
        return this.value * this.quantity;
    }

    @Override
    public String toString() {
        return this.item + "," + String.format("%.2f", this.totalValue());
    }
    
}
