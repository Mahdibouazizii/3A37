package ecorayen.controllers;

import ecorayen.models.post;
import ecorayen.services.postservice;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class DisplayPostsController {

    @FXML
    private TilePane postsTilePane;

    private postservice postService;
    private Stage primaryStage;

    @FXML
    public void initialize() {
        postService = new postservice();
        loadPosts();
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    private void loadPosts() {
        List<post> allPosts = postService.getAll();
        postsTilePane.getChildren().clear();

        for (post p : allPosts) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/post_card.fxml"));
                VBox postCard = loader.load();
                PostCardController controller = loader.getController();
                controller.setPost(p);
                postsTilePane.getChildren().add(postCard);
            } catch (IOException e) {
                System.err.println("Error loading post card: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAddPost(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add_post.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Add Post");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdatePost(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/update_post.fxml"));
            Parent root = loader.load();
            UpdatePostController updatePostController = loader.getController();

            //  get the selected post.
            post selectedPost = null;
            //Dummy Post Object for testing
            if(selectedPost == null){
                selectedPost = new post(1,"testCaption","testurl","testLoc","testCreated","testUpdated","testPostType",1.0,10,null);
            }

            updatePostController.setPost(selectedPost);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Update Post");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
