import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Date;
import java.util.Optional;

public class BankingApp extends Application {
    private TextField accountUsername;
    private TextField amountField;
    private Label balanceLabel;

    private String username;
    public BankingApp(String username) {
        this.username = username;
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Жаңа Банк");

        Label welcomeLabel = new Label("Добро пожаловать, " + username + "!");
        Label accountNumberLabel = new Label("Номер аккаунта:");
        accountUsername = new TextField();

        Label amountLabel = new Label("Количество:");
        amountField = new TextField();

        HBox hBox1 = new HBox(20);
        HBox hBox2 = new HBox(20);
        Button transferButton = new Button("Перевод денег");
        styleButton(transferButton);
        transferButton.setOnAction(e -> handleTransferButtonClick());

        Button withdrawButton = new Button("Пополнить счет");
        styleButton(withdrawButton);
        withdrawButton.setOnAction(e -> handleRefillButtonClick());

        Button balanceButton = new Button("Проверить баланс");
        styleButton(balanceButton);
        balanceButton.setOnAction(e -> CheckOwnBalanceClick(username));

        Button refundButton = new Button("Снять со счета");
        styleButton(refundButton);
        refundButton.setOnAction(e -> handleRefundButtonClick());

        Button creditButton = new Button("Оформить кредит");
        styleButton(creditButton);
        creditButton.setOnAction(e -> handleCreditButtonClick());

        Button repaymentButton = new Button("Досрочное погашение кредита");
        styleButton(repaymentButton);
        repaymentButton.setOnAction(e -> handleRepaymentButtonClick());

        hBox1.getChildren().addAll(transferButton, withdrawButton, balanceButton);
        hBox2.getChildren().addAll(refundButton, creditButton, repaymentButton);
        balanceLabel = new Label();

        Button logoutButton = new Button("Выйти с аккаунта");


        styleButton(logoutButton);
        logoutButton.setOnAction(e -> handleLogoutButtonClick(primaryStage));

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.setStyle("-fx-background-color: #f4f4f4;");
        layout.getChildren().addAll(welcomeLabel, accountNumberLabel, accountUsername, amountLabel,
                amountField, hBox1, hBox2, balanceLabel, logoutButton);

        Scene scene = new Scene(layout, 600, 480);
        primaryStage.getIcons().add(new Image("./kspi.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleRepaymentButtonClick() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Досрочное погашение кредита");

        UserData userData = Database.getUserByUsername(username);
        double remainingCredit = userData.debt;

        dialog.setHeaderText("У вас осталось к погашению: $" + remainingCredit + "\nВведите сумму для досрочного погашения:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(amountText -> {
            if (isValidInput(username, amountText)) {
                double amount = Double.parseDouble(amountText);

                if (amount <= userData.getBalance()) {
                    userData.decreaseBalance(amount);
                    userData.debt = userData.debt - amount;
                    Database.updateUser(userData);
                    showAlert("Досрочное погашение", "Сумма " + amount + "$ успешно погашена.");
                    Database.insertTransaction(new Transaction(amount, username, username, "Досрочное погашение", new Date(System.currentTimeMillis())));
                } else {
                    showAlert("Ошибка", "Недостаточно средств на счете для досрочного погашения.");
                }
            } else {
                showAlert("Ошибка", "Неверный ввод. Пожалуйста, введите правильную сумму.");
            }
        });
    }

    private void handleCreditButtonClick() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Оформление кредита");
        dialog.setHeaderText("Введите детали кредита:");

        TextField creditAmountField = new TextField();
        TextField interestRateField = new TextField();
        TextField termField = new TextField();

        dialog.getDialogPane().setContent(new VBox(10, new Label("Сумма кредита:"), creditAmountField,
                new Label("Процентная ставка:"), interestRateField,
                new Label("Срок (в годах):"), termField));

