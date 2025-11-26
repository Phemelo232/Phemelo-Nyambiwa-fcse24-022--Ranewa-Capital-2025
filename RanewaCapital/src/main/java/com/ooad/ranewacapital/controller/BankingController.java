// ... (imports remain the same) ...
package com.ooad.ranewacapital.controller;

import com.ooad.ranewacapital.model.Customer;
import com.ooad.ranewacapital.model.DatabaseManager;
import com.ooad.ranewacapital.service.CustomerService;
import com.ooad.ranewacapital.view.AccountView;
import com.ooad.ranewacapital.view.CreateAccountView;
import com.ooad.ranewacapital.view.LoginView;
import com.ooad.ranewacapital.view.SignupView;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BankingController {
    private final LoginView loginView;
    private final SignupView signupView;
    private final AccountView accountView;
    private final CreateAccountView createAccountView;
    private final CustomerService customerService;
    private Customer currentCustomer;

    public BankingController(DatabaseManager dbManager) {
        this.customerService = new CustomerService(dbManager);
        this.loginView = new LoginView();
        this.signupView = new SignupView();
        this.accountView = new AccountView();
        this.createAccountView = new CreateAccountView();
        wireListeners();
    }

    private void wireListeners() {
        // ... (Keep existing login, signup, and createAccount listeners exactly as before) ...

        loginView.setListener(new LoginView.LoginViewListener() {
            @Override public void onLoginAttempt(String e, String p) { handleLogin(e, p); }
            @Override public void onSignup() { showSignup(); }
        });

        signupView.setListener(new SignupView.SignupViewListener() {
            @Override public void onSignupAttempt(String f, String s, String e, String p, String em) { handleSignup(f, s, e, p, em); }
            @Override public void onBackToLogin() { showLogin(); }
        });

        createAccountView.setListener((type, initialDeposit, branch, isVerified) -> {
            handleCreateAccount(type, initialDeposit, branch, isVerified);
        });

        // Dashboard Listener
        accountView.setListener(new AccountView.AccountViewListener() {
            @Override public void onAccountSelected(String acc) { loadBalance(acc); }
            @Override public void onDeposit(String acc, double amt) { performTransaction(acc, amt, "DEPOSIT"); }
            @Override public void onWithdraw(String acc, double amt) { performTransaction(acc, amt, "WITHDRAW"); }
            @Override public void onCalculateInterest(String acc) { performTransaction(acc, 0, "INTEREST"); }
            @Override public void onOpenNewAccount() { createAccountView.show(accountView.getStage()); }
            @Override public void onLogout() {
                Platform.runLater(() -> {
                    accountView.getStage().close();
                    currentCustomer = null;
                    showLogin();
                });
            }
        });
    }

    // ... (Keep handleLogin, handleSignup, handleCreateAccount, performTransaction logic exactly as before) ...

    private void handleLogin(String email, String password) {
        Platform.runLater(() -> loginView.showError(""));
        if (email.isEmpty() || password.isEmpty()) { loginView.showError("Please enter email and password"); return; }

        try {
            currentCustomer = customerService.validateLogin(email, password);
            if (currentCustomer != null) {
                Platform.runLater(() -> {
                    List<String> summaries = customerService.getAccountSummaries(currentCustomer);
                    accountView.show(new Stage(), summaries);
                    accountView.updateUserInfo(currentCustomer.getFirstName(), currentCustomer.getEmail());
                    accountView.updateTotalBalance(customerService.getTotalBalance(currentCustomer));
                    loginView.getStage().close();
                });
            } else { loginView.showError("Invalid email or password"); }
        } catch (SQLException e) { loginView.showError("DB Error"); e.printStackTrace(); }
    }

    private void handleSignup(String f, String s, String e, String p, String em) {
        try {
            if(customerService.createCustomer(new Customer(f,s,e,p,em)) > 0) {
                signupView.showSuccess("Success!");
                new Timer().schedule(new TimerTask() { @Override public void run() { showLogin(); } }, 1500);
            } else signupView.showError("Failed.");
        } catch(Exception ex) { signupView.showError(ex.getMessage()); }
    }

    private void handleCreateAccount(String type, double initialDeposit, String branch, boolean isVerified) {
        try {
            customerService.openNewAccount(currentCustomer, type, initialDeposit, branch, isVerified);
            Platform.runLater(() -> {
                createAccountView.close();
                refreshDashboard();
                accountView.showStatus("Account created!", false);
            });
        } catch (Exception e) { Platform.runLater(() -> createAccountView.showError(e.getMessage())); }
    }

    private void performTransaction(String acc, double amt, String type) {
        if (amt < 0) { accountView.showStatus("Amount must be positive", true); return; }
        try {
            if ("DEPOSIT".equals(type)) customerService.deposit(currentCustomer, acc, amt);
            else if ("WITHDRAW".equals(type)) customerService.withdraw(currentCustomer, acc, amt);
            else if ("INTEREST".equals(type)) customerService.calculateInterest(currentCustomer, acc);
            loadBalance(acc);
            refreshDashboard();
            accountView.showStatus("Success: " + type, false);
        } catch (Exception e) { accountView.showStatus("Error: " + e.getMessage(), true); }
    }

    private void showLogin() {
        Platform.runLater(() -> {
            if (signupView.getStage() != null) signupView.getStage().close();
            loginView.showError("");
            Stage stage = new Stage();
            loginView.start(stage);
        });
    }

    private void showSignup() {
        Platform.runLater(() -> {
            loginView.getStage().close();
            signupView.show(new Stage());
        });
    }

    private void refreshDashboard() {
        List<String> summaries = customerService.getAccountSummaries(currentCustomer);
        accountView.updateAccountsList(summaries);
        accountView.updateTotalBalance(customerService.getTotalBalance(currentCustomer));
    }

    // UPDATED CURRENCY: Formats single account balance with 'P'
    private void loadBalance(String accountNumber) {
        double balance = customerService.getBalance(currentCustomer, accountNumber);
        Platform.runLater(() -> accountView.updateBalance(String.format("P%.2f", balance)));
    }

    public void start(Stage primaryStage) {
        loginView.start(primaryStage);
    }
}