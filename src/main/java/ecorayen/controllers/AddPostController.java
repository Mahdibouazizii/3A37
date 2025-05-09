package ecorayen.controllers;

import ecorayen.models.post;
import ecorayen.services.postservice;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

public class  AddPostController {

    @FXML
    private TextField captionField;

    @FXML
    private TextField imageUrlField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField postTypeField;

    @FXML
    private TextField aspectRatioField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ImageView imageView; // Added for displaying the selected image

    @FXML
    private Button uploadButton; // Added button for uploading

    @FXML
    private HBox buttonBox;

    private postservice postService;
    private File selectedFile; // Store the selected image file

    @FXML
    public void initialize() {
        postService = new postservice();
        imageView.setFitWidth(100); // set the image view dimension
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
    }

    @FXML
    private void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Image");
        // Set the initial directory, for example to the user's home directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        // Add file type filters if needed
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedFile = fileChooser.showOpenDialog(null); // Pass null to get the current window

        if (selectedFile != null) {
            // Load the image and display it in the ImageView
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
            imageUrlField.setText(selectedFile.toURI().toString()); // Set the file path to the image url field
        }
    }

    @FXML
    private void handleSave() {
        String caption = captionField.getText();
        String imageUrl = imageUrlField.getText();
        String location = locationField.getText();
        String postType = postTypeField.getText();
        Double aspectRatio = null;
        try {
            aspectRatio = Double.parseDouble(aspectRatioField.getText());
        } catch (NumberFormatException e) {
            // Handle the case where the aspect ratio is not a valid number
            System.err.println("Invalid aspect ratio format.");
        }

        // Get current timestamp for created_at and updated_at
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);

        post newPost = new post(caption, imageUrl, location, timestamp, timestamp, postType, aspectRatio);
        postService.add(newPost);

        // Close the add post window
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        // Close the add post window
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
