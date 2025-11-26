// com.ooad.ranewacapital.view.LoginView.java - Enhanced with Signup button
package com.ooad.ranewacapital.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {
    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button signupButton;  // Added: Signup button
    private Label errorLabel;
    private Label welcomeLabel;
    private Label infoLabel;
    private LoginViewListener listener;

    public void show(Stage stage) {
    }

    public interface LoginViewListener {
        void onLoginAttempt(String email, String password);
        void onSignup();  // Already exists - perfect!
    }

    public LoginView() {
        // Pure boundary class - no business logic
    }

    public void setListener(LoginViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void start(Stage primaryStage) {
        // Header section
        welcomeLabel = new Label("Welcome to Ranewa Capital");
        welcomeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #007BFF;");

        infoLabel = new Label("Securely manage your accounts and transactions.\nVersion 1.0 | © 2025 Ranewa Capital");
        infoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #6c757d;");

        // Form grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(12);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        // Email
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-weight: bold;");
        grid.add(emailLabel, 0, 0);
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefWidth(220);
        grid.add(emailField, 1, 0);

        // Password
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        grid.add(passwordLabel, 0, 1);
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(220);
        grid.add(passwordField, 1, 1);

        // Buttons row: Login (primary) + Signup (secondary)
        loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setPrefWidth(100);
        loginButton.setOnAction(e -> handleLogin());

        signupButton = new Button("Create Account");
        signupButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #007BFF; -fx-border-color: #007BFF; -fx-border-radius: 4; -fx-padding: 8;");
        signupButton.setPrefWidth(120);
        signupButton.setOnAction(e -> {
            if (listener != null) {
                listener.onSignup();
            }
        });

        HBox buttonBox = new HBox(15, loginButton, signupButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 1, 2);

        // Error message
        errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11;");
        grid.add(errorLabel, 1, 3);

        // Main layout
        VBox root = new VBox(20, welcomeLabel, infoLabel, grid);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(root, 420, 380);
        primaryStage.setTitle("Ranewa Capital - Login");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (listener != null) {
            listener.onLoginAttempt(email, password);
        }

        // Clear inputs after attempt
        emailField.clear();
        passwordField.clear();
        errorLabel.setText("");
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    public Stage getStage() {
        return (Stage) emailField.getScene().getWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}