package components.containers.cards;

import api.objects.comments.Comment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CommentCard extends HBox {
    private Comment comment;

    public CommentCard() {
        load();
    }

    public CommentCard(Comment comment){
        load();
        setComment(comment);
    }

    @FXML
    private Button userRedirectButton;
    @FXML
    private ImageView commentorImg;
    @FXML
    private Label commentCreationInfo;
    @FXML
    private Label commentText;

    @FXML
    private void onUserRedirection() {
        //userRedirectioner.getValue().accept(comment.getAuthor().getId());
    }

    private void load(){
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("commentCard.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void setComment(Comment comment) {
        commentorImg.setImage(comment.getAuthor().getPicture_medium() == null ? null :
                new Image(comment.getAuthor().getPicture_medium().toString(), true));
        commentorImg.fitWidthProperty().bind(this.prefWidthProperty());
        commentorImg.fitHeightProperty().bind(commentorImg.fitWidthProperty());
        userRedirectButton.prefWidthProperty().bind(commentorImg.fitWidthProperty());
        userRedirectButton.prefHeightProperty().bind(commentorImg.fitHeightProperty());
        commentCreationInfo.setText(String.format("%s - %s", comment.getAuthor().getName(),
                Instant.ofEpochSecond(comment.getDate()).atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ISO_LOCAL_DATE)));
        commentText.setText(comment.getText());
    }
}
