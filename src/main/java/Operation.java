import java.io.Serializable;
import java.util.Date;

public class Operation implements Serializable {
    private Category category;
    private double amount;
    private Date date;
    
    public Operation(Category category, double amount) {
        this.category = category;
        this.amount = amount;
        this.date = new Date();
    }
    
    public Category getCategory() { return category; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    
    @Override
    public String toString() {
        return String.format("Operation{category=%s, amount=%.2f, date=%s}", 
                           category.getName(), amount, date);
    }
}