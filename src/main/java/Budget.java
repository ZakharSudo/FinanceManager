import java.io.Serializable;

public class Budget implements Serializable {
    private Category category;
    private double limit;
    
    public Budget(Category category, double limit) {
        this.category = category;
        this.limit = limit;
    }
    
    public Category getCategory() { return category; }
    public double getLimit() { return limit; }
    public void setLimit(double limit) { this.limit = limit; }
    
    @Override
    public String toString() {
        return String.format("Budget{category=%s, limit=%.2f}", 
                           category.getName(), limit);
    }
}