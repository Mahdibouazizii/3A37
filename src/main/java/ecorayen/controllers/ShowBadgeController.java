package ecorayen.controllers;

import ecorayen.models.badge;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class ShowBadgeController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ImageView badgeImageView;

    private badge currentBadge;

    public void initData(badge badge) {
        this.currentBadge = badge;
        if (badge != null) {
            nameLabel.setText("Name: " + badge.getName());
            descriptionLabel.setText("Description: " + badge.getDescription());
            String imagePath = badge.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    try {
                        Image image = new Image(imageFile.toURI().toString());
                        badgeImageView.setImage(image);
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
                        // Optionally set a default image or clear the ImageView
                        badgeImageView.setImage(null);
                    }
                } else {
                    System.err.println("Image file not found: " + imagePath);
                    // Optionally set a default image or clear the ImageView
                    badgeImageView.setImage(null);
                }
            } else {
                // No image path available
                badgeImageView.setImage(null); // Or set a default "no image" image
            }
        }
    }

    @FXML
    public void initialize() {
        assert nameLabel != null : "fx:id=\"nameLabel\" was not injected: check your FXML file 'show_badge_view.fxml'.";
        assert descriptionLabel != null : "fx:id=\"descriptionLabel\" was not injected: check your FXML file 'show_badge_view.fxml'.";
        assert badgeImageView != null : "fx:id=\"badgeImageView\" was not injected: check your FXML file 'show_badge_view.fxml'.";
        // Any additional initialization logic can go here
    }

    public void handlePrint(ActionEvent actionEvent) {
    }

    public void handleEdit(ActionEvent actionEvent) {
    }

    public void handleDelete(ActionEvent actionEvent) {
    }

    public void handleShare(ActionEvent actionEvent) {
    }
}