package ecorayen.controllers;

import ecorayen.models.challenge;
import ecorayen.services.ServiceChallenge;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddChallengeController implements Initializable {

    private final ServiceChallenge serviceChallenge = new ServiceChallenge();

    @FXML
    private TextField Tname; // Corrected fx:id to Tname

    @FXML
    private TextField Tdescription;  // Corrected fx:id to Tdescription

    @FXML
    private DatePicker dateStart; // Corrected fx:id to dateStart

    @FXML
    private DatePicker Tdate_end; // Corrected fx:id to Tdate_end

    @FXML
    private TextField Tlocation;  // Corrected fx:id to Tlocation

    @FXML
    private TextField Timage; // Corrected fx:id to Timage

    @FXML
    private Button confirm;

    @FXML
    private Button cancelButton;

    @FXML
    private Button browseButton; // Added fx:id for the new Browse Button


    private File selectedImageFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //  Initialization logic if needed.  For  example, setting date constraints
        dateStart.setValue(LocalDate.now()); //set initial value
        Tdate_end.setValue(LocalDate.now().plusDays(7));
    }

    @FXML
    void add(ActionEvent event) { // Changed MouseEvent to ActionEvent for Button
        String name = Tname.getText();
        String description = Tdescription.getText();
        LocalDate startDate = dateStart.getValue();
        LocalDate endDate = Tdate_end.getValue();
        String imagePath = Timage.getText();
        String location = Tlocation.getText();

        if (name == null || name.isEmpty() ||
                description == null || description.isEmpty() ||
                startDate == null ||
                endDate == null ||
                imagePath == null || imagePath.isEmpty() ||
                location == null || location.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all the fields.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            showAlert(Alert.AlertType.ERROR, "Error", "End date cannot be before Start date");
            return;
        }

        challenge newChallenge = new challenge(name, description, startDate, endDate, location, imagePath);
        boolean isAdded = serviceChallenge.add(newChallenge);

        if (isAdded) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Challenge added to the database.");
            clearForm();

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add challenge to the database.");
        }
    }

    @FXML
    void cancel(ActionEvent event) { // Changed MouseEvent to ActionEvent for Button
        //  Add your logic for canceling the challenge creation here
        System.out.println("Cancel button clicked");
        Stage stage = (Stage) cancelButton.getScene().getWindow(); //get stage from cancelButton
        if (stage != null) {
            stage.close();
        }

    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearForm() {
        Tname.clear();
        Tdescription.clear();
        dateStart.setValue(null);
        Tdate_end.setValue(null);
        Timage.clear();
        Tlocation.clear();
    }

    @FXML
    private void handleBrowseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) browseButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            String fileName = selectedImageFile.getName();
            Path destinationPath = Paths.get("images", fileName); //  "images" directory

            try {
                Files.createDirectories(Paths.get("images")); // Ensure the directory exists
                Files.copy(selectedImageFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                Timage.setText(destinationPath.toString()); // Update the TextField
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to copy image: " + e.getMessage());
                e.printStackTrace(); //important
            }
        }
    }

}
