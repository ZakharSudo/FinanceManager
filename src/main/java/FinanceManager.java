import java.io.*;
import java.util.*;
import java.text.NumberFormat;

public class FinanceManager {
    private Map<String, User> users;
    private User currentUser;
    private Scanner scanner;
    private NumberFormat numberFormat;
    
    public FinanceManager() {
        this.users = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.numberFormat = NumberFormat.getInstance();
        this.numberFormat.setMinimumFractionDigits(1);
        this.numberFormat.setMaximumFractionDigits(2);
        this.numberFormat.setGroupingUsed(true);
    }
    
    public static void main(String[] args) {
        FinanceManager manager = new FinanceManager();
        manager.run();
    }
    
    public void run() {
        System.out.println("=== Система управления личными финансами ===");
        
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }
    
    private void showAuthMenu() {
        System.out.println("\n=== Меню авторизации ===");
        System.out.println("1. Вход");
        System.out.println("2. Регистрация");
        System.out.println("3. Выход из приложения");
        System.out.print("Выберите действие: ");
        
        String choice = getInput();
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                exitApplication();
                break;
            default:
                System.out.println("❌ Неверный выбор! Пожалуйста, выберите 1, 2 или 3.");
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. 💰 Добавить доход");
        System.out.println("2. 💸 Добавить расход");
        System.out.println("3. 📊 Установить бюджет");
        System.out.println("4. 📈 Показать полный отчет");
        System.out.println("5. 🔍 Подсчет по категориям");
        System.out.println("6. 👥 Перевод другому пользователю");
        System.out.println("7. 📋 Показать историю операций");
        System.out.println("8. 🚪 Выход из аккаунта");
        System.out.print("Выберите действие: ");
        
        String choice = getInput();
        switch (choice) {
            case "1":
                addIncome();
                break;
            case "2":
                addExpense();
                break;
            case "3":
                setBudget();
                break;
            case "4":
                showReport();
                break;
            case "5":
                calculateByCategories();
                break;
            case "6":
                transferMoney();
                break;
            case "7":
                showOperationHistory();
                break;
            case "8":
                logout();
                break;
            default:
                System.out.println("❌ Неверный выбор! Пожалуйста, выберите число от 1 до 8.");
        }
    }
    
    private String getInput() {
        return scanner.nextLine().trim();
    }
    
    private void login() {
        System.out.print("Введите логин: ");
        String login = getInput();
        
        if (login.isEmpty()) {
            System.out.println("❌ Ошибка: логин не может быть пустым!");
            return;
        }
        
        System.out.print("Введите пароль: ");
        String password = getInput();
        
        if (password.isEmpty()) {
            System.out.println("❌ Ошибка: пароль не может быть пустым!");
            return;
        }
        
        if (users.containsKey(login) && users.get(login).validatePassword(password)) {
            currentUser = users.get(login);
            loadUserData();
            System.out.println("✅ Успешный вход! Добро пожаловать, " + login + "!");
        }else {
            System.out.println("❌ Неверный логин или пароль!");
        }
    }
    
    private void register() {
        System.out.print("Введите логин: ");
        String login = getInput();
        
        if (login.isEmpty()) {
            System.out.println("❌ Ошибка: логин не может быть пустым!");
            return;
        }
        
        if (login.length() < 3) {
            System.out.println("❌ Ошибка: логин должен содержать минимум 3 символа!");
            return;
        }
        
        if (users.containsKey(login)) {
            System.out.println("❌ Пользователь с таким логином уже существует!");
            return;
        }
        
        System.out.print("Введите пароль: ");
        String password = getInput();
        
        if (password.isEmpty()) {
            System.out.println("❌ Ошибка: пароль не может быть пустым!");
            return;
        }
        
        if (password.length() < 4) {
            System.out.println("❌ Ошибка: пароль должен содержать минимум 4 символа!");
            return;
        }
        
        User newUser = new User(login, password);
        users.put(login, newUser);
        System.out.println("✅ Регистрация успешна! Теперь вы можете войти в систему.");
    }
    
