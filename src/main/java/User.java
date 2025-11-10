import java.io.Serializable;

public class User implements Serializable {
    private String login;
    private String password;
    private Wallet wallet;
    
    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.wallet = new Wallet();
    }
    
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public Wallet getWallet() { return wallet; }
    
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
    
    @Override
    public String toString() {
        return "User{login='" + login + "', walletBalance=" + wallet.getBalance() + "}";
    }
}