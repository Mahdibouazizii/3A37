package ecorayen.controllers;

import ecorayen.models.Participation;
import ecorayen.services.ParticipationService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class ParticipationListController {

    @FXML private ListView<Participation> participationListView;
    @FXML private VBox participationCardContainer;
    @FXML private Button addParticipationButton;
    @FXML private Button showStatsButton;
    @FXML private RadioButton listViewRadioButton;
    @FXML private RadioButton cardViewRadioButton;
    @FXML private ToggleGroup viewToggleGroup;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private ComboBox<String> sortByComboBox;

    private final ParticipationService participationService = new ParticipationService();
    private ObservableList<Participation> allParticipations = FXCollections.observableArrayList();
    private ObservableList<Participation> filteredParticipations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadParticipations();
        setupListView();
        displayParticipationCards(); // Initial display as cards
        cardViewRadioButton.setSelected(true); // Set default view
        filteredParticipations.addAll(allParticipations); // Initially show all

        // Bind filtered list to the List View
        participationListView.setItems(filteredParticipations);

        // Add listeners for view switching
        listViewRadioButton.setOnAction(event -> {
            participationListView.setVisible(true);
            participationCardContainer.setVisible(false);
        });
        cardViewRadioButton.setOnAction(event -> {
            participationListView.setVisible(false);
            participationCardContainer.setVisible(true);
        });

        // Setup search functionality
        searchButton.setOnAction(event -> filterParticipations());
        searchTextField.setOnAction(event -> filterParticipations()); // Allow search on Enter

        // Setup sorting options
        sortByComboBox.setItems(FXCollections.observableArrayList("ID", "Challenge ID", "Score", "Date"));
        sortByComboBox.setValue("ID"); // Default sort
        sortByComboBox.setOnAction(event -> sortParticipations());
    }

    private void loadParticipations() {
        allParticipations.setAll(participationService.getAll());
        filteredParticipations.setAll(allParticipations); // Reset filtered list on reload
        displayParticipationCards();
    }

    private void setupListView() {
        participationListView.setCellFactory(param -> new ParticipationListCell(this::handleDeleteParticipation, this::handleUpdateParticipation));
    }

    private void displayParticipationCards() {
        participationCardContainer.getChildren().clear();
        for (Participation participation : filteredParticipations) { // Use filtered list
            VBox card = createParticipationCard(participation);
            participationCardContainer.getChildren().add(card);
        }
    }

    private VBox createParticipationCard(Participation participation) {
        VBox card = new VBox(10);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        Label idLabel = new Label("ID: " + participation.getId());
        Label challengeIdLabel = new Label("Challenge ID: " + participation.getChallengeId());
        Label scoreLabel = new Label("Score: " + String.format("%.2f", participation.getScore()));
        Label dateLabel = new Label("Date: " + participation.getParticipationDateTime().toString());
        Label detailsLabel = new Label("Details: " + (participation.getSubmissionDetails() == null ? "N/A" : participation.getSubmissionDetails()));
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> handleDeleteParticipation(participation));
        Button updateButton = new Button("Update");
        updateButton.setOnAction(event -> handleUpdateParticipation(participation));

        card.getChildren().addAll(idLabel, challengeIdLabel, scoreLabel, dateLabel, detailsLabel, updateButton, deleteButton);
        return card;
    }

    @FXML
    public void handleAddParticipationAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ecorayen/views/add_participation.fxml"));
            VBox page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Participation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addParticipationButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AddParticipationController controller = loader.getController();
            controller.setOnParticipationAdded(this::loadParticipations);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the Add Participation form.");
        }
    }

    private void handleUpdateParticipation(Participation participation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ecorayen/views/update_participation.fxml"));
            VBox page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Participation");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(
                    listViewRadioButton.isSelected() ? participationListView.getScene().getWindow() : updateButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            UpdateParticipationController controller = loader.getController();
            controller.setOnParticipationUpdated(this::loadParticipations);
            controller.initData(participation);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the Update Participation form.");
        }
    }

    private void handleDeleteParticipation(Participation participation) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Participation");
        confirm.setContentText("Are you sure you want to delete participation ID " + participation.getId() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (participationService.delete(participation.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Participation deleted successfully.");
                    loadParticipations();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete participation.");
                }
            }
        });
    }

    @FXML
    public void handleShowStatsAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ecorayen/views/stats_participation.fxml"));
            VBox page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Participation Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(showStatsButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            StatsParticipationController controller = loader.getController();
            controller.initialize(); // The stats controller will load the data

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load the Participation Statistics form.");
        }
    }

    @FXML
    private void filterParticipations() {
        String searchText = searchTextField.getText().toLowerCase();
        filteredParticipations.setAll(allParticipations.stream()
                .filter(p -> String.valueOf(p.getId()).contains(searchText) ||
                        String.valueOf(p.getChallengeId()).contains(searchText) ||
                        String.valueOf(p.getScore()).contains(searchText) ||
                        p.getParticipationDateTime().toString().toLowerCase().contains(searchText) ||
                        (p.getSubmissionDetails() != null && p.getSubmissionDetails().toLowerCase().contains(searchText)))
                .toList());
        displayParticipationCards(); // Refresh card view
    }

    private void sortParticipations() {
        String sortBy = sortByComboBox.getValue();
        switch (sortBy) {
            case "ID":
                filteredParticipations.sort(Comparator.comparingInt(Participation::getId));
                break;
            case "Challenge ID":
                filteredParticipations.sort(Comparator.comparingInt(Participation::getChallengeId));
                break;
            case "Score":
                filteredParticipations.sort(Comparator.comparingDouble(Participation::getScore));
                break;
            case "Date":
                filteredParticipations.sort(Comparator.comparing(Participation::getParticipationDateTime));
                break;
        }
        // No need to reload all, the ObservableList will update the ListView automatically
        if (cardViewRadioButton.isSelected()) {
            displayParticipationCards(); // Refresh card view if visible
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Inner class for the ListCell
    private static class ParticipationListCell extends ListCell<Participation> {
        private final Button deleteButton = new Button("Delete");
        private final Button updateButton = new Button("Update");
        private final HBox container = new HBox(5);
        private Participation currentItem;
        private final java.util.function.Consumer<Participation> onDelete;
        private final java.util.function.Consumer<Participation> onUpdate;

        public ParticipationListCell(java.util.function.Consumer<Participation> deleteHandler, java.util.function.Consumer<Participation> updateHandler) {
            this.onDelete = deleteHandler;
            this.onUpdate = updateHandler;
            deleteButton.setOnAction(event -> {
                if (currentItem != null) {
                    onDelete.accept(currentItem);
                }
            });
            updateButton.setOnAction(event -> {
                if (currentItem != null) {
                    onUpdate.accept(currentItem);
                }
            });
            container.getChildren().addAll(new Label(), updateButton, deleteButton);
        }

        @Override
        protected void updateItem(Participation item, boolean empty) {
            super.updateItem(item, empty);
            currentItem = item;
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(String.format("ID: %d, Challenge ID: %d, Score: %.2f, Date: %s",
                        item.getId(), item.getChallengeId(), item.getScore(), item.getParticipationDateTime().toString()));
                ((Label) container.getChildren().get(0)).setText("");
                setGraphic(container);
            }
        }
    }
}