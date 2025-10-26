package com.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleBox;

    @FXML
    private Label messageLabel;

    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("Citizen", "Admin");
    }

    @FXML
    protected void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleBox.getValue();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            messageLabel.setText("⚠️ Please fill all fields!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("❌ Passwords do not match!");
            return;
        }

        // Run async so UI stays responsive
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/auth/register";
                String jsonBody = String.format(
                        "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                        username, password, role
                );

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200 && response.body().toLowerCase().contains("success")) {
                        messageLabel.setText("✅ Account created! Please login.");
                        goBackToLogin();
                    } else {
                        messageLabel.setText("❌ Registration failed: " + response.body());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> messageLabel.setText("⚠️ Error connecting to server"));
            }
        }).start();
    }

    @FXML
    protected void goBackToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("⚠️ Failed to load login screen.");
        }
    }
}