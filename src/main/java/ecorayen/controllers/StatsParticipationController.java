package ecorayen.controllers;

import ecorayen.models.Participation;
import ecorayen.services.ParticipationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatsParticipationController {

    // UI Components
    @FXML private TabPane statsTabPane;
    @FXML private Tab overallStatsTab;
    @FXML private Label totalParticipationsLabel;
    @FXML private Label averageScoreLabel;
    @FXML private Label highestScoreLabel;
    @FXML private Label lowestScoreLabel;
    @FXML private Tab scoreDistributionTab;
    @FXML private LineChart<Number, Number> scoreDistributionChart;
    @FXML private NumberAxis scoreCategoryAxis;
    @FXML private NumberAxis scoreNumberAxis;
    @FXML private Tab participationsByChallengeTab;
    @FXML private ComboBox<Integer> challengeFilterComboBox;
    @FXML private LineChart<Number, Number> participationsByChallengeChart;
    @FXML private NumberAxis challengeCategoryAxis;
    @FXML private NumberAxis challengeNumberAxis;
    @FXML private DatePicker fromDateFilter;
    @FXML private DatePicker toDateFilter;
    @FXML private ComboBox<String> sortComboBox;

    // Services and Data
    private final ParticipationService participationService = new ParticipationService();
    private List<Participation> allParticipations;
    private Map<Integer, List<Participation>> participationsByChallenge;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        setupDatePickers();
        setupSortComboBox();
        loadParticipations();
        setupUI();
        refreshAllCharts();
    }

    private void setupDatePickers() {
        fromDateFilter.setConverter(new LocalDateStringConverter());
        toDateFilter.setConverter(new LocalDateStringConverter());
        toDateFilter.setValue(LocalDate.now());
        fromDateFilter.setValue(LocalDate.now().minusMonths(3));
    }

    private void setupSortComboBox() {
        sortComboBox.setItems(FXCollections.observableArrayList(
                "Score (Ascending)",
                "Score (Descending)",
                "Date (Newest First)",
                "Date (Oldest First)",
                "Challenge ID",
                "Participations Count"
        ));
        sortComboBox.setValue("Participations Count");
        sortComboBox.setOnAction(event -> refreshAllCharts());
    }

    private void setupUI() {
        scoreDistributionChart.setTitle("Score Distribution");
        participationsByChallengeChart.setTitle("Participations by Challenge");
        setupChartTooltips(scoreDistributionChart);
        setupChartTooltips(participationsByChallengeChart);
    }

    private void loadParticipations() {
        allParticipations = participationService.getAll();
        participationsByChallenge = allParticipations.stream()
                .collect(Collectors.groupingBy(Participation::getChallengeId));
    }

    private void refreshAllCharts() {
        List<Participation> filteredData = applyFilters(allParticipations);
        displayOverallStats(filteredData);
        displayScoreDistribution(filteredData);
        displayParticipationsByChallenge(filteredData);
    }

    private List<Participation> applyFilters(List<Participation> participations) {
        List<Participation> filtered = applyDateFilter(participations);
        Integer selectedChallenge = challengeFilterComboBox.getValue();
        if (selectedChallenge != null) {
            filtered = filtered.stream()
                    .filter(p -> p.getChallengeId() == selectedChallenge)
                    .collect(Collectors.toList());
        }
        return applySorting(filtered);
    }

    private List<Participation> applySorting(List<Participation> participations) {
        String sortOption = sortComboBox.getValue();
        if (sortOption == null) return participations;

        switch (sortOption) {
            case "Score (Ascending)":
                return participations.stream()
                        .sorted(Comparator.comparingDouble(Participation::getScore))
                        .collect(Collectors.toList());
            case "Score (Descending)":
                return participations.stream()
                        .sorted(Comparator.comparingDouble(Participation::getScore).reversed())
                        .collect(Collectors.toList());
            case "Date (Newest First)":
                return participations.stream()
                        .sorted(Comparator.comparing(Participation::getParticipationDateTime).reversed())
                        .collect(Collectors.toList());
            case "Date (Oldest First)":
                return participations.stream()
                        .sorted(Comparator.comparing(Participation::getParticipationDateTime))
                        .collect(Collectors.toList());
            case "Challenge ID":
                return participations.stream()
                        .sorted(Comparator.comparingInt(Participation::getChallengeId))
                        .collect(Collectors.toList());
            default:
                return participations;
        }
    }

    private void displayOverallStats(List<Participation> participations) {
        int total = participations.size();
        DoubleSummaryStatistics stats = participations.stream()
                .mapToDouble(Participation::getScore)
                .summaryStatistics();

        totalParticipationsLabel.setText(String.format("Total Participations: %,d", total));
        averageScoreLabel.setText(String.format("Average Score: %.2f", stats.getAverage()));
        highestScoreLabel.setText(String.format("Highest Score: %.2f", stats.getMax()));
        lowestScoreLabel.setText(String.format("Lowest Score: %.2f", stats.getMin()));
    }

    private void displayScoreDistribution(List<Participation> participations) {
        Map<Double, Long> scoreCounts = participations.stream()
                .collect(Collectors.groupingBy(
                        Participation::getScore,
                        Collectors.counting()
                ));

        scoreDistributionChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Score Distribution");

        scoreCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });

        scoreDistributionChart.getData().add(series);
        scoreCategoryAxis.setLabel("Score");
        scoreNumberAxis.setLabel("Number of Participations");
    }

    private void displayParticipationsByChallenge(List<Participation> participations) {
        Map<Integer, Long> participationCounts = participations.stream()
                .collect(Collectors.groupingBy(
                        Participation::getChallengeId,
                        Collectors.counting()
                ));

        participationsByChallengeChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Participations by Challenge");

        participationCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });

        participationsByChallengeChart.getData().add(series);
        challengeCategoryAxis.setLabel("Challenge ID");
        challengeNumberAxis.setLabel("Number of Participations");
    }

    private List<Participation> applyDateFilter(List<Participation> participations) {
        LocalDate fromDate = fromDateFilter.getValue();
        LocalDate toDate = toDateFilter.getValue();

        return participations.stream()
                .filter(p -> {
                    LocalDate participationDate = p.getParticipationDateTime().toLocalDate();
                    return (fromDate == null || !participationDate.isBefore(fromDate)) &&
                            (toDate == null || !participationDate.isAfter(toDate));
                })
                .collect(Collectors.toList());
    }

    private void setupChartTooltips(LineChart<Number, Number> chart) {
        chart.getData().forEach(series -> {
            series.getData().forEach(data -> {
                Tooltip tooltip = new Tooltip(
                        String.format("Value: %.1f\nCount: %d",
                                data.getXValue().doubleValue(),
                                data.getYValue().intValue())
                );
                Tooltip.install(data.getNode(), tooltip);
            });
        });
    }

    private static class LocalDateStringConverter extends StringConverter<LocalDate> {
        @Override
        public String toString(LocalDate date) {
            return date != null ? DATE_FORMATTER.format(date) : "";
        }

        @Override
        public LocalDate fromString(String string) {
            return string != null && !string.isEmpty() ? LocalDate.parse(string, DATE_FORMATTER) : null;
        }
    }
}