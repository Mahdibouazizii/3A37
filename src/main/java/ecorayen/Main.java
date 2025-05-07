package ecorayen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load the FXML file for the DisplayPostsView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/display_posts.fxml")); // Corrected path - added leading slash and package
            Parent root = loader.load();

            // Create a scene and set it on the stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Display Posts"); // Set a title for the window
            primaryStage.show(); // Show the stage
        } catch (IOException e) {
            e.printStackTrace(); // VERY important to print the error, otherwise you will have no idea why it doesn't work.
            // Consider showing an alert to the user in a real application.  A dialog box is better than printing to the console
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
