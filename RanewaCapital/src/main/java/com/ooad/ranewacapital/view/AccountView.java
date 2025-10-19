package com.ooad.ranewacapital.view;// com.ooad.ranewacapital.view.AccountView.java - Boundary class for account overview (pure JavaFX)
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.List;

public class AccountView {
    private ListView<String> accountsListView;
    private TextField amountField;
    private ChoiceBox<String> actionChoiceBox;
    private Label balanceLabel;
    private AccountViewListener listener; // Interface for delegation

    public interface AccountViewListener {
        void onAccountSelected(String accountNumber);
        void onDeposit(String accountNumber, double amount);
        void onWithdraw(String accountNumber, double amount);
        void onCalculateInterest(String accountNumber);
    }

    public AccountView() {
        // No business logic
    }

    public void setListener(AccountViewListener listener) {
        this.listener = listener;
    }

    public void show(Stage stage, List<String> accountSummaries) {
        // Populate accounts list
        accountsListView = new ListView<>();
        accountsListView.setItems(FXCollections.observableArrayList(accountSummaries));
        accountsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && listener != null) {
                // Extract account number from summary string (e.g., "ACC123: $1000.00")
                String accountNumber = newVal.split(":")[0].trim();
                listener.onAccountSelected(accountNumber);
            }
        });

        // Action selection
        actionChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("Deposit", "Withdraw", "Calculate Interest"));
        actionChoiceBox.setValue("Deposit");

        amountField = new TextField();
        amountField.setPromptText("Enter amount");

        Button actionButton = new Button("Perform Action");
        actionButton.setOnAction(e -> handleAction());

        balanceLabel = new Label("Balance: $0.00");

        // Layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        root.getChildren().addAll(
                new Label("Select Account:"),
                accountsListView,
                balanceLabel,
                new HBox(10, new Label("Action:"), actionChoiceBox, new Label("Amount:"), amountField, actionButton)
        );

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Banking System - Accounts");
        stage.setScene(scene);
        stage.show();
    }

    private void handleAction() {
        String selectedAction = actionChoiceBox.getValue();
        String accountNumber = accountsListView.getSelectionModel().getSelectedItem() != null ?
                accountsListView.getSelectionModel().getSelectedItem().split(":")[0].trim() : null;
        if (accountNumber == null) return;

        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (listener != null) {
                switch (selectedAction) {
                    case "Deposit" -> listener.onDeposit(accountNumber, amount);
                    case "Withdraw" -> listener.onWithdraw(accountNumber, amount);
                    case "Calculate Interest" -> listener.onCalculateInterest(accountNumber);
                }
            }
        } catch (NumberFormatException ex) {
            // UI-only error handling: Could show alert, but keeping simple
        }
        amountField.clear(); // UI reset
    }

    public void updateBalance(String balanceText) {
        balanceLabel.setText("Balance: " + balanceText); // UI update only
    }

    public void updateAccountsList(List<String> summaries) {
        accountsListView.setItems(FXCollections.observableArrayList(summaries)); // UI update only
    }
}