        ButtonType applyButton = new ButtonType("Применить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButton) {
                String amountText = creditAmountField.getText();
                String rateText = interestRateField.getText();
                String termText = termField.getText();

                if (isValidInput(username, amountText) && isValidInput(username, rateText) && isValidInput(username, termText)) {
                    double amount = Double.parseDouble(amountText);
                    double interestRate = Double.parseDouble(rateText);
                    int term = Integer.parseInt(termText);

                    double totalAmount = calculateTotalAmount(amount, interestRate, term);
                    UserData userData = Database.getUserByUsername(username);
                    userData.increaseBalance(amount);
                    userData.debt += totalAmount;
                    System.out.println(totalAmount);
                    Database.updateUser(userData);
                    showAlert("Кредит оформлен", "Сумма к возврату: $" + totalAmount);
                } else {
                    showAlert("Ошибка", "Неверный ввод. Пожалуйста, введите правильные значения.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private double calculateTotalAmount(double principal, double interestRate, int term) {
        double monthlyInterestRate = interestRate / 12 / 100;
        double numberOfPayments = term * 12;
        return principal * (Math.pow(1 + monthlyInterestRate, numberOfPayments) - 1) / monthlyInterestRate * (1 + monthlyInterestRate);
    }
    private void handleTransferButtonClick() {
        String username = accountUsername.getText();
        String amountText = amountField.getText();
        System.out.println(username);
        System.out.println(amountText);
        if (isValidInput(username, amountText)) {
            double amount = Double.parseDouble(amountText);
            UserData userData = Database.getUserByUsername(username);
            UserData thisUser = Database.getUserByUsername(this.username);
            if (userData != null) {

                if (userData.isAdmin() ||
                        userData.isManager()) {
                    showAlert("Ошибка", "Аккаунт отправителя не найден");
                }
                else if (thisUser.getBalance() >= amount) {
                    thisUser.decreaseBalance(amount);
                    userData.increaseBalance(amount);
                    Database.updateUser(thisUser);
                    Database.updateUser(userData);
                    Database.insertTransaction(new Transaction(amount, this.username, username, "Перевод", new Date(System.currentTimeMillis())));
                    showAlert("Перевод средств", "Перевод выполнен успешно!");
                } else {
                    showAlert("Ошибка", "Недостаточно средств для перевода.");
                }
            } else {
                showAlert("Ошибка", "Аккаунт отправителя не найден");
            }
        } else {
            showAlert("Ошибка", "Неверный ввод. Пожалуйста, введите правильный номер счета и сумму.");
        }
    }

    private void handleRefillButtonClick() {
        String amountText = amountField.getText();

        if (isValidInput(username, amountText)) {
            double amount = Double.parseDouble(amountText);
            UserData userData = Database.getUserByUsername(username);
            if (userData != null) {
                userData.increaseBalance(amount);
                Database.updateUser(userData);
               Database.insertTransaction(new Transaction(amount, username, username, "Пополнение", new Date(System.currentTimeMillis())));
                showAlert("Пополнение счета", "Сумма пополнения " + amount + "$");
            } else {
                showAlert("Ошибка", "Аккаунт не найден");
            }
        } else {
            showAlert("Ошибка", "Неверный ввод. Пожалуйста, введите правильный номер счета и сумму.");
        }
    }

    private boolean isValidInput(String accountNumber, String amountText) {
        return !accountNumber.isEmpty() && !amountText.isEmpty() && amountText.matches("\\d+(\\.\\d+)?");
    }

    private void CheckOwnBalanceClick(String username) {
        UserData userData = Database.getUserByUsername(username);
        if (userData != null) {
            double balance = userData.getBalance();
            balanceLabel.setText("Balance: $" + balance);
        } else {
            showAlert("Ошибка", "Аккаунт не найден");
        }
    }
    public void handleRefundButtonClick() {
        String amountText = amountField.getText();
        UserData userData = Database.getUserByUsername(username);
        if (Double.parseDouble(amountText) <= userData.getBalance()) {
            userData.decreaseBalance(Double.parseDouble(amountText));
            showAlert("Снятие со счета прошло успешно", "Со счета сняли " + Double.parseDouble(amountText) + "$");
            Database.insertTransaction(new Transaction(Double.parseDouble(amountText), username, username, "Снятие со счета", new Date(System.currentTimeMillis())));
        }
        else {
            showAlert("Недостаточно средств на счете", "Нехватает денег");
        }
    }

    private void handleLogoutButtonClick(Stage primaryStage) {
        primaryStage.close();

        UserRegistrationApp userRegistrationApp = new UserRegistrationApp();
        userRegistrationApp.start(new Stage());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void styleButton(Button button) {
        button.setStyle(
                "-fx-background-color: #d4523e; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 10px;"
        );
    }
}
