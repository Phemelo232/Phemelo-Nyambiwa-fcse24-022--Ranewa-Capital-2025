package com.ooad.ranewacapital;

import com.ooad.ranewacapital.controller.BankingController;
import com.ooad.ranewacapital.model.DatabaseManager;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.SQLException;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Initialize Database Connection
            DatabaseManager dbManager = new DatabaseManager();

            // 2. Initialize Controller (which sets up Service and Views)
            BankingController controller = new BankingController(dbManager);

            // 3. Start the Application Flow
            controller.start(primaryStage);

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Connection Failed", "Could not connect to the database.\nPlease check your internet connection and credentials.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Critical Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}