// com.ooad.ranewacapital.Launcher.java - Fixed launcher class
package com.ooad.ranewacapital;

import com.ooad.ranewacapital.controller.BankingController;
import com.ooad.ranewacapital.model.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Launcher extends Application { // Extend Application for proper JavaFX initialization

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseManager dbManager = new DatabaseManager(); // Initialize DB
            BankingController controller = new BankingController(dbManager); // Pass DB to controller (it creates CustomerService internally)
            controller.start(primaryStage); // Launch login view
        } catch (SQLException e) {
            e.printStackTrace();
            // Optional: Show error dialog or exit
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args); // Standard JavaFX launcher
    }
}