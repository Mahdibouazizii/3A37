package ecorayen.controllers;

import ecorayen.models.challenge;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class ShowChallengeController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label dateStartLabel;

    @FXML
    private Label dateEndLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private ImageView challengeImageView;

    private challenge currentChallenge;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void initData(challenge challenge) {
        this.currentChallenge = challenge;
        if (challenge != null) {
            nameLabel.setText("Name: " + challenge.getName());
            descriptionLabel.setText("Description: " + challenge.getDescription());
            dateStartLabel.setText("Start Date: " + challenge.getDate_start().format(dateFormatter));
            dateEndLabel.setText("End Date: " + challenge.getDate_end().format(dateFormatter));
            locationLabel.setText("Location: " + challenge.getLocation());
            String imagePath = challenge.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    try {
                        Image image = new Image(imageFile.toURI().toString());
                        challengeImageView.setImage(image);
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
                        challengeImageView.setImage(null);
                    }
                } else {
                    System.err.println("Image file not found: " + imagePath);
                    challengeImageView.setImage(null);
                }
            } else {
                challengeImageView.setImage(null);
            }
        }
    }

    @FXML
    public void initialize() {
        assert nameLabel != null : "fx:id=\"nameLabel\" was not injected: check your FXML file 'show challenge.fxml'.";
        assert descriptionLabel != null : "fx:id=\"descriptionLabel\" was not injected: check your FXML file 'show challenge.fxml'.";
        assert dateStartLabel != null : "fx:id=\"dateStartLabel\" was not injected: check your FXML file 'show challenge.fxml'.";
        assert dateEndLabel != null : "fx:id=\"dateEndLabel\" was not injected: check your FXML file 'show challenge.fxml'.";
        assert locationLabel != null : "fx:id=\"locationLabel\" was not injected: check your FXML file 'show challenge.fxml'.";
        assert challengeImageView != null : "fx:id=\"challengeImageView\" was not injected: check your FXML file 'show challenge.fxml'.";
    }
}