import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

public class UserLoginApp extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label loginStatusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Войти в Аккаунт");

        Label titleLabel = new Label("Логин");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        Label usernameLabel = new Label("Пользователь:");
        usernameField = new TextField();

        Label passwordLabel = new Label("Пароль:");
        passwordField = new PasswordField();

        loginStatusLabel = new Label();
        loginStatusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12;");

        Button loginButton = new Button("Войти");
        loginButton.setStyle("-fx-background-color: #d4523e ; -fx-text-fill: white;");
        loginButton.setOnAction(e -> handleLoginButtonClick(primaryStage));

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(titleLabel, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, loginStatusLabel);

        Scene scene = new Scene(layout, 600, 480);

        primaryStage.getIcons().add(new Image("./kspi.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLoginButtonClick(Stage primaryStage) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (Database.userExists(username)) {
            UserData userData = Database.getUserByUsername(username);

            if (userData.getPassword().equals(password)) {
                loginStatusLabel.setStyle("-fx-text-fill: green;");
                loginStatusLabel.setText("Зашли в аккаунт");

                openMainApplication(primaryStage, username);
            } else {
                loginStatusLabel.setStyle("-fx-text-fill: red;");
                loginStatusLabel.setText("Неправильный пароль.");
            }
        } else {
            loginStatusLabel.setStyle("-fx-text-fill: red;");
            loginStatusLabel.setText("Пользователь не найден.");
        }
    }

    private void openMainApplication(Stage primaryStage, String username) {
        primaryStage.close();
        UserData userData = Database.getUserByUsername(username);
        if (userData.isAdmin()) {
            AdminApp adminApp = new AdminApp();
            adminApp.start(new Stage());
        } else if (userData.isManager()) {
            ManagerApp managerApp = new ManagerApp();
            managerApp.start(new Stage());
        } else {
            BankingApp bankingApp = new BankingApp(username);
            bankingApp.start(new Stage());
        }
    }
}

