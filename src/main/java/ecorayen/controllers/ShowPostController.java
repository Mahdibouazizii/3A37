package ecorayen.controllers;

import ecorayen.models.post;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ShowPostController {

    @FXML
    private Label captionLabel;

    @FXML
    private ImageView postImageView;

    @FXML
    private Label locationLabel;

    @FXML
    private Label likeCountLabel;

    @FXML
    private Label createdAtLabel;

    @FXML
    private Label updatedAtLabel;

    private post currentPost;

    public void setPost(post post) {
        this.currentPost = post;
        captionLabel.setText(post.getCaption());
        locationLabel.setText(post.getLocation());
        likeCountLabel.setText(String.valueOf(post.getPost_like_count()));
        createdAtLabel.setText(post.getCreated_at());
        updatedAtLabel.setText(post.getUpdated_at());

        // Load the image from the URL (you might need error handling)
        try {
            Image image = new Image(post.getImage_url());
            postImageView.setImage(image);
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading image: " + post.getImage_url() + " - " + e.getMessage());
            // Optionally set a default image
        }
    }
}

