import java.sql.*;
import java.util.HashMap;

public class UserData {
    private String username;
    private String password;

    public Double debt;

    public boolean isAdmin() {
        return IsAdmin;
    }

    public void setAdmin(boolean admin) {
        IsAdmin = admin;
    }

    public boolean isManager() {
        return IsManager;
    }

    public void setManager(boolean manager) {
        IsManager = manager;
    }

    private boolean IsAdmin;
    private boolean IsManager;
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void decreaseBalance(Double balance) {
        this.balance -= balance;
    }

    public void increaseBalance(Double balance) {
        this.balance += balance;
    }
    private Double balance;
    public UserData (String username, String password, boolean IsAdmin, boolean IsManager, Double debt) {
        this.password = password;
        this.username = username;
        this.balance = 0.0;
        this.IsAdmin = IsAdmin;
        this.IsManager = IsManager;
        this.debt = debt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
