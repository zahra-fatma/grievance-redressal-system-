package com.example;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class CitizenController {

    // ---------------- Complaint fields ----------------
    @FXML private TextField complaintTitleField;
    @FXML private TextArea complaintDetailsArea;
    @FXML private TextArea ledgerView;
    @FXML private ListView<String> announcementsList;
    @FXML private ComboBox<String> categoryCombo;

    // My Complaints Table
    @FXML private TableView<ComplaintFX> complaintsTable;
    @FXML private TableColumn<ComplaintFX, Long> colId;
    @FXML private TableColumn<ComplaintFX, String> colTitle;
    @FXML private TableColumn<ComplaintFX, String> colStatus;
    @FXML private TableColumn<ComplaintFX, String> colDate;
    @FXML private TableColumn<ComplaintFX, String> colAutoComment;  // ✅ Auto Comment column

    // File upload support
    @FXML private Label attachedFileLabel;
    private File attachedFile;

    // ---------------- Profile fields ----------------
    @FXML private ImageView profileImage;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;

    // ---------------- Initialization ----------------
    @FXML
    public void initialize() {
        if (colId != null) colId.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getId()).asObject());
        if (colTitle != null) colTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        if (colStatus != null) colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        if (colDate != null) colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));

        if (colAutoComment != null) {
            colAutoComment.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAutoComment()));

            // ✅ Enable text wrapping in Auto Comment column
            colAutoComment.setCellFactory(tc -> {
                TableCell<ComplaintFX, String> cell = new TableCell<>() {
                    private final Text text = new Text();

                    {
                        text.wrappingWidthProperty().bind(tc.widthProperty().subtract(10));
                        setGraphic(text);
                        setPrefHeight(Control.USE_COMPUTED_SIZE);
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        text.setText(empty || item == null ? null : item);
                    }
                };
                return cell;
            });
        }

        if (announcementsList != null) {
            announcementsList.getItems().addAll(
                    "🚨 System maintenance on Sept 15",
                    "✅ Your complaint #102 has been resolved",
                    "ℹ️ New feature: Track complaints by status"
            );
        }
        if (categoryCombo != null) {
            categoryCombo.getItems().addAll("Water", "Electricity", "Roads", "Sanitation", "Other");
        }
    }

    // ---------------- Navigation Methods ----------------
    @FXML private void showFileComplaint() { showAlert("Coming Soon", "Complaint filing feature is not enabled yet."); }

    // ✅ Fetch complaints for the logged-in user
    @FXML private void showMyComplaints() {
        try {
            String username = SessionManager.getLoggedInUser(); // ✅ Always from session
            String url = "http://localhost:8080/api/complaints/user/" + username;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();

                ObjectMapper mapper = new ObjectMapper();
                List<ComplaintFX> complaints = mapper.readValue(body, new TypeReference<List<ComplaintFX>>() {});

                ObservableList<ComplaintFX> data = FXCollections.observableArrayList(complaints);
                complaintsTable.setItems(data);
            } else {
                showAlert("Error", "Failed to fetch complaints: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not load complaints: " + e.getMessage());
        }
    }

    @FXML private void showLedger() { showAlert("Coming Soon", "Ledger feature is not enabled yet."); }
    @FXML private void showAnnouncements() { showAlert("Coming Soon", "Announcements feature is not enabled yet."); }
    @FXML private void showDashboard() { showAlert("Coming Soon", "Dashboard feature is not enabled yet."); }
    @FXML private void showProfile() { showAlert("Coming Soon", "Profile feature is not enabled yet."); }

    // ---------------- Complaint Handling ----------------
    @FXML
    protected void handleAttachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Attach Evidence File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx", "*.txt")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            attachedFile = file;
            attachedFileLabel.setText("Attached: " + file.getName());
        }
    }

    @FXML
    protected void handleSubmitComplaint() {
        String title = (complaintTitleField != null) ? complaintTitleField.getText().trim() : "";
        String description = (complaintDetailsArea != null) ? complaintDetailsArea.getText().trim() : "";
        String username = SessionManager.getLoggedInUser(); // ✅ from session
        String status = "Pending";

        if (title.isEmpty() || description.isEmpty()) {
            showAlert("Validation", "Please enter both title and description.");
            return;
        }

        String json = "{"
                + "\"title\":\"" + escapeJson(title) + "\","
                + "\"description\":\"" + escapeJson(description) + "\","
                + "\"status\":\"" + escapeJson(status) + "\","
                + "\"username\":\"" + escapeJson(username) + "\""
                + "}";

        final String BACKEND_URL = "http://localhost:8081/api/complaints";

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BACKEND_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                showAlert("Success", "Complaint submitted successfully.");
                if (complaintTitleField != null) complaintTitleField.clear();
                if (complaintDetailsArea != null) complaintDetailsArea.clear();
                attachedFile = null;
                if (attachedFileLabel != null) attachedFileLabel.setText("No file selected");

                // ✅ refresh complaints after submit
                showMyComplaints();
            } else {
                showAlert("Error", "Submission failed: " + response.statusCode() + "\n" + response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not submit complaint: " + e.getMessage());
        }
    }

    // ---------------- Profile Handling ----------------
    @FXML
    protected void handleUploadProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            profileImage.setImage(image);
        }
    }

    @FXML
    protected void handleSaveProfile() {
        showAlert("Coming Soon", "Profile saving not available yet.");
    }

    // ---------------- Helpers ----------------
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}