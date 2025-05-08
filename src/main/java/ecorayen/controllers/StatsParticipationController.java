package ecorayen.controllers;

import ecorayen.models.Participation;
import ecorayen.services.ParticipationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsParticipationController {

    @FXML private TabPane statsTabPane;
    @FXML private Tab overallStatsTab;
    @FXML private Label totalParticipationsLabel;
    @FXML private Label averageScoreLabel;
    @FXML private Tab scoreDistributionTab;
    @FXML private BarChart<String, Number> scoreDistributionChart;
    @FXML private CategoryAxis scoreCategoryAxis;
    @FXML private NumberAxis scoreNumberAxis;
    @FXML private Tab participationsByChallengeTab;
    @FXML private ComboBox<Integer> challengeFilterComboBox;
    @FXML private BarChart<String, Number> participationsByChallengeChart;
    @FXML private CategoryAxis challengeCategoryAxis;
    @FXML private NumberAxis challengeNumberAxis;

    private final ParticipationService participationService = new ParticipationService();
    private List<Participation> allParticipations;

    @FXML
    public void initialize() {
        loadParticipations();
        displayOverallStats(allParticipations);
        displayScoreDistribution(allParticipations);
        populateChallengeFilter();
        setupChallengeFilterListener();
    }

    private void loadParticipations() {
        allParticipations = participationService.getAll();
    }

    private void displayOverallStats(List<Participation> participations) {
        int totalParticipations = participations.size();
        double averageScore = participations.stream().mapToDouble(Participation::getScore).average().orElse(0.0);
        double highestScore = participations.stream().mapToDouble(Participation::getScore).max().orElse(0.0);
        double lowestScore = participations.stream().mapToDouble(Participation::getScore).min().orElse(0.0);

        totalParticipationsLabel.setText("Total Participations: " + totalParticipations);
        averageScoreLabel.setText("Average Score: " + String.format("%.2f", averageScore));

        Label highestScoreLabel = new Label("Highest Score: " + String.format("%.2f", highestScore));
        Label lowestScoreLabel = new Label("Lowest Score: " + String.format("%.2f", lowestScore));

        VBox overallStatsContainer = new VBox(10, totalParticipationsLabel, averageScoreLabel, highestScoreLabel, lowestScoreLabel);
        overallStatsTab.setContent(overallStatsContainer);
    }

    private void displayScoreDistribution(List<Participation> participations) {
        Map<Integer, Long> scoreCounts = participations.stream()
                .map(p -> (int) Math.floor(p.getScore() / 10) * 10) // Group scores by intervals of 10
                .collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()));

        scoreDistributionChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Score Distribution");

        scoreCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey() + "-" + (entry.getKey() + 9), entry.getValue())));

        scoreDistributionChart.getData().add(series);
        scoreCategoryAxis.setLabel("Score Range");
        scoreNumberAxis.setLabel("Number of Participations");
    }

    private void populateChallengeFilter() {
        List<Integer> challengeIds = allParticipations.stream()
                .map(Participation::getChallengeId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        challengeFilterComboBox.setItems(FXCollections.observableArrayList(challengeIds));
        challengeFilterComboBox.setValue(null); // Initially show all
    }

    private void setupChallengeFilterListener() {
        challengeFilterComboBox.setOnAction(event -> {
            Integer selectedChallengeId = challengeFilterComboBox.getValue();
            if (selectedChallengeId == null) {
                displayParticipationsByChallenge(allParticipations);
            } else {
                List<Participation> filteredParticipations = allParticipations.stream()
                        .filter(p -> p.getChallengeId() == selectedChallengeId)
                        .collect(Collectors.toList());
                displayParticipationsByChallenge(filteredParticipations);
            }
        });
    }

    private void displayParticipationsByChallenge(List<Participation> participations) {
        Map<Integer, Long> participationCounts = participations.stream()
                .collect(Collectors.groupingBy(Participation::getChallengeId, Collectors.counting()));

        participationsByChallengeChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Participations per Challenge");

        participationCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue())));

        participationsByChallengeChart.getData().add(series);
        challengeCategoryAxis.setLabel("Challenge ID");
        challengeNumberAxis.setLabel("Number of Participations");
    }
}