package ecorayen;

import ecorayen.controllers.ListChallengeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main3 extends Application {

    private static final String LIST_CHALLENGE_VIEW_FXML = "/views/ListChallengeView.fxml";
    private static final String STYLES_CSS = "/styles/styles.css";
    private static final String APPLICATION_TITLE = "Challenge List";

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(LIST_CHALLENGE_VIEW_FXML)));
            Parent root = loader.load();

            // Set up the scene and apply CSS
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLES_CSS)).toExternalForm());

            // Set up the stage
            primaryStage.setTitle(APPLICATION_TITLE);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();

            // Access the controller if needed
            ListChallengeController controller = loader.getController();
            // controller.loadInitialChallenges();

        } catch (IOException e) {
            System.err.println("❌ Error loading FXML: " + LIST_CHALLENGE_VIEW_FXML);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
