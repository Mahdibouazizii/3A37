package ecorayen.controllers;

import ecorayen.models.badge;
import ecorayen.services.ServiceBadge;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class UpdateBadgeController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField imagePathField;

    private badge currentBadge;
    private final ServiceBadge serviceBadge = new ServiceBadge();
    private File selectedImageFile;

    public void initData(badge badge) {
        this.currentBadge = badge;
        if (badge != null) {
            nameField.setText(badge.getName());
            descriptionField.setText(badge.getDescription());
            imagePathField.setText(badge.getImagePath());
        }
    }

    @FXML
    void update(MouseEvent event) {
        if (currentBadge == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No badge selected for update.");
            return;
        }

        String name = nameField.getText();
        String description = descriptionField.getText();
        String imagePath = imagePathField.getText();

        if (name.isEmpty() || description.isEmpty() || imagePath.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        currentBadge.setName(name);
        currentBadge.setDescription(description);
        currentBadge.setImagePath(imagePath);

        if (serviceBadge.update(currentBadge)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Badge updated successfully.");
            // Optionally close the update window
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update badge in the database.");
        }
    }

    @FXML
    void cancel(MouseEvent event) {
        // Close the update window
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select New Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) nameField.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            String fileName = selectedImageFile.getName();
            Path destinationPath = Paths.get("images", fileName); // Adjust "images" directory as needed

            try {
                Files.createDirectories(Paths.get("images")); // Ensure directory exists
                Files.copy(selectedImageFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                imagePathField.setText(destinationPath.toString());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to copy image: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        assert nameField != null : "fx:id=\"nameField\" was not injected: check your FXML file 'update_badge_view.fxml'.";
        assert descriptionField != null : "fx:id=\"descriptionField\" was not injected: check your FXML file 'update_badge_view.fxml'.";
        assert imagePathField != null : "fx:id=\"imagePathField\" was not injected: check your FXML file 'update_badge_view.fxml'.";
    }
}