package ecorayen.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class MainDashboardController {

    @FXML private TabPane mainTabPane;

    // Main Entity Tabs
    @FXML private Tab challengesTab;
    @FXML private Tab postsTab;
    @FXML private Tab participationsTab;
    @FXML private Tab badgesTab;

    // Stats Tab
    @FXML private Tab statisticsTab;

    @FXML
    public void initialize() {
        try {
            // Load Challenge Management View
            AnchorPane challengeView = FXMLLoader.load(getClass().getResource("/views/ListChallengeView.fxml"));
            challengesTab.setContent(challengeView);

            // Load Post Management View
            AnchorPane postView = FXMLLoader.load(getClass().getResource("/views/display_posts.fxml"));
            postsTab.setContent(postView);

            // Load Participation Management View
            AnchorPane participationView = FXMLLoader.load(getClass().getResource("/views/participation_list_view.fxml"));
            participationsTab.setContent(participationView);

            // Load Badge Management View
            AnchorPane badgeView = FXMLLoader.load(getClass().getResource("/views/ListBadgeView.fxml"));
            badgesTab.setContent(badgeView);

            // Load Statistics View
            AnchorPane statsView = FXMLLoader.load(getClass().getResource("/views/stats_participation.fxml"));
            statisticsTab.setContent(statsView);

            System.out.println("Dashboard initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing dashboard: " + e.getMessage());
            e.printStackTrace();

            // Show an alert to the user indicating initialization failure
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Initialization Error");
            alert.setHeaderText("Failed to initialize the dashboard");
            alert.setContentText("An error occurred during the loading of dashboard components: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Navigation methods for CRUD operations
    public void showAddChallenge() {
        loadViewInDialog("/views/addnewchallenge.fxml", "Add New Challenge");
    }

    public void showEditChallenge(int challengeId) {
        // Implementation would load updatedhallengefixed.fxml with data
    }

    public void showChallengeDetails(int challengeId) {
        loadViewInDialog("/views/showchallenge.fxml", "Challenge Details");
    }

    public void showChallengeStats(int challengeId) {
        loadViewInDialog("/views/challenge stats.fxml", "Challenge Statistics");
    }

    // Similar methods for Posts, Participations, and Badges...

    private void loadViewInDialog(String fxmlPath, String title) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource(fxmlPath));
            // Create and show dialog with this content
        } catch (IOException e) {
            e.printStackTrace();
            // Consider showing an alert here as well
        }
    }
}