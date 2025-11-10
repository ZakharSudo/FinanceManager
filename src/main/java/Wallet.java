import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Wallet implements Serializable {
    private List<Operation> operations;
    private Map<Category, Budget> budgets;
    private double balance;
    
    public Wallet() {
        this.operations = new ArrayList<>();
        this.budgets = new HashMap<>();
        this.balance = 0.0;
    }
    
    public void addOperation(Operation operation) {
        operations.add(operation);
        if (operation.getCategory().getType() == OperationType.INCOME) {
            balance += operation.getAmount();
        } else {
            balance -= operation.getAmount();
        }
    }
    
    public void setBudget(Budget budget) {
        budgets.put(budget.getCategory(), budget);
    }
    
    public List<Operation> getOperations() { return new ArrayList<>(operations); }
    public Map<Category, Budget> getBudgets() { return new HashMap<>(budgets); }
    public double getBalance() { return balance; }
    
    public double getTotalIncome() {
        return operations.stream()
                .filter(op -> op.getCategory().getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .sum();
    }
    
    public double getTotalExpenses() {
        return operations.stream()
                .filter(op -> op.getCategory().getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .sum();
    }
    
    public Map<Category, Double> getIncomeByCategory() {
        return operations.stream()
                .filter(op -> op.getCategory().getType() == OperationType.INCOME)
                .collect(Collectors.groupingBy(
                    Operation::getCategory,
                    Collectors.summingDouble(Operation::getAmount)
                ));
    }
    
    public Map<Category, Double> getExpensesByCategory() {
        return operations.stream()
                .filter(op -> op.getCategory().getType() == OperationType.EXPENSE)
                .collect(Collectors.groupingBy(
                    Operation::getCategory,
                    Collectors.summingDouble(Operation::getAmount)
                ));
    }
    
    public Map<String, Double> calculateByCategories(List<String> categoryNames, OperationType type) {
        Map<String, Double> result = new HashMap<>();
        Set<String> foundCategories = new HashSet<>();
        Set<String> unknownCategories = new HashSet<>(categoryNames);
        
        for (Operation op : operations) {
            if (op.getCategory().getType() == type) {
                String currentCategoryName = op.getCategory().getName();
                if (categoryNames.contains(currentCategoryName)) {
                    result.merge(currentCategoryName, op.getAmount(), Double::sum);
                    foundCategories.add(currentCategoryName);
                    unknownCategories.remove(currentCategoryName);
                }
            }
        }
        
        // Добавляем нулевые значения для найденных категорий, которые не имеют операций
        for (String categoryName : categoryNames) {
            if (foundCategories.contains(categoryName) && !result.containsKey(categoryName)) {
                result.put(categoryName, 0.0);
            }
        }
        
        return result;
    }
    
    public Set<String> getUnknownCategories(List<String> categoryNames, OperationType type) {
        Set<String> unknownCategories = new HashSet<>(categoryNames);
        
        for (Operation op : operations) {
            if (op.getCategory().getType() == type) {
                String currentCategoryName = op.getCategory().getName();
                unknownCategories.remove(currentCategoryName);
            }
        }
        
        return unknownCategories;
    }
    
    public List<Operation> getOperationsByCategory(String categoryName) {
        return operations.stream()
                .filter(op -> op.getCategory().getName().equals(categoryName))
                .collect(Collectors.toList());
    }
}