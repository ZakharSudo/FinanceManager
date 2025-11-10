import java.io.Serializable;
import java.util.Objects;

public class Category implements Serializable {
    private String name;
    private OperationType type;
    
    public Category(String name, OperationType type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() { return name; }
    public OperationType getType() { return type; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name) && type == category.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
    
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}