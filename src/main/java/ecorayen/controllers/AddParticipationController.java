package ecorayen.controllers;

import ecorayen.models.challenge;
import ecorayen.models.Participation;
import ecorayen.services.ParticipationService;
import ecorayen.services.ServiceChallenge;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.List;

public class AddParticipationController {

    @FXML private ComboBox<challenge> challengeComboBox;
    @FXML private TextField scoreTextField;
    @FXML private TextArea submissionDetailsTextArea;

    private final ParticipationService participationService = new ParticipationService();
    private final ServiceChallenge challengeService = new ServiceChallenge();
    private Runnable onParticipationAdded;

    public void setOnParticipationAdded(Runnable callback) {
        this.onParticipationAdded = callback;
        loadChallenges();
    }

    @FXML
    public void initialize() {
        // Challenges are loaded when setOnParticipationAdded is called
    }

    private void loadChallenges() {
        List<challenge> challenges = challengeService.getAll();
        challengeComboBox.setItems(FXCollections.observableArrayList(challenges));
        challengeComboBox.setCellFactory(new Callback<ListView<challenge>, ListCell<challenge>>() {
            @Override
            public ListCell<challenge> call(ListView<challenge> param) {
                return new ListCell<challenge>() {
                    @Override
                    protected void updateItem(challenge item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        });
        challengeComboBox.setButtonCell(new ListCell<challenge>() {
            @Override
            protected void updateItem(challenge item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select challenge");
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    @FXML
    public void handleAddParticipation() {
        challenge selectedChallenge = challengeComboBox.getValue();
        String scoreText = scoreTextField.getText();
        String submissionDetails = submissionDetailsTextArea.getText();

        if (selectedChallenge == null || scoreText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please select a challenge and enter a score.");
            return;
        }

        try {
            double score = Double.parseDouble(scoreText);
            Participation newParticipation = new Participation();
            newParticipation.setChallengeId(selectedChallenge.getId());
            newParticipation.setUserId(1); // Default user ID for now
            newParticipation.setParticipationDateTime(LocalDateTime.now());
            newParticipation.setScore(score);
            newParticipation.setSubmissionDetails(submissionDetails);

            if (participationService.add(newParticipation)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Participation added successfully.");
                clearInputFields();
                if (onParticipationAdded != null) {
                    onParticipationAdded.run(); // Notify the main controller to reload data
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add participation.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid score.");
        }
    }

    private void clearInputFields() {
        challengeComboBox.setValue(null);
        scoreTextField.clear();
        submissionDetailsTextArea.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}