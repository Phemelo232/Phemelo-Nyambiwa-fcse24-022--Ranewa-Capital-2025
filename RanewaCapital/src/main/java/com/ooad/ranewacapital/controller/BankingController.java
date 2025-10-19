package com.ooad.ranewacapital.controller;

// BankingController.java - Full controller integrating boundary classes and business logic
import com.ooad.ranewacapital.model.Customer;
import com.ooad.ranewacapital.model.DatabaseManager;
import com.ooad.ranewacapital.view.AccountView;
import com.ooad.ranewacapital.view.LoginView;
import javafx.stage.Stage;
import service.CustomerService;

import java.sql.SQLException;
import java.util.List;

public class BankingController {
    private LoginView loginView;
    private AccountView accountView;
    private CustomerService customerService;
    private Customer currentCustomer;

    public BankingController(DatabaseManager dbManager) {
        this.customerService = new CustomerService(dbManager);
        initializeViews();
    }

    private void initializeViews() {
        loginView = new LoginView();
        loginView.setListener(this::handleLoginAttempt);

        accountView = new AccountView();
        accountView.setListener(this::handleAccountSelected);
        accountView.setListener(this::handleDeposit);
        accountView.setListener(this::handleWithdraw);
        accountView.setListener(this::handleCalculateInterest);
    }

    // Login handler
    private void handleLoginAttempt(String email, String password) {
        try {
            // Note: Password ignored for demo; add to Customer in real scenario
            currentCustomer = customerService.validateLogin(email);
            if (currentCustomer != null) {
                // Switch to account view
                List<String> summaries = customerService.getAccountSummaries(currentCustomer);
                new Thread(() -> { // Off UI thread for DB
                    try {
                        javafx.application.Platform.runLater(() -> {
                            accountView.show(new Stage(), summaries);
                            try {
                                loginView.stop(); // Close login (extend Application to add stop())
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                javafx.application.Platform.runLater(() -> loginView.showError("Invalid email or password"));
            }
        } catch (SQLException e) {
            javafx.application.Platform.runLater(() -> loginView.showError("Database error: " + e.getMessage()));
        }
    }

    // Account selection handler
    private void handleAccountSelected(String accountNumber) {
        try {
            double balance = customerService.getBalance(accountNumber);
            javafx.application.Platform.runLater(() -> accountView.updateBalance(String.format("$%.2f", balance)));
        } catch (SQLException e) {
            // Handle error (e.g., show alert)
        }
    }

    // Deposit handler
    private void handleDeposit(String accountNumber, double amount) {
        try {
            customerService.deposit(accountNumber, amount);
            handleAccountSelected(accountNumber); // Refresh balance
            // Update summaries if needed
        } catch (SQLException | IllegalArgumentException e) {
            // Show error in UI
        }
    }

    // Withdraw handler
    private void handleWithdraw(String accountNumber, double amount) {
        try {
            customerService.withdraw(accountNumber, amount);
            handleAccountSelected(accountNumber); // Refresh
        } catch (SQLException | IllegalArgumentException e) {
            // Handle
        }
    }

    // Interest calculation handler
    private void handleCalculateInterest(String accountNumber) {
        try {
            double interest = customerService.calculateInterest(accountNumber);
            // Optionally: deposit interest
            // customerService.deposit(accountNumber, interest);
            // For demo: Just log or show
            System.out.println("Interest for " + accountNumber + ": $" + interest);
            handleAccountSelected(accountNumber); // Refresh if credited
        } catch (SQLException e) {
            // Handle
        }
    }

    // Main entry point
    public void start(Stage primaryStage) {
        loginView.start(primaryStage); // Launches login
    }

    // For testing/integration
    public static void main(String[] args) {
        DatabaseManager dbManager = null;
        try {
            dbManager = new DatabaseManager();
            BankingController controller = new BankingController(dbManager);
            javafx.application.Application.launch(LoginView.class, args); // Or custom launcher
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
