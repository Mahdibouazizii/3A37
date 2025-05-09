package ecorayen.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.event.ActionEvent;

import java.time.LocalDate;
import java.util.Random;

public class StatsDashboardController {

    @FXML
    private LineChart<String, Number> userGrowthChart;

    @FXML
    private CategoryAxis userGrowthXAxis;

    @FXML
    private NumberAxis userGrowthYAxis;

    @FXML
    private Button updateButton;

    @FXML
    private Button backButton;

    @FXML
    private Button printButton;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button confirmDateRangeButton;

    @FXML
    public void initialize() {
        // Initial data for the chart (you'll likely fetch this from your data source)
        populateUserGrowthChart(LocalDate.now().minusDays(7), LocalDate.now());
    }

    @FXML
    private void handleUpdateButton(ActionEvent event) {
        // Logic to update the chart data (e.g., refetch from database)
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        if (start != null && end != null && !end.isBefore(start)) {
            populateUserGrowthChart(start, end);
        } else {
            // Optionally show an error message for invalid date range
            System.out.println("Invalid date range selected.");
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        // Logic to navigate back to the previous view
        System.out.println("Back button clicked.");
    }

    @FXML
    private void handlePrintButton(ActionEvent event) {
        // Logic to print the statistics or chart
        System.out.println("Print button clicked.");
    }

    @FXML
    private void handleConfirmDateRange(ActionEvent event) {
        // Logic to filter the chart data based on the selected date range
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        if (start != null && end != null && !end.isBefore(start)) {
            populateUserGrowthChart(start, end);
        } else {
            // Optionally show an error message for invalid date range
            System.out.println("Invalid date range selected.");
        }
    }

    private void populateUserGrowthChart(LocalDate startDate, LocalDate endDate) {
        userGrowthChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("New Users");

        Random random = new Random();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            series.getData().add(new XYChart.Data<>(currentDate.toString(), random.nextInt(20)));
            currentDate = currentDate.plusDays(1);
        }

        userGrowthChart.getData().add(series);
        userGrowthXAxis.setLabel("Date (" + startDate + " to " + endDate + ")");
    }
}