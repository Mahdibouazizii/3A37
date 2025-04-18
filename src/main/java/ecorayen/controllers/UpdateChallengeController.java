package ecorayen.controllers;

import ecorayen.models.challenge;
import ecorayen.services.ServiceChallenge;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
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
import java.time.LocalDate;

public class UpdateChallengeController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dateStartPicker;

    @FXML
    private DatePicker dateEndPicker;

    @FXML
    private TextField locationField;

    @FXML
    private TextField imagePathField;

    private challenge currentChallenge;
    private final ServiceChallenge serviceChallenge = new ServiceChallenge();
    private File selectedImageFile;

    public void initData(challenge challenge) {
        this.currentChallenge = challenge;
        if (challenge != null) {
            nameField.setText(challenge.getName());
            descriptionField.setText(challenge.getDescription());
            dateStartPicker.setValue(challenge.getDate_start());
            dateEndPicker.setValue(challenge.getDate_end());
            locationField.setText(challenge.getLocation());
            imagePathField.setText(challenge.getImage());
        }
    }

    @FXML
    void update(MouseEvent event) {
        if (currentChallenge == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No challenge selected for update.");
            return;
        }

        String name = nameField.getText();
        String description = descriptionField.getText();
        LocalDate dateStart = dateStartPicker.getValue();
        LocalDate dateEnd = dateEndPicker.getValue();
        String location = locationField.getText();
        String imagePath = imagePathField.getText();

        if (name.isEmpty() || description.isEmpty() || dateStart == null || dateEnd == null || location.isEmpty() || imagePath.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        currentChallenge.setName(name);
        currentChallenge.setDescription(description);
        currentChallenge.setDate_start(dateStart);
        currentChallenge.setDate_end(dateEnd);
        currentChallenge.setLocation(location);
        currentChallenge.setImage(imagePath);

        if (serviceChallenge.update(currentChallenge)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Challenge updated successfully.");
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update challenge in the database.");
        }
    }

    @FXML
    void cancel(MouseEvent event) {
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
        assert nameField != null : "fx:id=\"nameField\" was not injected: check your FXML file 'updatechallengefixed.fxml'.";
        assert descriptionField != null : "fx:id=\"descriptionField\" was not injected: check your FXML file 'updatechallengefixed.fxml'.";
        assert dateStartPicker != null : "fx:id=\"dateStartPicker\" was not injected: check your FXML file 'updatechallengefixed.fxml'.";
        assert dateEndPicker != null : "fx:id=\"dateEndPicker\" was not injected: check your FXML file 'updatechallengefixed.fxml'.";
        assert locationField != null : "fx:id=\"locationField\" was not injected: check your FXML file 'updatechallengefixed.fxml'.";
        assert imagePathField != null : "fx:id=\"imagePathField\" was not injected: check your FXML file 'updatechallengefixed.fxml'.";
    }
}