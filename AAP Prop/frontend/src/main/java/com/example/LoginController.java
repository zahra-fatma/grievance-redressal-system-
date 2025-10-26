package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleBox;

    @FXML
    private Label messageLabel; // 🔹 To display error/success messages

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("Citizen", "Admin");
    }

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            messageLabel.setText("⚠ Please fill all fields and select a role.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/auth/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInput = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"%s\"}",
                    username, password, role
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            StringBuilder response = new StringBuilder();

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream(),
                            StandardCharsets.UTF_8
                    ))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
            }

            System.out.println("Login Response: " + response);

            String resp = response.toString();

            // ✅ Only accept exact success message from backend
            if (status == 200 && resp.startsWith("✅ Login successful")) {
                messageLabel.setText("✅ Login successful!");

                // 🔹 Save logged-in user to SessionManager
                SessionManager.setLoggedInUser(username);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader;

                if ("Admin".equalsIgnoreCase(role)) {
                    loader = new FXMLLoader(getClass().getResource("admin-view.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("citizen-view.fxml"));
                }

                Scene scene = new Scene(loader.load(), 1200, 800);
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            } else {
                // Show backend message directly
                messageLabel.setText(resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Could not connect to server.");
        }
    }

    @FXML
    protected void goToRegister() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Failed to open register screen.");
        }
    }
}