package ecorayen.controllers;

import ecorayen.models.challenge;
import ecorayen.models.Participation;
import ecorayen.services.ParticipationService;
import ecorayen.services.ServiceChallenge;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.util.List;

public class UpdateParticipationController {

    @FXML private TextField idTextField;
    @FXML private ComboBox<challenge> challengeComboBox;
    @FXML private TextField scoreTextField;
    @FXML private TextArea submissionDetailsTextArea;

    private final ParticipationService participationService = new ParticipationService();
    private final ServiceChallenge challengeService = new ServiceChallenge();
    private Participation currentParticipation;
    private List<challenge> availableChallenges;
    private Runnable onParticipationUpdated;

    public void setOnParticipationUpdated(Runnable callback) {
        this.onParticipationUpdated = callback;
        loadChallenges();
    }

    public void initData(Participation participation) {
        if (participation != null) {
            this.currentParticipation = participation;
            idTextField.setText(String.valueOf(participation.getId()));
            // Find and select the correct challenge in the combo box from the loaded list
            if (availableChallenges != null) {
                availableChallenges.stream()
                        .filter(challenge -> challenge.getId() == participation.getChallengeId())
                        .findFirst()
                        .ifPresent(challengeComboBox::setValue);
            }
            scoreTextField.setText(String.valueOf(participation.getScore()));
            submissionDetailsTextArea.setText(participation.getSubmissionDetails());
        }
    }

    @FXML
    public void initialize() {
        // Challenges are loaded when setOnParticipationUpdated is called
    }

    private void loadChallenges() {
        availableChallenges = challengeService.getAll();
        challengeComboBox.setItems(FXCollections.observableArrayList(availableChallenges));
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
    public void handleUpdateParticipation() {
        if (currentParticipation == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No participation selected for update.");
            return;
        }

        challenge selectedChallenge = challengeComboBox.getValue();
        String scoreText = scoreTextField.getText();
        String submissionDetails = submissionDetailsTextArea.getText();

        if (selectedChallenge == null || scoreText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please select a challenge and enter a score.");
            return;
        }

        try {
            double score = Double.parseDouble(scoreText);
            currentParticipation.setChallengeId(selectedChallenge.getId());
            currentParticipation.setScore(score);
            currentParticipation.setSubmissionDetails(submissionDetails);

            if (participationService.update(currentParticipation)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Participation updated successfully.");
                if (onParticipationUpdated != null) {
                    onParticipationUpdated.run(); // Notify the main controller to reload data
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update participation.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid score.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}