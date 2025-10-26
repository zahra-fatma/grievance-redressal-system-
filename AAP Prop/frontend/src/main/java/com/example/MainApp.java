package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        // 🔹 Start with the Login screen
        showLoginView();
    }

    private void showLoginView() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 250);

        // 🌍 Add global CSS (applies everywhere)
        scene.getStylesheets().add(getClass().getResource("/com/example/neo-brutal.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/com/example/login.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Citizen Login");
        primaryStage.show();
    }

    // 🔹 Call this after successful login
    public void showCitizenPortal() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/citizen-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);

        // 🌍 Reuse global CSS
        scene.getStylesheets().add(getClass().getResource("/com/example/neo-brutal.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/com/example/login.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Citizen Portal");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}