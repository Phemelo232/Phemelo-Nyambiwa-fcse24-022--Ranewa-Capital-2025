package com.ooad.ranewacapital.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class AccountView {
    private ListView<String> accountsListView;
    private TextField amountField;
    private ChoiceBox<String> actionChoiceBox;
    private Label balanceLabel;
    private Label statusLabel;
    private Label userInfoLabel;
    private Label totalBalanceLabel;
    private AccountViewListener listener;

    public interface AccountViewListener {
        void onAccountSelected(String accountNumber);
        void onDeposit(String accountNumber, double amount);
        void onWithdraw(String accountNumber, double amount);
        void onCalculateInterest(String accountNumber);
        void onOpenNewAccount();
        void onLogout();
    }

    public void setListener(AccountViewListener listener) {
        this.listener = listener;
    }

    public void show(Stage stage, List<String> accountSummaries) {
        // --- Top Bar ---
        userInfoLabel = new Label("Logged in...");
        userInfoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #007BFF;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11;");
        logoutBtn.setOnAction(e -> {
            if (listener != null) listener.onLogout();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(10, userInfoLabel, spacer, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // --- Summary Section (UPDATED CURRENCY) ---
        totalBalanceLabel = new Label("Total Balance: P0.00");
        totalBalanceLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #28A745;");

        // --- Open Account Button ---
        Button openAccountBtn = new Button("+ Open New Account");
        openAccountBtn.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-weight: bold;");
        openAccountBtn.setMaxWidth(Double.MAX_VALUE);
        openAccountBtn.setOnAction(e -> {
            if (listener != null) listener.onOpenNewAccount();
        });

        // --- Account List ---
        accountsListView = new ListView<>();
        accountsListView.setItems(FXCollections.observableArrayList(accountSummaries));
        accountsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && listener != null) {
                String accountNumber = newVal.split(":")[0].trim();
                listener.onAccountSelected(accountNumber);
            }
        });

        // --- Action Section ---
        actionChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("Deposit", "Withdraw", "Calculate Interest"));
        actionChoiceBox.setValue("Deposit");

        amountField = new TextField();
        amountField.setPromptText("Amount");

        Button actionButton = new Button("Execute");
        actionButton.setOnAction(e -> handleAction());

        HBox actionBox = new HBox(10, actionChoiceBox, amountField, actionButton);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        balanceLabel = new Label("Select an account...");
        statusLabel = new Label("");
        statusLabel.setWrapText(true);

        // --- Main Layout ---
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
                topBar,
                totalBalanceLabel,
                new Separator(),
                openAccountBtn,
                new Label("Your Accounts:"),
                accountsListView,
                balanceLabel,
                new Separator(),
                actionBox,
                statusLabel
        );

        Scene scene = new Scene(root, 480, 600);
        stage.setTitle("Ranewa Capital - Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private void handleAction() {
        String selected = accountsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus("Select an account first.", true);
            return;
        }
        String accountNumber = selected.split(":")[0].trim();
        try {
            double amount = amountField.getText().isEmpty() ? 0 : Double.parseDouble(amountField.getText());
            if (listener != null) {
                String action = actionChoiceBox.getValue();
                if ("Deposit".equals(action)) listener.onDeposit(accountNumber, amount);
                else if ("Withdraw".equals(action)) listener.onWithdraw(accountNumber, amount);
                else if ("Calculate Interest".equals(action)) listener.onCalculateInterest(accountNumber);
            }
            amountField.clear();
        } catch (NumberFormatException e) {
            showStatus("Invalid amount.", true);
        }
    }

    public void updateBalance(String text) { balanceLabel.setText("Current Balance: " + text); }
    public void updateAccountsList(List<String> list) { accountsListView.setItems(FXCollections.observableArrayList(list)); }
    public void updateUserInfo(String name, String email) { userInfoLabel.setText(name + " (" + email + ")"); }

    // UPDATED CURRENCY
    public void updateTotalBalance(double total) {
        totalBalanceLabel.setText("Total Balance: P" + String.format("%.2f", total));
    }

    public void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: " + (isError ? "red;" : "green;"));
    }

    public Stage getStage() { return (Stage) accountsListView.getScene().getWindow(); }
}