package ecorayen.controllers;

import ecorayen.models.post;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ecorayen.services.postservice; // Import the postservice
import javafx.application.Platform;
// Import classes for electronic signature
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class PostCardController {

    @FXML
    private Label captionLabel;

    @FXML
    private ImageView postImageView;

    @FXML
    private Label locationLabel;

    @FXML
    private Label likeCountLabel;

    @FXML
    private Button viewDetailsButton;

    @FXML
    private Button likeButton;
    @FXML
    private Button dislikeButton;       // Added Dislike Button

    @FXML
    private Button signButton; // Added Electronic Signature Button

    @FXML
    private Label signatureLabel; // Added Label to display the signature

    private post currentPost;
    private postservice postService;
    private boolean liked = false;
    private boolean disliked = false; //added
    private String currentSignature = null; // To store the current signature

    public void initialize() {
        postService = new postservice();
        signatureLabel.setVisible(false); // Initially hide the signature label
    }

    public void setPost(post post) {
        this.currentPost = post;
        captionLabel.setText(post.getCaption());
        locationLabel.setText(post.getLocation());
        likeCountLabel.setText(String.valueOf(post.getPost_like_count()));

        try {
            Image image = new Image(post.getImage_url());
            postImageView.setImage(image);
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading image: " + post.getImage_url() + " - " + e.getMessage());
        }
        updateLikeButtonsState();
        displayExistingSignature(); // Check and display any existing signature
    }

    private void displayExistingSignature() {
        // In a real application, you would fetch the signature associated with the post
        // from your backend service here. For this example, we'll simulate
        // having a signature if the post ID is even.
        if (currentPost.getPost_id() % 2 == 0 && currentSignature == null) {
            currentSignature = "Simulated Signature for Post " + currentPost.getPost_id();
            signatureLabel.setText("Signed by: " + currentSignature);
            signatureLabel.setVisible(true);
            signButton.setDisable(true); // Disable sign button if already signed
        } else if (currentSignature != null) {
            signatureLabel.setText("Signed by: " + currentSignature);
            signatureLabel.setVisible(true);
            signButton.setDisable(true); // Disable sign button if already signed
        } else {
            signatureLabel.setVisible(false);
            signButton.setDisable(false);
        }
    }

    @FXML
    private void handleViewDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/show_post.fxml"));
            Scene scene = new Scene(loader.load());
            ShowPostController controller = loader.getController();
            controller.setPost(currentPost);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Post Details");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLike(ActionEvent event) {
        if (currentPost == null) return;

        if (!liked) {
            if(disliked){
                currentPost.setPost_like_count(currentPost.getPost_like_count() + 1);
                disliked = false;
            }
            currentPost.setPost_like_count(currentPost.getPost_like_count() + 1);
            liked = true;
            updatePostAndUI();
        }
    }

    @FXML
    private void handleDislike(ActionEvent event) {
        if (currentPost == null) return;

        if (!disliked) {
            if(liked){
                currentPost.setPost_like_count(currentPost.getPost_like_count() - 1);
                liked = false;
            }
            currentPost.setPost_like_count(currentPost.getPost_like_count() - 1);
            disliked = true;
            updatePostAndUI();
        }
    }

    @FXML
    private void handleSign(ActionEvent event) {
        if (currentPost == null) return;

        // Create a custom dialog for electronic signature input
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Electronic Signature");
        dialog.setHeaderText("Please enter your electronic signature:");

        // Set up the layout for the dialog
        VBox dialogPaneContent = new VBox();
        TextField signatureField = new TextField();
        signatureField.setPromptText("Your Signature");
        dialogPaneContent.getChildren().add(signatureField);
        dialogPaneContent.setPadding(new Insets(10, 10, 10, 10));

        dialog.getDialogPane().setContent(dialogPaneContent);

        // Custom button types
        ButtonType signButtonType = new ButtonType("Sign", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().setAll(signButtonType, cancelButtonType);

        // Get the result of the dialog
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == signButtonType) {
            String signature = signatureField.getText();
            if (signature != null && !signature.trim().isEmpty()) {
                // Here you would typically send the signature to a backend service
                // to associate it with the current post and retrieve it.
                System.out.println("Post ID: " + currentPost.getPost_id() + " signed with: " + signature);

                // For this example, we'll just store it locally and display it.
                currentSignature = signature;
                signatureLabel.setText("Signed by: " + currentSignature);
                signatureLabel.setVisible(true);
                signButton.setDisable(true);

                // Provide feedback to the user
                Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmationAlert.setTitle("Signature Successful");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("This post has been electronically signed.");
                confirmationAlert.showAndWait();

            } else {
                // Inform the user that the signature cannot be empty
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Signature Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Electronic signature cannot be empty.");
                errorAlert.showAndWait();
            }
        }
        // If the user clicks "Cancel", do nothing
    }

    private void updatePostAndUI() {
        postService.update(currentPost);

        Platform.runLater(() -> {
            likeCountLabel.setText(String.valueOf(currentPost.getPost_like_count()));
            updateLikeButtonsState();
        });
    }

    private void updateLikeButtonsState() {
        likeButton.setDisable(liked);
        dislikeButton.setDisable(disliked);
    }
}