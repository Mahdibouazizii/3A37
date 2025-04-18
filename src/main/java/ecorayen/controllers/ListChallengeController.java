package ecorayen.controllers;

import ecorayen.models.challenge;
import ecorayen.services.ServiceChallenge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ListChallengeController {

    @FXML
    private TableView<challenge> challengeTable;

    @FXML
    private TableColumn<challenge, Integer> idColumn;

    @FXML
    private TableColumn<challenge, String> nameColumn;

    @FXML
    private TableColumn<challenge, String> descriptionColumn;

    @FXML
    private TableColumn<challenge, LocalDate> dateStartColumn;

    @FXML
    private TableColumn<challenge, LocalDate> dateEndColumn;

    @FXML
    private TableColumn<challenge, String> locationColumn;

    @FXML
    private TableColumn<challenge, String> imageColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button showDetailsButton;

    private final ServiceChallenge serviceChallenge = new ServiceChallenge();
    private final ObservableList<challenge> masterChallengeList = FXCollections.observableArrayList(); // Original list
    private final ObservableList<challenge> filteredChallengeList = FXCollections.observableArrayList(); // Filtered list

    @FXML
    public void initialize() {
        initializeTable();
        loadInitialChallenges();
        challengeTable.setItems(masterChallengeList);

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        showDetailsButton.setDisable(true);

        challengeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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
        dateStartColumn.setCellValueFactory(new PropertyValueFactory<>("date_start"));
        dateEndColumn.setCellValueFactory(new PropertyValueFactory<>("date_end"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
    }

    private void loadInitialChallenges() {
        masterChallengeList.clear();
        List<challenge> challenges = serviceChallenge.getAll();
        masterChallengeList.addAll(challenges);
        filteredChallengeList.addAll(masterChallengeList);
    }

    private void reloadChallenges() {
        masterChallengeList.clear();
        List<challenge> challenges = serviceChallenge.getAll();
        masterChallengeList.addAll(challenges);
        challengeTable.setItems(masterChallengeList);
    }

    @FXML
    void showAddChallengeInterface(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addnewchallengefixed.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Prevents user interaction with other windows
            stage.setTitle("Add New Challenge");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Ensures the Add Challenge window is closed before continuing
            reloadChallenges(); // Reload challenges after the Add Challenge window is closed
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load Add Challenge interface: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "FXML file not found for Add Challenge.");
        }
    }

    @FXML
    void showUpdateChallengeInterface(ActionEvent event) {
        challenge selectedChallenge = challengeTable.getSelectionModel().getSelectedItem();
        if (selectedChallenge == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a challenge to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updatechallengefixed.fxml"));
            Parent root = loader.load();
            UpdateChallengeController controller = loader.getController(); // Get the controller instance
            controller.initData(selectedChallenge); // Pass the selected challenge data
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Challenge");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            reloadChallenges();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load Update Challenge interface: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "FXML file not found for Update Challenge.");
        }
    }

    @FXML
    void deleteChallenge(ActionEvent event) {
        challenge selectedChallenge = challengeTable.getSelectionModel().getSelectedItem();
        if (selectedChallenge == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a challenge to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this challenge?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (serviceChallenge.delete(selectedChallenge)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Challenge deleted successfully.");
                    reloadChallenges();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete challenge.");
                }
            }
        });
    }

    @FXML
    void showChallengeDetails(ActionEvent event) {
        challenge selectedChallenge = challengeTable.getSelectionModel().getSelectedItem();
        if (selectedChallenge == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a challenge to show details.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/show challenge.fxml"));
            Parent root = loader.load();
            ShowChallengeController controller = loader.getController(); // Get the controller
            controller.initData(selectedChallenge); // Pass data to the controller
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Challenge Details");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load Challenge Details interface: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "FXML file not found for Show Challenge.");
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
