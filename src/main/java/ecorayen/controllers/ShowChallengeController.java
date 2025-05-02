package ecorayen.controllers;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import ecorayen.models.challenge;
import ecorayen.models.comment;
import ecorayen.services.ServiceChallenge;
import ecorayen.services.commentservice;
import io.nayuki.qrcodegen.QrCode;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class ShowChallengeController {

    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dateStartLabel;
    @FXML private Label dateEndLabel;
    @FXML private Label locationLabel;
    @FXML private ImageView challengeImageView;
    @FXML private VBox qrCodeContainer;
    @FXML private ListView<comment> commentsListView;
    @FXML private TextField commentAuthorField;
    @FXML private TextArea commentContentField;
    @FXML private Button sortNewestButton;
    @FXML private Button sortOldestButton;
    @FXML private Button sortPopularButton;

    private challenge currentChallenge;
    private final ServiceChallenge serviceChallenge = new ServiceChallenge();
    private final commentservice commentService = new commentservice();
    private ArrayList<comment> commentsList = new ArrayList<>();

    // Image display constants
    private static final double FIXED_WIDTH = 300;
    private static final double FIXED_HEIGHT = 300;

    public void initData(challenge challenge) {
        if (challenge != null) {
            this.currentChallenge = challenge;
            nameLabel.setText(challenge.getName());
            descriptionLabel.setText(challenge.getDescription());
            dateStartLabel.setText("Start: " + challenge.getDate_start());
            dateEndLabel.setText("End: " + challenge.getDate_end());
            locationLabel.setText("Location: " + challenge.getLocation());
            displayImageFromDatabase(challenge.getImage());
            generateAndDisplayQRCode(createQRContent(challenge));
            loadComments();
            setupSortButtons();
        }
    }

    private void setupSortButtons() {
        sortNewestButton.setOnAction(e -> sortComments(CommentSortType.NEWEST));
        sortOldestButton.setOnAction(e -> sortComments(CommentSortType.OLDEST));
        sortPopularButton.setOnAction(e -> sortComments(CommentSortType.POPULAR));
    }

    private enum CommentSortType {
        NEWEST, OLDEST, POPULAR
    }

    private void sortComments(CommentSortType sortType) {
        switch (sortType) {
            case NEWEST:
                commentsList.sort(Comparator.comparing(comment::getDate).reversed());
                break;
            case OLDEST:
                commentsList.sort(Comparator.comparing(comment::getDate));
                break;
            case POPULAR:
                commentsList.sort(Comparator.comparingInt(c -> -(c.getLikes() - c.getDislikes())));
                break;
        }
        refreshCommentsListView();
    }

    private void loadComments() {
        commentsList = commentService.getAll();
        // Initialize likes/dislikes if not already set
        for (comment c : commentsList) {
            if (c.getLikes() == 0 && c.getDislikes() == 0) {
                c.setLikes((int)(Math.random() * 10)); // Random likes for demo
                c.setDislikes((int)(Math.random() * 3)); // Random dislikes for demo
            }
        }
        sortComments(CommentSortType.NEWEST); // Default to newest first
    }

    private void refreshCommentsListView() {
        commentsListView.getItems().setAll(commentsList);
        commentsListView.setCellFactory(lv -> new CommentListCell());
    }

    private class CommentListCell extends javafx.scene.control.ListCell<comment> {
        private final HBox container = new HBox();
        private final VBox contentBox = new VBox();
        private final Label authorLabel = new Label();
        private final Label dateLabel = new Label();
        private final Label contentLabel = new Label();
        private final HBox likeDislikeBox = new HBox();
        private final Button likeButton = new Button("Like");
        private final Button dislikeButton = new Button("Dislike");
        private final Label likesLabel = new Label();
        private final Label dislikesLabel = new Label();

        public CommentListCell() {
            super();

            // Setup UI elements
            authorLabel.setStyle("-fx-font-weight: bold;");
            dateLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 0.8em;");
            contentLabel.setWrapText(true);
            likesLabel.setStyle("-fx-text-fill: green;");
            dislikesLabel.setStyle("-fx-text-fill: red;");

            likeButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 2 5 2 5;");
            dislikeButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 2 5 2 5;");

            likeButton.setOnAction(e -> handleLike(getItem()));
            dislikeButton.setOnAction(e -> handleDislike(getItem()));

            likeDislikeBox.getChildren().addAll(
                    likeButton, likesLabel,
                    new Label("   "),
                    dislikeButton, dislikesLabel
            );
            likeDislikeBox.setSpacing(5);

            contentBox.getChildren().addAll(
                    new HBox(authorLabel, dateLabel),
                    contentLabel,
                    likeDislikeBox
            );
            contentBox.setSpacing(5);

            container.getChildren().add(contentBox);
            container.setSpacing(10);
        }

        @Override
        protected void updateItem(comment comment, boolean empty) {
            super.updateItem(comment, empty);

            if (empty || comment == null) {
                setGraphic(null);
            } else {
                authorLabel.setText(comment.getAuthor());
                dateLabel.setText(comment.getDate());
                contentLabel.setText(comment.getContent());
                likesLabel.setText(String.valueOf(comment.getLikes()));
                dislikesLabel.setText(String.valueOf(comment.getDislikes()));
                setGraphic(container);
            }
        }
    }

    private void handleLike(comment comment) {
        if (comment != null) {
            comment.setLikes(comment.getLikes() + 1);
            refreshCommentsListView();
        }
    }

    private void handleDislike(comment comment) {
        if (comment != null) {
            comment.setDislikes(comment.getDislikes() + 1);
            refreshCommentsListView();
        }
    }

    private String createQRContent(challenge challenge) {
        return String.format(
                "Challenge: %s\nDescription: %s\nDates: %s to %s\nLocation: %s",
                challenge.getName(),
                challenge.getDescription(),
                challenge.getDate_start(),
                challenge.getDate_end(),
                challenge.getLocation()
        );
    }

    @FXML
    private void handleAddComment() {
        String author = commentAuthorField.getText().trim();
        String content = commentContentField.getText().trim();

        if (author.isEmpty() || content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill all fields");
            return;
        }

        comment newComment = new comment();
        newComment.setAuthor(author);
        newComment.setContent(content);
        newComment.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        newComment.setLikes(0);
        newComment.setDislikes(0);

        if (commentService.add(newComment)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Comment added successfully");
            commentAuthorField.clear();
            commentContentField.clear();
            loadComments();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add comment");
        }
    }

    @FXML
    private void handleUpdateComment() {
        comment selectedComment = commentsListView.getSelectionModel().getSelectedItem();
        if (selectedComment == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a comment to update");
            return;
        }

        String author = commentAuthorField.getText().trim();
        String content = commentContentField.getText().trim();

        if (author.isEmpty() || content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill all fields");
            return;
        }

        selectedComment.setAuthor(author);
        selectedComment.setContent(content);
        selectedComment.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (commentService.update(selectedComment)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Comment updated successfully");
            loadComments();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update comment");
        }
    }

    @FXML
    private void handleDeleteComment() {
        comment selectedComment = commentsListView.getSelectionModel().getSelectedItem();
        if (selectedComment == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a comment to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Comment");
        confirm.setContentText("Are you sure you want to delete this comment?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                if (commentService.delete(selectedComment)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Comment deleted successfully");
                    loadComments();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete comment");
                }
            }
        });
    }

    @FXML
    private void handleCommentSelected() {
        comment selectedComment = commentsListView.getSelectionModel().getSelectedItem();
        if (selectedComment != null) {
            commentAuthorField.setText(selectedComment.getAuthor());
            commentContentField.setText(selectedComment.getContent());
        }
    }

    private void generateAndDisplayQRCode(String data) {
        try {
            QrCode qr = QrCode.encodeText(data, QrCode.Ecc.MEDIUM);
            int size = qr.size;
            int scale = 8;
            WritableImage image = new WritableImage(size * scale, size * scale);
            PixelWriter pixelWriter = image.getPixelWriter();

            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    Color color = qr.getModule(x, y) ? Color.BLACK : Color.WHITE;
                    for (int dy = 0; dy < scale; dy++) {
                        for (int dx = 0; dx < scale; dx++) {
                            pixelWriter.setColor(x * scale + dx, y * scale + dy, color);
                        }
                    }
                }
            }

            ImageView qrImageView = new ImageView(image);
            qrImageView.setFitWidth(200);
            qrImageView.setFitHeight(200);
            qrImageView.setPreserveRatio(true);

            qrCodeContainer.getChildren().clear();
            qrCodeContainer.getChildren().add(qrImageView);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "QR Error", "Failed to generate QR code: " + e.getMessage());
        }
    }

    private void displayImageFromDatabase(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = new Image(new File(imagePath).toURI().toString(), true);
                challengeImageView.setImage(image);
            } else {
                Image placeholder = new Image(getClass().getResourceAsStream("/images/placeholder.png"));
                challengeImageView.setImage(placeholder);
            }
            challengeImageView.setFitWidth(FIXED_WIDTH);
            challengeImageView.setFitHeight(FIXED_HEIGHT);
            challengeImageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            challengeImageView.setImage(new Image(getClass().getResourceAsStream("/images/error.png")));
        }
    }

    @FXML
    private void handlePrint() {
        if (currentChallenge == null) {
            showAlert(Alert.AlertType.WARNING, "No challenge", "No challenge selected to print.");
            return;
        }

        try {
            String downloadsDir = getDownloadsDirectory();
            String safeName = currentChallenge.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
            String pdfFileName = String.format("Challenge_%d_%s.pdf", currentChallenge.getId(), safeName);
            File pdfFile = new File(downloadsDir, pdfFileName);

            int counter = 1;
            while (pdfFile.exists()) {
                pdfFileName = String.format("Challenge_%d_%s(%d).pdf",
                        currentChallenge.getId(), safeName, counter++);
                pdfFile = new File(downloadsDir, pdfFileName);
            }

            try (OutputStream os = new FileOutputStream(pdfFile)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(generateHtmlFromChallenge(currentChallenge), null);
                builder.toStream(os);
                builder.run();
            }

            showAlert(Alert.AlertType.INFORMATION, "PDF Generated",
                    "PDF saved to:\n" + pdfFile.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "PDF Error",
                    "Failed to generate PDF:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateHtmlFromChallenge(challenge challenge) {
        String qrSvg = generateQrCodeSvg(createQRContent(challenge));
        String commentsHtml = generateCommentsHtml();

        return String.format("""
            <html>
                <head>
                    <style>
                        body { 
                            font-family: 'Arial', sans-serif; 
                            margin: 0;
                            padding: 0;
                            background-color: #f9f9f9;
                        }
                        .invitation-card {
                            max-width: 600px;
                            margin: 20px auto;
                            padding: 30px;
                            background: white;
                            box-shadow: 0 0 20px rgba(0,0,0,0.1);
                            border-radius: 15px;
                            border-top: 10px solid #2E8B57;
                        }
                        .header { 
                            color: #2E8B57; 
                            text-align: center; 
                            margin-bottom: 10px;
                            font-size: 28px;
                            font-weight: bold;
                            text-transform: uppercase;
                            letter-spacing: 1px;
                        }
                        .subheader {
                            text-align: center;
                            color: #666;
                            margin-bottom: 30px;
                            font-style: italic;
                        }
                        .details { 
                            margin: 30px 0; 
                            padding: 25px;
                            background: linear-gradient(to right, #f8f8f8, #e8f5e9);
                            border-radius: 10px;
                            border-left: 5px solid #2E8B57;
                        }
                        .detail-item {
                            margin: 15px 0;
                            padding-bottom: 10px;
                            border-bottom: 1px dashed #ddd;
                        }
                        .detail-item:last-child {
                            border-bottom: none;
                        }
                        .detail-label {
                            font-weight: bold;
                            color: #2E8B57;
                            display: inline-block;
                            width: 120px;
                        }
                        .image-container { 
                            text-align: center; 
                            margin: 25px 0;
                            padding: 10px;
                            background: white;
                            border-radius: 10px;
                            box-shadow: 0 0 10px rgba(0,0,0,0.05);
                        }
                        img { 
                            max-width: 100%%; 
                            max-height: 300px; 
                            border-radius: 8px;
                            box-shadow: 0 0 5px rgba(0,0,0,0.1);
                        }
                        .footer {
                            text-align: center;
                            margin-top: 30px;
                            color: #888;
                            font-size: 12px;
                        }
                        .decoration {
                            height: 20px;
                            background: linear-gradient(to right, #2E8B57, #81C784);
                            margin: 20px -30px;
                            border-radius: 0 0 15px 15px;
                        }
                        .qr-container {
                            text-align: center;
                            margin: 20px 0;
                            padding: 15px;
                            background: white;
                            border-radius: 10px;
                            border: 1px solid #eee;
                        }
                        .qr-container h3 {
                            color: #2E8B57;
                            margin-bottom: 10px;
                        }
                        .comments-section {
                            margin: 30px 0;
                            padding: 20px;
                            background: #f5f5f5;
                            border-radius: 10px;
                        }
                        .comment {
                            margin-bottom: 15px;
                            padding: 15px;
                            background: white;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
                        }
                        .comment-author {
                            font-weight: bold;
                            color: #2E8B57;
                        }
                        .comment-date {
                            font-size: 12px;
                            color: #888;
                            margin-left: 10px;
                        }
                        .comment-content {
                            margin-top: 8px;
                            line-height: 1.4;
                        }
                        .comment-stats {
                            margin-top: 8px;
                            font-size: 0.9em;
                        }
                        .likes {
                            color: green;
                            margin-right: 15px;
                        }
                        .dislikes {
                            color: red;
                        }
                    </style>
                </head>
                <body>
                    <div class="invitation-card">
                        <h1 class="header">%s</h1>
                        <div class="subheader">You're cordially invited to participate!</div>
                        
                        %s
                        
                        <div class="details">
                            <div class="detail-item">
                                <span class="detail-label">Description:</span> %s
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Start Date:</span> %s
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">End Date:</span> %s
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Location:</span> %s
                            </div>
                        </div>
                        
                        <div class="qr-container">
                            <h3>Scan to save challenge details</h3>
                            %s
                        </div>
                        
                        <div class="comments-section">
                            <h3>Comments (%d)</h3>
                            %s
                        </div>
                        
                        <div class="decoration"></div>
                        <p class="footer">Generated by EcoRayen challenge System</p>
                    </div>
                </body>
            </html>
            """,
                escapeHtml(challenge.getName()),
                challenge.getImage() != null && !challenge.getImage().isEmpty() ?
                        String.format("<div class='image-container'><img src='%s'/></div>",
                                new File(challenge.getImage()).toURI()) :
                        "<div class='image-container'>[No Image]</div>",
                escapeHtml(challenge.getDescription()),
                escapeHtml(String.valueOf(challenge.getDate_start())),
                escapeHtml(String.valueOf(challenge.getDate_end())),
                escapeHtml(challenge.getLocation()),
                qrSvg,
                commentsList.size(),
                commentsHtml
        );
    }

    private String generateCommentsHtml() {
        if (commentsList.isEmpty()) {
            return "<p>No comments yet</p>";
        }

        StringBuilder sb = new StringBuilder();
        for (comment c : commentsList) {
            sb.append(String.format("""
                <div class="comment">
                    <div>
                        <span class="comment-author">%s</span>
                        <span class="comment-date">%s</span>
                    </div>
                    <div class="comment-content">%s</div>
                    <div class="comment-stats">
                        <span class="likes">👍 %d</span>
                        <span class="dislikes">👎 %d</span>
                    </div>
                </div>
                """,
                    escapeHtml(c.getAuthor()),
                    escapeHtml(c.getDate()),
                    escapeHtml(c.getContent()),
                    c.getLikes(),
                    c.getDislikes()
            ));
        }
        return sb.toString();
    }

    private String generateQrCodeSvg(String data) {
        QrCode qr = QrCode.encodeText(data, QrCode.Ecc.MEDIUM);
        return qr.toString();
    }

    @FXML
    private void handleEdit() {
        showAlert(Alert.AlertType.INFORMATION, "Edit", "Edit functionality coming soon!");
    }

    @FXML
    private void handleDelete() {
        if (currentChallenge == null) {
            showAlert(Alert.AlertType.WARNING, "No challenge", "No challenge selected to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete challenge");
        confirm.setContentText("Are you sure you want to delete '" + currentChallenge.getName() + "'?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    if (serviceChallenge.delete(currentChallenge.getId())) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "challenge deleted successfully.");
                        // TODO: Close this view or refresh parent view
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete challenge.");
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Error deleting challenge: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleShare() {
        showAlert(Alert.AlertType.INFORMATION, "Share", "Share functionality coming soon!");
    }

    private String getDownloadsDirectory() {
        String home = System.getProperty("user.home");
        String downloads = Paths.get(home, "Downloads").toString();
        File dir = new File(downloads);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return downloads;
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}