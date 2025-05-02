package ecorayen.controllers;

import ecorayen.models.challenge;
import ecorayen.services.ServiceChallenge;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
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

    // AI Description Generation
    private static final Map<String, List<String>> CHALLENGE_TYPES = Map.ofEntries(
            Map.entry("clean", List.of("cleanup initiative", "community cleanup")),
            Map.entry("tree", List.of("tree planting campaign", "urban forestry")),
            Map.entry("recycle", List.of("recycling program", "waste reduction")),
            Map.entry("charity", List.of("fundraising campaign", "charity drive")),
            Map.entry("sport", List.of("fitness challenge", "sports event")),
            Map.entry("fun", List.of("community event", "fun activity"))
    );

    private static final List<String> POSITIVE_IMPACTS = List.of(
            "reduce environmental impact", "improve local ecosystems",
            "support those in need", "bring the community together"
    );

    private static final List<String> CALLS_TO_ACTION = List.of(
            "Join us in making a difference!", "Be part of the solution!",
            "Your participation matters!", "Together we can create change!"
    );

    // FXML components
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dateStartPicker;
    @FXML private DatePicker dateEndPicker;
    @FXML private TextField locationField;
    @FXML private TextField imagePathField;
    @FXML private WebView mapView;
    @FXML private Button generateDescButton;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;
    @FXML private VBox locationContainer;
    @FXML private Label coordinatesLabel;
    @FXML private Label addressLabel;

    // Map configuration
    private static final String OSM_TILE_URL = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png";
    private static final String NOMINATIM_SEARCH_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1";
    private static final String NOMINATIM_REVERSE_URL = "https://nominatim.openstreetmap.org/reverse?lat=%s&lon=%s&format=json";

    // Map state
    private double currentLat = 0;
    private double currentLon = 0;
    private int currentZoom = 12;
    private String currentLocationName = "";
    private File selectedImageFile;

    // Services
    private final ServiceChallenge serviceChallenge = new ServiceChallenge();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @FXML
    public void initialize() {
        mapView.getEngine().getLoadWorker().exceptionProperty().addListener((obs, old, newExc) -> {
            if (newExc != null) {
                System.err.println("WebView Error: " + newExc.getMessage());
            }
        });

        configureDatePickers();
        setupMapView();
        setupLocationSearchListener();

        // Request location after initialization
        requestLocationFromJava();
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

    private void setupMapView() {
        progressIndicator.setVisible(false);
        descriptionField.setPrefRowCount(3);

        mapView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("WebView state: " + newState);
            if (newState == Worker.State.SUCCEEDED) {
                setupJavaJsBridge();
                // Don't auto-trigger JavaScript geolocation
                setDefaultMapLocation();
            } else if (newState == Worker.State.FAILED) {
                System.err.println("Load failed: " + mapView.getEngine().getLoadWorker().getException());
            }
        });

        loadDynamicMap();
    }

    private void setupJavaJsBridge() {
        JSObject window = (JSObject) mapView.getEngine().executeScript("window");
        window.setMember("javaConnector", new JavaConnector());
        mapView.getEngine().executeScript("console.log = function(message) { javaConnector.log(message); };");
    }

    // New method for Java-based geolocation
    private void requestLocationFromJava() {
        Task<Map<String, Double>> locationTask = new Task<>() {
            @Override
            protected Map<String, Double> call() throws Exception {
                // In a real application, you would use Java's location APIs here
                // For demonstration, we'll use a mock location (Paris)
                Map<String, Double> location = new HashMap<>();

                // Try to get real location first (would use Java location APIs)
                // If that fails, fall back to default location
                try {
                    // This is where you'd call actual Java location APIs
                    // For now, we'll just return a default location
                    location.put("lat", 48.8566);
                    location.put("lon", 2.3522);
                    location.put("zoom", 12.0);
                    return location;
                } catch (Exception e) {
                    System.err.println("Error getting location: " + e.getMessage());
                    // Fall back to default location
                    location.put("lat", 0.0);
                    location.put("lon", 0.0);
                    location.put("zoom", 2.0);
                    return location;
                }
            }
        };

        locationTask.setOnSucceeded(e -> {
            Map<String, Double> location = locationTask.getValue();
            double lat = location.get("lat");
            double lon = location.get("lon");
            double zoom = location.get("zoom");

            // Update the map with the obtained location
            String script = String.format("window.updateLocation(%f, %f, 'Current Location');", lat, lon);
            mapView.getEngine().executeScript(script);

            // If we got a real location (not 0,0), update the zoom
            if (lat != 0 || lon != 0) {
                mapView.getEngine().executeScript(String.format("window.updateMapView(%f, %f, %f);", lat, lon, zoom));
            }
        });

        locationTask.setOnFailed(e -> {
            System.err.println("Location task failed: " + locationTask.getException().getMessage());
            // Set default location on failure
            setDefaultMapLocation();
        });

        executor.execute(locationTask);
    }

    private void setDefaultMapLocation() {
        // Use a default location (Paris) if we can't get the real location
        double defaultLat = 48.8566;
        double defaultLon = 2.3522;
        int defaultZoom = 12;

        String script = String.format("window.updateLocation(%f, %f, 'Default Location');", defaultLat, defaultLon);
        mapView.getEngine().executeScript(script);
        mapView.getEngine().executeScript(String.format("window.updateMapView(%f, %f, %d);", defaultLat, defaultLon, defaultZoom));
    }

    private void loadDynamicMap() {
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { margin: 0; padding: 0; height: 100%; }
                    #map { height: 100%; width: 100%; position: relative; overflow: hidden; }
                    .map-info {
                        padding: 6px 8px;
                        background: white;
                        background: rgba(255,255,255,0.8);
                        border-radius: 4px;
                        font: 12px/1.5 Arial, sans-serif;
                    }
                    .custom-marker {
                        background-color: #4CAF50;
                        width: 24px;
                        height: 24px;
                        border-radius: 50%;
                        border: 2px solid white;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        color: white;
                        font-weight: bold;
                        position: absolute;
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var mapContainer = document.getElementById('map');
                    var currentMarker = null;
                    var zoom = 12;
                    var center = [0, 0];
                    var tileSize = 256;
                    var maxZoom = 19;
                    var minZoom = 1;

                    function initMap(lat, lon, zoomLevel) {
                        center = [lat, lon];
                        zoom = Math.min(Math.max(zoomLevel, minZoom), maxZoom);
                        mapContainer.innerHTML = '';
                        renderTiles();
                        if (lat !== 0 || lon !== 0) {
                            updateLocation(lat, lon, '');
                        }
                    }

                    function renderTiles() {
                        var zoomLevel = zoom;
                        var tiles = Math.pow(2, zoomLevel);
                        var tileX = Math.floor((center[1] + 180) / 360 * tiles);
                        var tileY = Math.floor((1 - Math.log(Math.tan(center[0] * Math.PI / 180) + 1 / Math.cos(center[0] * Math.PI / 180)) / Math.PI) / 2 * tiles);

                        mapContainer.innerHTML = '';
                        for (var x = tileX - 2; x <= tileX + 2; x++) {
                            for (var y = tileY - 2; y <= tileY + 2; y++) {
                                var tileUrl = 'https://a.tile.openstreetmap.org/' + zoomLevel + '/' + x + '/' + y + '.png';
                                var img = document.createElement('img');
                                img.src = tileUrl;
                                img.style.position = 'absolute';
                                img.style.width = tileSize + 'px';
                                img.style.height = tileSize + 'px';
                                img.style.left = ((x - tileX) * tileSize + mapContainer.offsetWidth / 2 - tileSize / 2) + 'px';
                                img.style.top = ((y - tileY) * tileSize + mapContainer.offsetHeight / 2 - tileSize / 2) + 'px';
                                mapContainer.appendChild(img);
                            }
                        }
                    }

                    window.updateLocation = function(lat, lon, name) {
                        center = [lat, lon];
                        renderTiles();

                        if (currentMarker) {
                            currentMarker.remove();
                        }

                        currentMarker = document.createElement('div');
                        currentMarker.className = 'custom-marker';
                        currentMarker.innerHTML = '✓';
                        currentMarker.style.left = (mapContainer.offsetWidth / 2 - 12) + 'px';
                        currentMarker.style.top = (mapContainer.offsetHeight / 2 - 12) + 'px';
                        mapContainer.appendChild(currentMarker);

                        var popupContent = '<div class="map-info">' +
                            '<b>' + (name || 'Location') + '</b><br>' +
                            'Latitude: ' + lat.toFixed(6) + '<br>' +
                            'Longitude: ' + lon.toFixed(6) + '</div>';

                        // Perform reverse geocoding to get address
                        fetch(`https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lon}&format=json`)
                            .then(response => response.json())
                            .then(data => {
                                var address = data.display_name || name || 'Unknown location';
                                if (window.javaConnector) {
                                    window.javaConnector.updateLocation(
                                        lat.toFixed(6) + ', ' + lon.toFixed(6),
                                        address,
                                        name
                                    );
                                }
                            })
                            .catch(error => {
                                console.error('Reverse geocoding error:', error);
                                if (window.javaConnector) {
                                    window.javaConnector.updateLocation(
                                        lat.toFixed(6) + ', ' + lon.toFixed(6),
                                        name || '',
                                        name
                                    );
                                }
                            });
                    };

                    window.updateMapView = function(lat, lon, zoomLevel) {
                        center = [lat, lon];
                        zoom = Math.min(Math.max(zoomLevel, minZoom), maxZoom);
                        renderTiles();
                        if (currentMarker) {
                            currentMarker.style.left = (mapContainer.offsetWidth / 2 - 12) + 'px';
                            currentMarker.style.top = (mapContainer.offsetHeight / 2 - 12) + 'px';
                        }
                    };

                    window.zoomIn = function() {
                        if (zoom < maxZoom) {
                            zoom++;
                            renderTiles();
                            if (currentMarker) {
                                currentMarker.style.left = (mapContainer.offsetWidth / 2 - 12) + 'px';
                                currentMarker.style.top = (mapContainer.offsetHeight / 2 - 12) + 'px';
                            }
                        }
                    };

                    window.zoomOut = function() {
                        if (zoom > minZoom) {
                            zoom--;
                            renderTiles();
                            if (currentMarker) {
                                currentMarker.style.left = (mapContainer.offsetWidth / 2 - 12) + 'px';
                                currentMarker.style.top = (mapContainer.offsetHeight / 2 - 12) + 'px';
                            }
                        }
                    };

                    window.panMap = function(deltaLat, deltaLon) {
                        center[0] += deltaLat / Math.pow(2, zoom);
                        center[1] += deltaLon / Math.pow(2, zoom);
                        renderTiles();
                        if (currentMarker) {
                            currentMarker.style.left = (mapContainer.offsetWidth / 2 - 12) + 'px';
                            currentMarker.style.top = (mapContainer.offsetHeight / 2 - 12) + 'px';
                        }
                    };

                    window.getUserLocation = function() {
                        if (navigator.geolocation) {
                            navigator.geolocation.getCurrentPosition(
                                position => {
                                    var lat = position.coords.latitude;
                                    var lon = position.coords.longitude;
                                    zoom = 15; // Set a closer zoom for GPS location
                                    window.updateLocation(lat, lon, 'Current Location');
                                },
                                error => {
                                    console.log('Geolocation error: ' + error.message);
                                    // Don't reset to default view here - let Java handle it
                                }
                            );
                        } else {
                            console.log('Geolocation not supported');
                            // Don't reset to default view here - let Java handle it
                        }
                    };

                    initMap(0, 0, 2);
                    console.log('Map initialized successfully');
                </script>
            </body>
            </html>
            """;

        mapView.getEngine().setJavaScriptEnabled(true);
        mapView.getEngine().loadContent(html);
    }

    private void setupLocationSearchListener() {
        locationField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !newVal.equals(oldVal)) {
                scheduler.schedule(() -> {
                    Platform.runLater(() -> {
                        if (newVal.equals(locationField.getText())) {
                            searchLocation(newVal);
                        }
                    });
                }, 750, TimeUnit.MILLISECONDS);
            } else if (newVal.isEmpty()) {
                resetMapView();
            }
        });
    }

    private void resetMapView() {
        mapView.getEngine().executeScript("initMap(0, 0, 2);");
        updateLocationData("", "", "");
    }

    private void searchLocation(String location) {
        progressIndicator.setVisible(true);

        Task<Void> searchTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    String encodedLocation = URLEncoder.encode(location, "UTF-8");
                    String searchUrl = String.format(NOMINATIM_SEARCH_URL, encodedLocation);

                    String script = String.format("""
                        fetch('%s')
                            .then(response => {
                                if (!response.ok) throw new Error('Network response was not ok');
                                return response.json();
                            })
                            .then(data => {
                                if (data && data.length > 0) {
                                    const result = data[0];
                                    const lat = parseFloat(result.lat);
                                    const lon = parseFloat(result.lon);
                                    window.updateLocation(lat, lon, result.display_name);
                                } else {
                                    console.log('No results found');
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                if (window.javaConnector) {
                                    window.javaConnector.updateLocation('', '', '');
                                }
                            });
                        """, searchUrl);

                    Platform.runLater(() -> {
                        mapView.getEngine().executeScript(script);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showAlert("Error", "Location search failed: " + e.getMessage());
                    });
                }
                return null;
            }
        };

        searchTask.setOnSucceeded(e -> progressIndicator.setVisible(false));
        searchTask.setOnFailed(e -> {
            progressIndicator.setVisible(false);
            showAlert("Error", "Location search task failed");
        });

        executor.execute(searchTask);
    }

    @FXML
    private void handleZoomIn(ActionEvent event) {
        mapView.getEngine().executeScript("window.zoomIn();");
        currentZoom = Math.min(currentZoom + 1, 19);
    }

    @FXML
    private void handleZoomOut(ActionEvent event) {
        mapView.getEngine().executeScript("window.zoomOut();");
        currentZoom = Math.max(currentZoom - 1, 1);
    }

    @FXML
    private void handlePanUp() {
        mapView.getEngine().executeScript("window.panMap(1, 0);");
        currentLat += 1.0 / Math.pow(2, currentZoom);
    }

    @FXML
    private void handlePanDown() {
        mapView.getEngine().executeScript("window.panMap(-1, 0);");
        currentLat -= 1.0 / Math.pow(2, currentZoom);
    }

    @FXML
    private void handlePanLeft() {
        mapView.getEngine().executeScript("window.panMap(0, -1);");
        currentLon -= 1.0 / Math.pow(2, currentZoom);
    }

    @FXML
    private void handlePanRight() {
        mapView.getEngine().executeScript("window.panMap(0, 1);");
        currentLon += 1.0 / Math.pow(2, currentZoom);
    }

    private void updateMapView() {
        mapView.getEngine().executeScript(
                String.format("window.updateMapView(%f, %f, %d);", currentLat, currentLon, currentZoom)
        );
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
                    Thread.sleep(800);
                    return generateDescription();
                } catch (InterruptedException e) {
                    return "Error generating description";
                }
            }
        };

        task.setOnSucceeded(e -> {
            descriptionField.setText(task.getValue());
            progressIndicator.setVisible(false);
            generateDescButton.setDisable(false);
        });

        executor.execute(task);
    }

    private String generateDescription() {
        String name = nameField.getText();
        String location = currentLocationName.isEmpty() ? locationField.getText() : currentLocationName;
        LocalDate start = dateStartPicker.getValue();
        LocalDate end = dateEndPicker.getValue();

        String type = determineChallengeType(name.toLowerCase());
        String activity = getRandomElement(CHALLENGE_TYPES.getOrDefault(type, List.of("community initiative")));
        String impact = getRandomElement(POSITIVE_IMPACTS);
        String callToAction = getRandomElement(CALLS_TO_ACTION);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        String dates = start.format(formatter) + " to " + end.format(formatter);

        return String.format("""
            %s %s from %s to %s.

            Help us %s in %s.

            %s
            """,
                name,
                activity,
                dates,
                impact.toLowerCase(),
                location,
                callToAction
        );
    }

    private String determineChallengeType(String name) {
        for (String keyword : CHALLENGE_TYPES.keySet()) {
            if (name.contains(keyword)) {
                return keyword;
            }
        }
        return "community";
    }

    private String getRandomElement(List<String> list) {
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
                if (file.length() > 5 * 1024 * 1024) {
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
                selectedImageFile = file;
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
            showAlert("Inappropriate Content", "Please remove inappropriate language");
            return false;
        }

        if (dateStartPicker.getValue().isAfter(dateEndPicker.getValue())) {
            showAlert("Invalid Dates", "End date must be after start date");
            return false;
        }

        if (locationField.getText().isBlank()) {
            showAlert("Required", "Location is required");
            return false;
        }

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
    }

    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        dateStartPicker.setValue(LocalDate.now());
        dateEndPicker.setValue(LocalDate.now().plusDays(7));
        locationField.clear();
        imagePathField.clear();
        selectedImageFile = null;
        updateLocationData("", "", "");
        resetMapView();
    }

    private void updateLocationData(String coordinates, String address, String locationName) {
        Platform.runLater(() -> {
            if (coordinates == null || coordinates.isEmpty()) {
                coordinatesLabel.setText("Coordinates: Not set");
                addressLabel.setText("Address: Not available");
                currentLat = 0;
                currentLon = 0;
                currentLocationName = "";
            } else {
                coordinatesLabel.setText("Coordinates: " + coordinates);
                addressLabel.setText("Address: " + (address.isEmpty() ? locationName : address));
                currentLocationName = address.isEmpty() ? locationName : address;

                String[] parts = coordinates.split(",");
                if (parts.length == 2) {
                    currentLat = Double.parseDouble(parts[0].trim());
                    currentLon = Double.parseDouble(parts[1].trim());
                }
            }
            // Update location field with the address
            locationField.setText(currentLocationName);
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public class JavaConnector {
        public void updateLocation(String coords, String address, String name) {
            Platform.runLater(() -> updateLocationData(coords, address, name));
        }

        public void log(String message) {
            System.out.println("JS Console: " + message);
        }
    }
}