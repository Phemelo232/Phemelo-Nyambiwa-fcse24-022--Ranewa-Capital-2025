package com.ooad.ranewacapital.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignupView {
    private TextField firstNameField, surnameField, emailField, employmentField;
    private PasswordField passwordField, confirmPasswordField;
    private Label errorLabel, successLabel;
    private SignupViewListener listener;

    public interface SignupViewListener {
        void onSignupAttempt(String f, String s, String e, String p, String em);
        void onBackToLogin(); // <--- Back Action
    }

    public void setListener(SignupViewListener listener) { this.listener = listener; }

    public void show(Stage stage) {
        stage.setTitle("Ranewa Capital - Sign Up");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(12);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        // Fields
        grid.add(new Label("First Name:"), 0, 0);
        firstNameField = new TextField(); grid.add(firstNameField, 1, 0);

        grid.add(new Label("Surname:"), 0, 1);
        surnameField = new TextField(); grid.add(surnameField, 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        emailField = new TextField(); grid.add(emailField, 1, 2);

        grid.add(new Label("Password:"), 0, 3);
        passwordField = new PasswordField(); grid.add(passwordField, 1, 3);

        grid.add(new Label("Confirm Pass:"), 0, 4);
        confirmPasswordField = new PasswordField(); grid.add(confirmPasswordField, 1, 4);

        grid.add(new Label("Employment:"), 0, 5);
        employmentField = new TextField(); grid.add(employmentField, 1, 5);

        // --- BUTTONS ---
        Button submitButton = new Button("Create Account");
        submitButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-font-weight: bold;");
        submitButton.setOnAction(e -> handleSignup());

        Button backButton = new Button("Back to Login");
        backButton.setOnAction(e -> {
            if (listener != null) listener.onBackToLogin(); // Trigger Back
        });

        HBox buttonBox = new HBox(15, submitButton, backButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(buttonBox, 1, 6);

        // Feedback
        errorLabel = new Label(""); errorLabel.setStyle("-fx-text-fill: red;");
        successLabel = new Label(""); successLabel.setStyle("-fx-text-fill: green;");

        VBox root = new VBox(20, new Label("Create Account"), grid, errorLabel, successLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 450, 550));
        stage.show();
    }

    private void handleSignup() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorLabel.setText("Passwords do not match"); return;
        }
        if (listener != null) listener.onSignupAttempt(
                firstNameField.getText(), surnameField.getText(), emailField.getText(),
                passwordField.getText(), employmentField.getText()
        );
    }

    public void showError(String msg) { errorLabel.setText(msg); }
    public void showSuccess(String msg) { successLabel.setText(msg); }
    public Stage getStage() { return (Stage) firstNameField.getScene().getWindow(); }
}