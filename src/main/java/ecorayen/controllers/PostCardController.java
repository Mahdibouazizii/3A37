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

    private post currentPost;
    private postservice postService;
    private boolean liked = false;
    private boolean disliked = false; //added

    public void initialize() {
        postService = new postservice();
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
