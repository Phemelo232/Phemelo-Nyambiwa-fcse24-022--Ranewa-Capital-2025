package com.ooad.ranewacapital.view;// com.ooad.ranewacapital.view.LoginView.java - Boundary class for login screen (pure JavaFX, no FXML for simplicity)
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {
    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label errorLabel;
    private LoginViewListener listener; // Interface to notify controller (no business logic here)

    public interface LoginViewListener {
        void onLoginAttempt(String email, String password);
    }

    public LoginView() {
        // No business logic; just UI setup
    }

    public void setListener(LoginViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        // Email label and field
        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 0);
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        grid.add(emailField, 1, 0);

        // Password label and field
        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 1);
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        grid.add(passwordField, 1, 1);

        // Login button
        loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin());
        grid.add(loginButton, 1, 2);

        // Error label
        errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 1, 3);

        // Wrap in VBox for centering
        VBox root = new VBox(10, grid);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Banking System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        if (listener != null) {
            listener.onLoginAttempt(email, password); // Delegate to controller
        }
        // Clear fields on attempt (UI only)
        emailField.clear();
        passwordField.clear();
        errorLabel.setText(""); // Reset error
    }

    public void showError(String message) {
        errorLabel.setText(message); // UI update only
    }

    public static void main(String[] args) {
        launch(args);
    }
}