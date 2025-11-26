package com.ooad.ranewacapital.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateAccountView {
    private Stage stage;
    private ComboBox<String> typeBox;
    private TextField depositField;
    private TextField branchField;
    private CheckBox employerCheck;
    private Label errorLabel;
    private CreateAccountListener listener;

    public interface CreateAccountListener {
        void onRequestAccountCreation(String type, double initialDeposit, String branch, boolean employerVerified);
    }

    public void setListener(CreateAccountListener listener) {
        this.listener = listener;
    }

    public void show(Stage owner) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Open New Account");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Account Type
        grid.add(new Label("Account Type:"), 0, 0);
        typeBox = new ComboBox<>(FXCollections.observableArrayList("Savings", "Investment", "Cheque"));
        typeBox.setValue("Savings");
        grid.add(typeBox, 1, 0);

        // Branch
        grid.add(new Label("Branch Name:"), 0, 1);
        branchField = new TextField("Main Branch");
        grid.add(branchField, 1, 1);

        // Initial Deposit (UPDATED CURRENCY)
        grid.add(new Label("Initial Deposit (P):"), 0, 2);
        depositField = new TextField();
        depositField.setPromptText("Min P500 for Investment");
        grid.add(depositField, 1, 2);

        // Employer Verification (Hidden unless Cheque selected)
        employerCheck = new CheckBox("Employer Verified?");
        grid.add(employerCheck, 1, 3);
        employerCheck.setVisible(false);

        // Dynamic Logic
        typeBox.setOnAction(e -> {
            String selected = typeBox.getValue();
            employerCheck.setVisible("Cheque".equals(selected));
            if ("Investment".equals(selected)) {
                depositField.setPromptText("Min P500.00"); // Updated to P
            } else {
                depositField.setPromptText("Any amount");
            }
        });

        // Buttons
        Button createBtn = new Button("Open Account");
        createBtn.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold;");
        createBtn.setOnAction(e -> handleCreate());

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, createBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttons, 1, 4);

        // Error Feedback
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 0, 5, 2, 1);

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.show();
    }

    private void handleCreate() {
        try {
            String type = typeBox.getValue();
            String branch = branchField.getText().trim();
            String depositText = depositField.getText().trim();
            double deposit = depositText.isEmpty() ? 0.0 : Double.parseDouble(depositText);
            boolean isVerified = employerCheck.isSelected();

            if (branch.isEmpty()) {
                errorLabel.setText("Branch is required.");
                return;
            }

            if (listener != null) {
                listener.onRequestAccountCreation(type, deposit, branch, isVerified);
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid amount entered.");
        }
    }

    public void close() {
        if (stage != null) stage.close();
    }

    public void showError(String msg) {
        errorLabel.setText(msg);
    }
}