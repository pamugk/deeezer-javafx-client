package components.containers.boxes;

import api.PartialSearchResponse;
import api.objects.comments.Comment;
import components.containers.cards.CommentCard;
import javafx.scene.layout.VBox;

public class CommentBox extends VBox {
    public void fill(PartialSearchResponse<Comment> comments) {
        getChildren().clear();
        for (final Comment comment : comments.getData()) {
            CommentCard commentCard = new CommentCard(comment);
            getChildren().add(commentCard);
        }
    }
}