    private void addIncome() {
        try {
            System.out.print("Введите категорию дохода: ");
            String categoryName = getInput();
            
            if (categoryName.isEmpty()) {
                System.out.println("❌ Ошибка: название категории не может быть пустым!");
                return;
            }
            
            System.out.print("Введите сумму дохода: ");
            String amountInput = getInput();
            
            double amount = validateAmount(amountInput, "дохода");
            if (amount < 0) return;
            
            Category category = new Category(categoryName, OperationType.INCOME);
            Operation operation = new Operation(category, amount);
            currentUser.getWallet().addOperation(operation);
            
            System.out.println("✅ Доход успешно добавлен!");
            checkFinancialStatus();
        } catch (Exception e) {
            System.out.println("❌ Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }
    
    private void addExpense() {
        try {
            System.out.print("Введите категорию расхода: ");
            String categoryName = getInput();
            
            if (categoryName.isEmpty()) {
                System.out.println("❌ Ошибка: название категории не может быть пустым!");
                return;
            }
            
            System.out.print("Введите сумму расхода: ");
            String amountInput = getInput();
            
            double amount = validateAmount(amountInput, "расхода");
            if (amount < 0) return;
            
            if (amount > currentUser.getWallet().getBalance()) {
                System.out.println("⚠️ Предупреждение: сумма расхода превышает текущий баланс!");
                System.out.print("Продолжить? (да/нет): ");
                String confirmation = getInput().toLowerCase();
                if (!confirmation.equals("да") && !confirmation.equals("yes")) {
                    System.out.println("Операция отменена.");
                    return;
                }
            }
            
            Category category = new Category(categoryName, OperationType.EXPENSE);
            Operation operation = new Operation(category, amount);
            currentUser.getWallet().addOperation(operation);
            
            System.out.println("✅ Расход успешно добавлен!");
            checkBudgetExceedance(category, amount);
            checkFinancialStatus();
        } catch (Exception e) {
            System.out.println("❌ Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }
    
    private void setBudget() {
        try {
            System.out.print("Введите категорию расходов для бюджета: ");
            String categoryName = getInput();
            
            if (categoryName.isEmpty()) {
                System.out.println("❌ Ошибка: название категории не может быть пустым!");
                return;
            }
            
            System.out.print("Введите лимит бюджета: ");
            String limitInput = getInput();
            
            double limit = validateAmount(limitInput, "бюджета");
            if (limit < 0) return;
            
            Category category = new Category(categoryName, OperationType.EXPENSE);
            Budget budget = new Budget(category, limit);
            currentUser.getWallet().setBudget(budget);
            
            System.out.println("✅ Бюджет успешно установлен!");
        } catch (Exception e) {
            System.out.println("❌ Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }
    
    private void showReport() {
        Wallet wallet = currentUser.getWallet();
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 ФИНАНСОВЫЙ ОТЧЕТ");
        System.out.println("=".repeat(50));
        
        System.out.println("💰 Общий доход: " + numberFormat.format(wallet.getTotalIncome()));
        
        Map<Category, Double> incomeByCategory = wallet.getIncomeByCategory();
        if (!incomeByCategory.isEmpty()) {
            System.out.println("\n📈 Доходы по категориям:");
            incomeByCategory.forEach((category, amount) -> 
                System.out.println("   • " + category.getName() + ": " + numberFormat.format(amount)));
        } else {
            System.out.println("\n📈 Доходы по категориям: нет данных");
        }
        
        System.out.println("\n💸 Общие расходы: " + numberFormat.format(wallet.getTotalExpenses()));
        
        Map<Category, Double> expensesByCategory = wallet.getExpensesByCategory();
        if (!expensesByCategory.isEmpty()) {
            System.out.println("\n📉 Расходы по категориям:");
            expensesByCategory.forEach((category, amount) -> 
                System.out.println("   • " + category.getName() + ": " + numberFormat.format(amount)));
        } else {
            System.out.println("\n📉 Расходы по категориям: нет данных");
        }
        
        System.out.println("\n🎯 Бюджет по категориям:");
        Map<Category, Budget> budgets = wallet.getBudgets();
        Map<Category, Double> categoryExpenses = wallet.getExpensesByCategory();
        
        if (budgets.isEmpty()) {
            System.out.println("   Бюджеты не установлены");
        } else {
            budgets.forEach((category, budget) -> {
                double spent = categoryExpenses.getOrDefault(category, 0.0);
                double remaining = budget.getLimit() - spent;
                String status = remaining >= 0 ? "✅" : "❌";
                
                System.out.printf("   %s %s: %s, Оставшийся бюджет: %s%n",
                    status,
                    category.getName(),
                    numberFormat.format(budget.getLimit()),
                    numberFormat.format(remaining));
            });
        }
        
        System.out.println("\n💳 Текущий баланс: " + numberFormat.format(wallet.getBalance()));
        System.out.println("=".repeat(50));
    }
    
    private void calculateByCategories() {
        try {
            System.out.println("\n=== Подсчет по категориям ===");
            System.out.println("1. Подсчет доходов по категориям");
            System.out.println("2. Подсчет расходов по категориям");
            System.out.print("Выберите тип операций: ");
            
            String typeChoice = getInput();
            OperationType type;
            
            if (typeChoice.equals("1")) {
                type = OperationType.INCOME;
            } else if (typeChoice.equals("2")) {
                type = OperationType.EXPENSE;
            } else {
                System.out.println("❌ Неверный выбор!");return;
            }
            
            System.out.print("Введите названия категорий через запятую: ");
            String categoriesInput = getInput();
            
            if (categoriesInput.isEmpty()) {
                System.out.println("❌ Ошибка: не введены категории!");
                return;
            }
            
            List<String> categoriesList = Arrays.stream(categoriesInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
            if (categoriesList.isEmpty()) {
                System.out.println("❌ Ошибка: не введены корректные названия категорий!");
                return;
            }
            
            Map<String, Double> result = currentUser.getWallet().calculateByCategories(categoriesList, type);
            Set<String> unknownCategories = currentUser.getWallet().getUnknownCategories(categoriesList, type);
            
            if (!unknownCategories.isEmpty()) {
                System.out.println("⚠️ Предупреждение: следующие категории не найдены: " + unknownCategories);
            }
            
            if (result.isEmpty()) {
                System.out.println("По выбранным категориям операций не найдено.");
            } else {
                System.out.println("\n📊 Результаты подсчета:");
                result.forEach((category, amount) -> 
                    System.out.println("   • " + category + ": " + numberFormat.format(amount)));
                
                double total = result.values().stream().mapToDouble(Double::doubleValue).sum();
                System.out.println("   Итого: " + numberFormat.format(total));
            }
        } catch (Exception e) {
            System.out.println("❌ Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }
    
    private void showOperationHistory() {
        Wallet wallet = currentUser.getWallet();
        List<Operation> operations = wallet.getOperations();
        
        if (operations.isEmpty()) {
            System.out.println("История операций пуста.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 ИСТОРИЯ ОПЕРАЦИЙ");
        System.out.println("=".repeat(60));
        
        operations.forEach(op -> {
            String type = op.getCategory().getType() == OperationType.INCOME ? "💰 Доход" : "💸 Расход";
            System.out.printf("   %s: %s - %s%n", 
                type, 
                numberFormat.format(op.getAmount()),
                op.getCategory().getName());
        });
        
        System.out.println("=".repeat(60));
    }
    
    private void transferMoney() {
        try {
            System.out.print("Введите логин получателя: ");
            String recipientLogin = getInput();
            
            if (recipientLogin.isEmpty()) {
                System.out.println("❌ Ошибка: логин получателя не может быть пустым!");
                return;
            }
            
            if (recipientLogin.equals(currentUser.getLogin())) {
                System.out.println("❌ Ошибка: нельзя переводить самому себе!");
                return;
            }
            
            if (!users.containsKey(recipientLogin)) {
                System.out.println("❌ Пользователь с таким логином не найден!");
                return;
            }
            
            System.out.print("Введите сумму перевода: ");
            String amountInput = getInput();
            
            double amount = validateAmount(amountInput, "перевода");
            if (amount < 0) return;
            
            if (currentUser.getWallet().getBalance() < amount) {
                System.out.println("❌ Ошибка: недостаточно средств для перевода!");
                return;
            }
            
            // Подтверждение перевода
            System.out.print("Вы уверены, что хотите перевести " + numberFormat.format(amount) + 
                    " пользователю " + recipientLogin + "? (да/нет): ");
     String confirmation = getInput().toLowerCase();
     if (!confirmation.equals("да") && !confirmation.equals("yes")) {
         System.out.println("Перевод отменен.");
         return;
     }
     
     // Создаем операцию расхода у отправителя
     Category transferExpenseCategory = new Category("Перевод пользователю " + recipientLogin, OperationType.EXPENSE);
     Operation expenseOperation = new Operation(transferExpenseCategory, amount);
     currentUser.getWallet().addOperation(expenseOperation);
     
     // Создаем операцию дохода у получателя
     Category transferIncomeCategory = new Category("Перевод от пользователя " + currentUser.getLogin(), OperationType.INCOME);
     Operation incomeOperation = new Operation(transferIncomeCategory, amount);
     users.get(recipientLogin).getWallet().addOperation(incomeOperation);
     
     // Сохраняем данные получателя
     saveUserData(users.get(recipientLogin));
     
     System.out.println("✅ Перевод успешно выполнен!");
 } catch (Exception e) {
     System.out.println("❌ Произошла непредвиденная ошибка при переводе: " + e.getMessage());
 }
}

private double validateAmount(String amountInput, String operationType) {
 if (amountInput.isEmpty()) {
     System.out.println("❌ Ошибка: сумма " + operationType + " не может быть пустой!");
     return -1;
 }
 
 double amount;
 try {
     amount = Double.parseDouble(amountInput);
 } catch (NumberFormatException e) {
     System.out.println("❌ Ошибка: введите корректное число для суммы!");
     return -1;
 }
 
 if (amount <= 0) {
     System.out.println("❌ Ошибка: сумма должна быть положительной!");
     return -1;
 }
 
 if (amount > 1_000_000_000) {
     System.out.println("❌ Ошибка: сумма слишком большая!");
     return -1;
 }
 
 return amount;
}

private void checkBudgetExceedance(Category category, double newExpense) {
 Budget budget = currentUser.getWallet().getBudgets().get(category);
 if (budget != null) {
     double currentSpent = currentUser.getWallet().getExpensesByCategory()
         .getOrDefault(category, 0.0);
     
     if (currentSpent > budget.getLimit()) {
         System.out.println("⚠️ Внимание: превышен бюджет для категории '" + 
             category.getName() + "'!");
     }
 }
}

private void checkFinancialStatus() {
 Wallet wallet = currentUser.getWallet();
 if (wallet.getTotalExpenses() > wallet.getTotalIncome()) {
     System.out.println("⚠️ Внимание: расходы превышают доходы!");
 }
 
 if (wallet.getBalance() < 0) {
     System.out.println("🚨 Критическое предупреждение: отрицательный баланс!");
 }
}

private void logout() {
 saveUserData();
 currentUser = null;
 System.out.println("✅ Вы вышли из аккаунта.");
}

private void exitApplication() {
 if (currentUser != null) {
     saveUserData();
 }
 System.out.println("До свидания! Спасибо за использование нашего приложения!");
 System.exit(0);
}

private void loadUserData() {
 try {
     File file = new File(currentUser.getLogin() + ".dat");
     if (file.exists()) {
         ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
         User savedUser = (User) ois.readObject();
         currentUser.getWallet().getOperations().clear();
         currentUser.getWallet().getOperations().addAll(savedUser.getWallet().getOperations());
         currentUser.getWallet().getBudgets().putAll(savedUser.getWallet().getBudgets());
         ois.close();
         System.out.println("✅ Данные пользователя загружены.");
     }
 } catch (IOException | ClassNotFoundException e) {
     System.out.println("⚠️ Ошибка при загрузке данных: " + e.getMessage());
 }
}

private void saveUserData() {
 if (currentUser != null) {
     saveUserData(currentUser);
 }
}

private void saveUserData(User user) {
 try {
     File file = new File(user.getLogin() + ".dat");
     ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
     oos.writeObject(user);
     oos.close();
 } catch (IOException e) {
     System.out.println("⚠️ Ошибка при сохранении данных: " + e.getMessage());
 }
}
}