package ecorayen.controllers;

import ecorayen.models.challenge;
import ecorayen.services.ServiceChallenge;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AddChallengeController {

    // Constants
    private static final Set<String> PROFANITY_WORDS = Set.of(
            "shit", "fuck", "asshole", "bitch", "bastard", "damn", "cunt",
            "piss", "dick", "cock", "pussy", "whore", "slut", "fag", "nigger",
            "retard", "idiot", "moron", "douche", "wanker", "twat", "bellend",
            "arsehole", "bloody", "bugger", "bollocks", "chink", "spic", "kike"
    );
    private static final Map<String, List<String>> CHALLENGE_TYPES = Map.ofEntries(
            Map.entry("clean", Arrays.asList("cleanup initiative", "community cleanup")),
            Map.entry("tree", Arrays.asList("tree planting campaign", "urban forestry")),
            Map.entry("recycle", Arrays.asList("recycling program", "waste reduction")),
            Map.entry("charity", Arrays.asList("fundraising campaign", "charity drive")),
            Map.entry("sport", Arrays.asList("fitness challenge", "sports event")),
            Map.entry("fun", Arrays.asList("community event", "fun activity"))
    );
    private static final List<String> POSITIVE_IMPACTS = Arrays.asList(
            "reduce environmental impact", "improve local ecosystems",
            "support those in need", "bring the community together"
    );
    private static final List<String> CALLS_TO_ACTION = Arrays.asList(
            "Join us in making a difference!", "Be part of the solution!",
            "Your participation matters!", "Together we can create change!"
    );

    // FXML components
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker dateStartPicker;
    @FXML
    private DatePicker dateEndPicker;
    @FXML
    private TextField locationField;
    @FXML
    private TextField imagePathField;
    // @FXML private WebView mapView; // Removed
    @FXML
    private Button generateDescButton;
    @FXML
    private ProgressIndicator progressIndicator;
    // @FXML private Button zoomInButton; // Removed
    // @FXML private Button zoomOutButton; // Removed
    @FXML
    private VBox locationContainer;
    @FXML
    private Label coordinatesLabel;
    @FXML
    private Label addressLabel;

    // Map state
    private double currentLat = 0;
    private double currentLon = 0;
    private String currentLocationName = "";
    private File selectedImageFile;

    // Services
    private final ServiceChallenge serviceChallenge = new ServiceChallenge();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @FXML
    public void initialize() {
        configureDatePickers();
        updateLocationData("", "", "");
    }

    private void configureDatePickers() {
        dateStartPicker.setValue(LocalDate.now());
        dateEndPicker.setValue(LocalDate.now().plusDays(7));
        dateStartPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    @FXML
    private void handleGenerateDescription() {
        if (nameField.getText().isBlank()) {
            showAlert("Required", "Please enter challenge name first");
            return;
        }

        if (containsProfanity(nameField.getText())) {
            showAlert("Inappropriate Content", "Please choose a different name");
            return;
        }

        progressIndicator.setVisible(true);
        generateDescButton.setDisable(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                try {
                    Thread.sleep(5000); // << UPDATED: Delay changed to 5 seconds
                    return generateDescription();
                } catch (InterruptedException e) {
                    // It's good practice to restore the interrupted status
                    Thread.currentThread().interrupt();
                    return "Error generating description due to interruption";
                }
            }
        };

        task.setOnSucceeded(e -> {
            descriptionField.setText(task.getValue());
            progressIndicator.setVisible(false);
            generateDescButton.setDisable(false);
        });

        task.setOnFailed(e -> {
            // Handle any exceptions that occurred during the task execution
            descriptionField.setText("Failed to generate description.");
            progressIndicator.setVisible(false);
            generateDescButton.setDisable(false);
            // Optionally, log the exception: task.getException().printStackTrace();
        });


        executor.execute(task);
    }

    private String generateDescription() {
        String name = nameField.getText();
        String location = currentLocationName.isEmpty() ? locationField.getText() : currentLocationName;
        LocalDate start = dateStartPicker.getValue();
        LocalDate end = dateEndPicker.getValue();

        String type = determineChallengeType(name.toLowerCase());
        List<String> activities = CHALLENGE_TYPES.getOrDefault(type, Arrays.asList("community initiative"));
        String activity = getRandomElement(activities);
        String impact = getRandomElement(POSITIVE_IMPACTS);
        String callToAction = getRandomElement(CALLS_TO_ACTION);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy"); // Corrected date pattern
        String startDateFormatted = start != null ? start.format(formatter) : "Not set";
        String endDateFormatted = end != null ? end.format(formatter) : "Not set";


        return String.format("""
            Announcing the '%s'! This %s will run from %s to %s.

            Our goal is to %s in the %s area.

            %s Let's make a positive change together!
            """,
                name,
                activity,
                startDateFormatted, // Use formatted date
                endDateFormatted, // Use formatted date
                impact.toLowerCase(),
                location.isEmpty() ? "local community" : location, // Handle empty location
                callToAction
        );
    }

    private String determineChallengeType(String name) {
        for (String keyword : CHALLENGE_TYPES.keySet()) {
            if (name.contains(keyword)) {
                return keyword;
            }
        }
        return "community"; // Default type
    }

    private String getRandomElement(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "default activity"; // Fallback for empty list
        }
        return list.get(new Random().nextInt(list.size()));
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Challenge Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(nameField.getScene().getWindow());
        if (file != null) {
            try {
                if (file.length() > 5 * 1024 * 1024) { // 5MB limit
                    showAlert("Error", "Image must be less than 5MB");
                    return;
                }

                Path targetDir = Paths.get("images");
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }

                String uniqueName = System.currentTimeMillis() + "_" + file.getName();
                Path destination = targetDir.resolve(uniqueName);
                Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                imagePathField.setText(destination.toString());
                selectedImageFile = file; // Keep track of the original file if needed elsewhere
            } catch (IOException e) {
                showAlert("Error", "Could not save image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddChallenge(ActionEvent event) {
        if (!validateInputs()) return;

        challenge challenge = new challenge(
                nameField.getText(),
                descriptionField.getText(),
                dateStartPicker.getValue(),
                dateEndPicker.getValue(),
                locationField.getText(),
                imagePathField.getText()
        );

        if (serviceChallenge.add(challenge)) {
            showAlert("Success", "Challenge added successfully!");
            clearForm();
        } else {
            showAlert("Error", "Failed to save challenge");
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().isBlank()) {
            showAlert("Required", "Challenge name is required");
            return false;
        }

        if (containsProfanity(nameField.getText()) || containsProfanity(descriptionField.getText())) {
            showAlert("Inappropriate Content", "Please remove inappropriate language from name or description");
            return false;
        }

        if (dateStartPicker.getValue() == null || dateEndPicker.getValue() == null) {
            showAlert("Invalid Dates", "Start and End dates must be selected.");
            return false;
        }


        if (dateStartPicker.getValue().isAfter(dateEndPicker.getValue())) {
            showAlert("Invalid Dates", "End date must be on or after the start date");
            return false;
        }

        if (locationField.getText().isBlank() && currentLocationName.isBlank()) {
            showAlert("Required", "Location is required. Please enter it manually or select from map.");
            return false;
        }

        // Optionally, validate image path if it's mandatory
        // if (imagePathField.getText().isBlank()) {
        //     showAlert("Required", "Challenge image is required");
        //     return false;
        // }

        return true;
    }

    private boolean containsProfanity(String text) {
        if (text == null || text.isEmpty()) return false;
        String normalized = text.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();
        return Arrays.stream(normalized.split("\\s+"))
                .anyMatch(PROFANITY_WORDS::contains);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        ((Stage) nameField.getScene().getWindow()).close();
        // Consider shutting down executors if the application is closing
        // executor.shutdown();
        // scheduler.shutdown();
    }

    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        dateStartPicker.setValue(LocalDate.now());
        dateEndPicker.setValue(LocalDate.now().plusDays(7));
        locationField.clear();
        imagePathField.clear();
        selectedImageFile = null;
        updateLocationData("", "", ""); // Reset location display
    }

    // This method seems to be for updating UI elements based on map interaction (which was removed)
    // If map is truly removed, this might need adjustment or removal.
    // For now, it just resets the labels.
    private void updateLocationData(String coordinates, String address, String locationName) {
        Platform.runLater(() -> {
            coordinatesLabel.setText("Coordinates: " + (coordinates.isEmpty() ? "Not set" : coordinates));
            addressLabel.setText("Address: " + (address.isEmpty() ? "Not set" : address));
            currentLocationName = locationName; // Update the internal state
            if (!locationName.isEmpty()) {
                locationField.setText(locationName); // Also update the location text field if a name is provided
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Consider Alert.AlertType.WARNING or ERROR for errors
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleZoomIn(ActionEvent actionEvent) {
        //  handleZoomIn implementation (currently empty)
        System.out.println("Zoom In button pressed - No action implemented.");
    }

    @FXML
    public void handleZoomOut(ActionEvent actionEvent) {
        // handleZoomOut implementation (currently empty)
        System.out.println("Zoom Out button pressed - No action implemented.");
    }

    // Call this method when the window is closing to ensure threads are shut down.
    public void shutdown() {
        executor.shutdownNow(); // Attempt to stop all actively executing tasks
        scheduler.shutdownNow();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}