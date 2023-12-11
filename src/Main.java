import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        Database.createTable();
        Database.createTableTransaction();
        Database.insertInitialDataUser();

        Application.launch(UserRegistrationApp.class, args);
    }
}
