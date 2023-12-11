import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ManagerApp extends Application {

    private ListView<String> userList;
    private ListView<String> transactionList;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Manager App");

        Label titleLabel = new Label("Manager Dashboard");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        Label userListLabel = new Label("User List:");
        userList = new ListView<>();
        List<UserData> users = Database.getAllUsers();
        ObservableList<String> usernames = FXCollections.observableArrayList();
        for (UserData user : users) {
            usernames.add(user.getUsername());
        }
        userList.getItems().addAll(usernames);
        userList.setOnMouseClicked(e -> handleUserSelection());

        Label transactionListLabel = new Label("Transaction List:");
        transactionList = new ListView<>();
        transactionList.getItems().addAll(getTransactionDescriptions());

        Button logoutButton = new Button("Выйти с аккаунта");
        styleButton(logoutButton);
        logoutButton.setOnAction(e -> handleLogoutButtonClick(primaryStage));

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(titleLabel, userListLabel, userList, transactionListLabel,
                transactionList, logoutButton);

        Scene scene = new Scene(layout, 600, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleUserSelection() {
        String selectedUser = userList.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            updateTransactionList(selectedUser);
        }
    }

    private void updateTransactionList(String username) {
        transactionList.getItems().clear();
        for (Transaction transaction : Database.getTransactions()) {
            if (transaction.getFrom().equals(username) || transaction.getTo().equals(username)) {
                transactionList.getItems().add(transaction.getDescription());
            }
        }
    }

    private ObservableList<String> getTransactionDescriptions() {
        ObservableList<String> transactionDescriptions = FXCollections.observableArrayList();
        for (Transaction transaction : Database.getTransactions()) {
            transactionDescriptions.add(transaction.getDescription());
        }
        return transactionDescriptions;
    }

    private void handleLogoutButtonClick(Stage primaryStage) {
        primaryStage.close();

        UserRegistrationApp userRegistrationApp = new UserRegistrationApp();
        userRegistrationApp.start(new Stage());
    }
    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: #d4523e; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px;"
        );
    }
}
