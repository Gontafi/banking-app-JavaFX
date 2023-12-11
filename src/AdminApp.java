import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class AdminApp extends Application {

    private ListView<String> adminUserList;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Admin Panel");

        Label titleLabel = new Label("Admin Panel");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        Label adminUserListLabel = new Label("Admin User List:");
        adminUserList = new ListView<>();
        List<UserData> users = Database.getAllUsers();
        ObservableList<String> usernames = FXCollections.observableArrayList();
        for (UserData user : users) {
            usernames.add(user.getUsername());
        }
        adminUserList.getItems().addAll(usernames);

        Button editButton = new Button("Edit User");
        styleButton(editButton);
        editButton.setOnAction(e -> handleEditUserButtonClick());

        Button logoutButton = new Button("Logout");
        styleButton(logoutButton);
        logoutButton.setOnAction(e -> handleLogoutButtonClick(primaryStage));

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(titleLabel, adminUserListLabel, adminUserList, editButton, logoutButton);

        Scene scene = new Scene(layout, 600, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleEditUserButtonClick() {
        String selectedUser = adminUserList.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            openEditUserDialog(selectedUser);
        } else {
            showAlert("Error", "Please select a user to edit.");
        }
    }

    private void openEditUserDialog(String username) {
        Stage editUserStage = new Stage();
        editUserStage.setTitle("Edit User");

        UserData userData = Database.getUserByUsername(username);

        TextField newPasswordField = new TextField();
        RadioButton isAdminCheckBox = new RadioButton("Admin");
        RadioButton isManagerCheckBox = new RadioButton("Manager");
        ToggleGroup tg = new ToggleGroup();
        isAdminCheckBox.setToggleGroup(tg);
        isManagerCheckBox.setToggleGroup(tg);

        newPasswordField.setPromptText("Enter new password");
        isAdminCheckBox.setSelected(userData.isAdmin());
        isManagerCheckBox.setSelected(userData.isManager());

        Button saveButton = new Button("Save Changes");
        styleButton(saveButton);
        saveButton.setOnAction(e -> handleSaveChangesButtonClick(username, newPasswordField.getText(),
                isAdminCheckBox.isSelected(), isManagerCheckBox.isSelected(), editUserStage));

        VBox editUserLayout = new VBox(20);
        editUserLayout.setPadding(new Insets(20, 20, 20, 20));
        editUserLayout.getChildren().addAll(newPasswordField, isAdminCheckBox, isManagerCheckBox, saveButton);

        Scene editUserScene = new Scene(editUserLayout, 300, 200);

        editUserStage.setScene(editUserScene);

        editUserStage.show();
    }

    private void handleSaveChangesButtonClick(String username, String newPassword, boolean isAdmin, boolean isManager, Stage editUserStage) {
        UserData userData = Database.getUserByUsername(username);
        userData.setPassword(newPassword);
        userData.setAdmin(isAdmin);
        userData.setManager(isManager);

        Database.updateUser(userData);
        editUserStage.close();

        List<UserData> users = Database.getAllUsers();
        ObservableList<String> usernames = FXCollections.observableArrayList();
        for (UserData user : users) {
            usernames.add(user.getUsername());
        }
        adminUserList.getItems().setAll(usernames);

        showAlert("Success", "User data updated successfully.");
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
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
