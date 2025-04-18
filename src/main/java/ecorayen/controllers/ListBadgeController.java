package ecorayen.controllers;

import ecorayen.models.badge;
import ecorayen.services.ServiceBadge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ListBadgeController {

    @FXML
    private TableView<badge> badgeTable;

    @FXML
    private TableColumn<badge, Integer> idColumn;

    @FXML
    private TableColumn<badge, String> nameColumn;

    @FXML
    private TableColumn<badge, String> descriptionColumn;

    @FXML
    private TableColumn<badge, String> imagePathColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button showDetailsButton; // New button

    private final ServiceBadge serviceBadge = new ServiceBadge();
    private final ObservableList<badge> badgeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initializeTable();
        loadBadges();

        // Disable update, delete, and showDetails buttons initially
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        showDetailsButton.setDisable(true);

        // Enable/disable buttons based on table selection
        badgeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                showDetailsButton.setDisable(false);
            } else {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                showDetailsButton.setDisable(true);
            }
        });
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        imagePathColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        badgeTable.setItems(badgeList);
    }

    private void loadBadges() {
        badgeList.clear();
        List<badge> badges = serviceBadge.getAll();
        badgeList.addAll(badges);
    }

    @FXML
    void showAddBadgeInterface(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBadgeView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Badge");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadBadges(); // Reload the list after adding
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load Add Badge interface.");
        }
    }

    @FXML
    void showUpdateBadgeInterface(ActionEvent event) {
        badge selectedBadge = badgeTable.getSelectionModel().getSelectedItem();
        if (selectedBadge == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a badge to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateBadgeView.fxml"));
            Parent root = loader.load();
            UpdateBadgeController controller = loader.getController();
            controller.initData(selectedBadge); // Pass the selected badge to the update controller
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Badge");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadBadges(); // Reload the list after updating
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load Update Badge interface.");
        }
    }

    @FXML
    void deleteBadge(ActionEvent event) {
        badge selectedBadge = badgeTable.getSelectionModel().getSelectedItem();
        if (selectedBadge == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a badge to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this badge?");
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                if (serviceBadge.delete(selectedBadge)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Badge deleted successfully.");
                    loadBadges(); // Reload the list after deletion
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete badge.");
                }
            }
        });
    }

    @FXML
    void showBadgeDetails(ActionEvent event) {
        badge selectedBadge = badgeTable.getSelectionModel().getSelectedItem();
        if (selectedBadge == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a badge to show details.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ShowBadgeView.fxml"));
            Parent root = loader.load();
            ShowBadgeController controller = loader.getController();
            controller.initData(selectedBadge); // Pass the selected badge to the show details controller
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Badge Details");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load Badge Details interface.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}