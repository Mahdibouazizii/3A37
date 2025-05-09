package ecorayen.controllers;

import ecorayen.models.Participation;
import ecorayen.services.ParticipationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ParticipationListController {

    @FXML
    private ListView<Participation> participationListView;
    @FXML
    private VBox participationCardContainer;
    @FXML
    private Button addParticipationButton;
    @FXML
    private Button showStatsButton;
    @FXML
    private RadioButton listViewRadioButton;
    @FXML
    private RadioButton cardViewRadioButton;
    @FXML
    private ToggleGroup viewToggleGroup;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<String> sortByComboBox;

    // Leaderboard UI Elements
    @FXML
    private VBox leaderboardContainer; // Container for the leaderboard
    @FXML
    private TableView<Participation> leaderboardTableView;
    @FXML
    private TableColumn<Participation, Integer> rankColumn;
    @FXML
    private TableColumn<Participation, Integer> userIdColumn; // Assuming Participation has a userId
    @FXML
    private TableColumn<Participation, Double> scoreColumn;

    private final ParticipationService participationService = new ParticipationService();
    private final ObservableList<Participation> allParticipations = FXCollections.observableArrayList();
    private final ObservableList<Participation> filteredParticipations = FXCollections.observableArrayList();
    private final ObservableList<Participation> leaderboardData = FXCollections.observableArrayList();

    //  Use Consumer for better type safety
    private final Consumer<Participation> onDeleteParticipation = this::handleDeleteParticipation;
    private final Consumer<Participation> onUpdateParticipation = this::handleUpdateParticipation;

    @FXML
    public void initialize() {
        loadParticipations();
        setupListView();
        setupCardView();
        setupLeaderboard(); // Initialize the leaderboard
        updateLeaderboard(); // Populate the leaderboard data

        cardViewRadioButton.setSelected(true);
        filteredParticipations.addAll(allParticipations);
        participationListView.setItems(filteredParticipations);

        listViewRadioButton.setOnAction(event -> {
            participationListView.setVisible(true);
            participationCardContainer.setVisible(false);
            leaderboardContainer.setVisible(false); // Hide leaderboard in list view
        });
        cardViewRadioButton.setOnAction(event -> {
            participationListView.setVisible(false);
            participationCardContainer.setVisible(true);
            leaderboardContainer.setVisible(true); // Show leaderboard in card view
        });

        searchButton.setOnAction(event -> filterParticipations());
        searchTextField.setOnAction(event -> filterParticipations());

        sortByComboBox.setItems(FXCollections.observableArrayList("ID", "Challenge ID", "Score", "Date"));
        sortByComboBox.setValue("ID");
        sortByComboBox.setOnAction(event -> sortParticipations());
    }

    private void loadParticipations() {
        allParticipations.setAll(participationService.getAll());
        filteredParticipations.setAll(allParticipations);
        updateLeaderboard(); // Update leaderboard when participations are loaded
    }

    private void setupListView() {
        participationListView.setCellFactory(param -> new ParticipationListCell(onDeleteParticipation, onUpdateParticipation));
    }

    private void setupCardView() {
        participationCardContainer.getChildren().clear();
        displayParticipationCards();
    }

    private void displayParticipationCards() {
        participationCardContainer.getChildren().clear();
        for (Participation participation : filteredParticipations) {
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
        deleteButton.setOnAction(event -> onDeleteParticipation.accept(participation));
        Button updateButton = new Button("Update");
        updateButton.setOnAction(event -> onUpdateParticipation.accept(participation));

        card.getChildren().addAll(idLabel, challengeIdLabel, scoreLabel, dateLabel, detailsLabel, updateButton, deleteButton);
        return card;
    }

    private void setupLeaderboard() {
        if (leaderboardTableView != null) {
            rankColumn.setCellValueFactory(new PropertyValueFactory<>("id")); // Placeholder - adjust based on how you rank
            userIdColumn.setCellValueFactory(new PropertyValueFactory<>("challengeId")); // Placeholder - adjust based on your User ID field
            scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
            leaderboardTableView.setItems(leaderboardData);
        }
    }

    private void updateLeaderboard() {
        // Sort participations by score in descending order
        List<Participation> sortedParticipations = allParticipations.stream()
                .sorted(Comparator.comparingDouble(Participation::getScore).reversed())
                .collect(Collectors.toList());

        // For a basic leaderboard, we can just display the top participations
        leaderboardData.setAll(sortedParticipations);

        // If you need to display a rank, you might need to process the sorted list
        // and add a rank property (either in the model or just for display).
        // Example of adding a simple rank:
        for (int i = 0; i < leaderboardData.size(); i++) {
            // Note: You might need to create a wrapper class if Participation doesn't have a rank property
            // For simplicity, we're just using the index + 1 here.
            // If you have ties in scores, the ranking logic would be more complex.
            final int rank = i + 1;
            // This assumes you have a way to set a temporary rank for display
            // leaderboardData.get(i).setTemporaryRank(rank);
        }
        // If you added a temporary rank, you'd need to adjust the PropertyValueFactory for the rankColumn
        // rankColumn.setCellValueFactory(new PropertyValueFactory<>("temporaryRank"));
    }

    @FXML
    private void handleAddParticipationAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add_participation.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/update_participation.fxml"));
            VBox page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Participation");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            // Determine the owner stage based on the visible view
            Stage ownerStage = null;
            if (listViewRadioButton.isSelected() && participationListView.getSelectionModel().getSelectedItem() == participation) {
                ownerStage = (Stage) participationListView.getScene().getWindow();
            } else if (cardViewRadioButton.isSelected() && participationCardContainer.isVisible()) {
                ownerStage = (Stage) participationCardContainer.getScene().getWindow();
            } else {
                ownerStage = (Stage) addParticipationButton.getScene().getWindow(); // Fallback
            }
            dialogStage.initOwner(ownerStage);

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
    private void handleShowStatsAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/stats_participation.fxml"));
            BorderPane page = loader.load(); // Changed TabPane to BorderPane
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Participation Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(showStatsButton.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Access the TabPane within the BorderPane
            TabPane statsTabPane = (TabPane) page.getCenter();
            StatsParticipationController controller = loader.getController();
            // You might need to pass the TabPane to the controller if it needs direct access
            // controller.setStatsTabPane(statsTabPane);
            controller.initialize();

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
        displayParticipationCards();
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
        if (cardViewRadioButton.isSelected()) {
            displayParticipationCards();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class ParticipationListCell extends ListCell<Participation> {
        private final Button deleteButton = new Button("Delete");
        private final Button updateButton = new Button("Update");
        private final HBox container = new HBox(5);
        private Participation currentItem;
        private final Consumer<Participation> onDelete;
        private final Consumer<Participation> onUpdate;

        public ParticipationListCell(Consumer<Participation> deleteHandler, Consumer<Participation> updateHandler) {
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