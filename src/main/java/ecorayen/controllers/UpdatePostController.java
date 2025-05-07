package ecorayen.controllers;

import ecorayen.models.post;
import ecorayen.services.postservice;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.image.ImageView; // Add this import
import javafx.scene.image.Image; // Add this import
import javafx.stage.FileChooser; // Add this import
import java.io.File; // Add this import
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.HBox; // Add this import
import javafx.geometry.Pos;  // Add this import


public class UpdatePostController {

    @FXML
    private TextField postIdField; // To identify the post to update

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
    private TextField likeCountField;

    @FXML
    private Button updateButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ImageView imageView; // Added for displaying the selected image

    @FXML
    private Button uploadButton; // Added button for uploading

    @FXML
    private HBox buttonBox;

    private postservice postService;
    private post currentPost; // To hold the post being updated
    private File selectedFile;

    @FXML
    public void initialize() {
        postService = new postservice();
        // Initially disable fields until a post is loaded
        captionField.setDisable(true);
        imageUrlField.setDisable(true);
        locationField.setDisable(true);
        postTypeField.setDisable(true);
        aspectRatioField.setDisable(true);
        likeCountField.setDisable(true);
        updateButton.setDisable(true);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
    }

    public void setPost(post post) {
        this.currentPost = post;
        if (post != null) {
            postIdField.setText(String.valueOf(post.getPost_id()));
            captionField.setText(post.getCaption());
            imageUrlField.setText(post.getImage_url());
            locationField.setText(post.getLocation());
            postTypeField.setText(post.getPost_type());
            if (post.getAspect_ratio() != null) {
                aspectRatioField.setText(String.valueOf(post.getAspect_ratio()));
            } else {
                aspectRatioField.setText("");
            }
            likeCountField.setText(String.valueOf(post.getPost_like_count()));

            // Enable fields once a post is loaded
            captionField.setDisable(false);
            imageUrlField.setDisable(false);
            locationField.setDisable(false);
            postTypeField.setDisable(false);
            aspectRatioField.setDisable(false);
            likeCountField.setDisable(false);
            updateButton.setDisable(false);

            //load the image
            if(post.getImage_url() != null && !post.getImage_url().isEmpty()){
                File f = new File(post.getImage_url());
                if(f.exists()){
                    Image image = new Image(f.toURI().toString());
                    imageView.setImage(image);
                }
                else{
                    System.out.println("File does not exist");
                }
            }

        }
    }

    @FXML
    private void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
            imageUrlField.setText(selectedFile.toURI().toString());
        }
    }


    @FXML
    private void handleUpdate() {
        if (currentPost != null) {
            currentPost.setCaption(captionField.getText());
            currentPost.setImage_url(imageUrlField.getText());
            currentPost.setLocation(locationField.getText());
            currentPost.setPost_type(postTypeField.getText());
            try {
                currentPost.setAspect_ratio(Double.parseDouble(aspectRatioField.getText()));
            } catch (NumberFormatException e) {
                currentPost.setAspect_ratio(null); // Or handle error appropriately
            }
            try {
                currentPost.setPost_like_count(Integer.parseInt(likeCountField.getText()));
            } catch (NumberFormatException e) {
                System.err.println("Invalid like count format.");
                return; // Don't update if like count is invalid
            }

            // Update the updated_at timestamp
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            currentPost.setUpdated_at(now.format(formatter));

            postService.update(currentPost);

            // Close the update post window
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        // Close the update post window
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
