package ecorayen.controllers;

import ecorayen.models.challenge;
import ecorayen.services.ServiceChallenge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ListChallengeController {

    @FXML
    private FlowPane cardContainer;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button showDetailsButton;

    private challenge challengeTableSelection;
    private final ServiceChallenge serviceChallenge = new ServiceChallenge();
    private final ObservableList<challenge> masterChallengeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            loadInitialChallenges();
            setButtonState(true); // Disable initially
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "Failed to initialize: " + e.getMessage());
        }
    }

    private void setButtonState(boolean disable) {
        updateButton.setDisable(disable);
        deleteButton.setDisable(disable);
        showDetailsButton.setDisable(disable);
    }

    private void loadInitialChallenges() {
        try {
            masterChallengeList.clear();
            List<challenge> challenges = serviceChallenge.getAll();
            masterChallengeList.addAll(challenges);
            displayCards();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Failed to load challenges: " + e.getMessage());
        }
    }

    private void reloadChallenges() {
        try {
            masterChallengeList.clear();
            List<challenge> challenges = serviceChallenge.getAll();
            masterChallengeList.addAll(challenges);
            displayCards();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Reload Error", "Failed to reload challenges: " + e.getMessage());
        }
    }

    private void displayCards() {
        try {
            cardContainer.getChildren().clear();
            for (challenge ch : masterChallengeList) {
                VBox card = createChallengeCard(ch);
                cardContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Display Error", "Failed to display cards: " + e.getMessage());
        }
    }

    private VBox createChallengeCard(challenge ch) {
        VBox card = new VBox(10);
        // Using inline styles instead of styleClass
        card.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10;");
        card.setPrefWidth(250);
        card.setPadding(new Insets(10));

        ImageView challengeImage = createImageView(ch);
        Text challengeName = createNameText(ch);
        Text challengeLocation = createLocationText(ch);

        card.getChildren().addAll(challengeImage, challengeName, challengeLocation);

        card.setOnMouseClicked(e -> handleCardSelection(card, ch));

        return card;
    }

    private ImageView createImageView(challenge ch) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(250);
        imageView.setPreserveRatio(true);

        String imagePath = ch.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    imageView.setImage(new Image(imageFile.toURI().toString()));
                }
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
        return imageView;
    }

    private Text createNameText(challenge ch) {
        Text text = new Text(ch.getName());
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        return text;
    }

    private Text createLocationText(challenge ch) {
        Text text = new Text(ch.getLocation());
        text.setStyle("-fx-font-size: 12; -fx-fill: gray;");
        return text;
    }

    private void handleCardSelection(VBox card, challenge ch) {
        cardContainer.getChildren().forEach(node ->
                node.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10;")
        );
        card.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196f3; -fx-border-radius: 5; -fx-padding: 10;");
        challengeTableSelection = ch;
        setButtonState(false);
    }

    @FXML
    void showAddChallengeInterface(ActionEvent event) {
        try {
            showModalWindow("/views/addnewchallenge.fxml", "Add New Challenge", null);
            reloadChallenges();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open add challenge window: " + e.getMessage());
        }
    }

    @FXML
    void showUpdateChallengeInterface(ActionEvent event) {
        try {
            if (challengeTableSelection != null) {
                showModalWindow("/views/updatechallengefixed.fxml", "Update Challenge", challengeTableSelection);
                reloadChallenges();
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a challenge to update.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open update window: " + e.getMessage());
        }
    }

    @FXML
    void deleteChallenge(ActionEvent event) {
        try {
            if (challengeTableSelection == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a challenge to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this challenge?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    boolean deleted = serviceChallenge.delete(challengeTableSelection.getId());
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Challenge deleted successfully.");
                        reloadChallenges();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the challenge.");
                    }
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete challenge: " + e.getMessage());
        }
    }

    @FXML
    void showChallengeDetails(ActionEvent event) {
        try {
            if (challengeTableSelection != null) {
                showModalWindow("/views/showchallenge.fxml", "Challenge Details", challengeTableSelection);
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a challenge to show details.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to show details: " + e.getMessage());
        }
    }

    private void showModalWindow(String fxmlPath, String title, challenge selectedChallenge) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        // Set the builder factory to handle potential version mismatches
        loader.setBuilderFactory(new javafx.fxml.JavaFXBuilderFactory());

        Parent root = loader.load();

        // Initialize controller if needed
        Object controller = loader.getController();
        if (controller instanceof ShowChallengeController && selectedChallenge != null) {
            ((ShowChallengeController) controller).initData(selectedChallenge);
        } else if (controller instanceof UpdateChallengeController && selectedChallenge != null) {
            ((UpdateChallengeController) controller).initData(selectedChallenge);
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}