import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;

public class UserRegistrationApp extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label registrationStatusLabel;

    private boolean IsAdmin;

    private boolean IsManager;

    private CheckBox isAdmin;
    private CheckBox isManager;
    private String username;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Регистрация");

        Label titleLabel = new Label("Регистрация");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        Label usernameLabel = new Label("Пользователь:");
        usernameField = new TextField();

        Label passwordLabel = new Label("Пароль:");
        passwordField = new PasswordField();

        registrationStatusLabel = new Label();
        registrationStatusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12;");

        HBox checkboxes = new HBox(10);
        isAdmin = new CheckBox("Администратор");
        isManager = new CheckBox("Менеджер");
        isAdmin.setOnAction(e -> handleIsAdmin(primaryStage));
        isManager.setOnAction(e -> handleIsManager(primaryStage));

        Button registerButton = new Button("Зарегестрироваться");
        registerButton.setStyle("-fx-background-color: #d4523e; -fx-text-fill: white;");
        registerButton.setOnAction(e -> handleRegisterButtonClick(primaryStage));

        Button goToLoginPageButton = new Button("Войти в аккаунт");
        goToLoginPageButton.setOnAction(e -> openLoginPage(primaryStage));



        checkboxes.getChildren().addAll(isAdmin, isManager);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        layout.getChildren().addAll(titleLabel, usernameLabel, usernameField, passwordLabel, passwordField, checkboxes,
                registerButton, registrationStatusLabel, goToLoginPageButton);

        Scene scene = new Scene(layout, 600, 480);

        primaryStage.getIcons().add(new Image("./kspi.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleIsAdmin(Stage primaryStage) {
        if (isAdmin.isSelected()) {
            IsAdmin = true;
            isManager.setSelected(false);
        } else {
            IsAdmin = false;
        }
    }

    private void handleIsManager(Stage primaryStage) {
        if (isManager.isSelected()) {
            IsManager = true;
            isAdmin.setSelected(false);
        } else {
            IsManager = false;
        }
    }
    private void handleRegisterButtonClick(Stage primaryStage) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!username.isEmpty() && !password.isEmpty()) {
            if (!Database.userExists(username)) {
                UserData userData = new UserData(username, password, IsAdmin, IsManager, .0);
                Database.insertUserData(userData);

                this.username = username;
                usernameField.clear();
                passwordField.clear();
                registrationStatusLabel.setText("Registration successful!");

                openMainApplication(primaryStage);
            } else {
                showAlert("Ошибка", "Пользователь существует");
            }
        } else {
            showAlert("Ошибка", "Напишите имя и пароль");
        }
    }

    private void openMainApplication(Stage primaryStage) {
        primaryStage.close();
        if (IsAdmin) {
            AdminApp adminApp = new AdminApp();
            adminApp.start(new Stage());
        } else if (IsManager) {
            ManagerApp managerApp = new ManagerApp();
            managerApp.start(new Stage());
        } else {
            BankingApp bankingApp = new BankingApp(username);
            bankingApp.start(new Stage());
        }
    }

    private void openLoginPage(Stage primaryStage) {
        primaryStage.close();

        UserLoginApp userLoginApp = new UserLoginApp();
        userLoginApp.start(new Stage());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
