package ecorayen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main5 extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the dashboard FXML
            Parent root = FXMLLoader.load(getClass().getResource("/views/dashboard.fxml"));

            // Configure the main stage
            primaryStage.setTitle("EcoRayen Dashboard");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            // Set minimum window size
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);

            // Show the stage
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading dashboard: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Set JavaFX version compatibility
        System.setProperty("javafx.version", "17.0.6");
        launch(args);
    }
}