package components.containers.boxes;

import api.PartialSearchResponse;
import api.objects.comments.Comment;
import components.containers.cards.CommentCard;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class CommentBox extends VBox {
    public void fill(PartialSearchResponse<Comment> comments) {
        getChildren().clear();
        for (Comment comment : comments.getData()) {
            CommentCard commentCard = new CommentCard(comment);
            commentCard.setUserRedirectioner(getUserRedirectioner());
            getChildren().add(commentCard);
        }
    }

    public final ObjectProperty<Consumer<Long>> userRedirectionerProperty() { return userRedirectioner; }
    public final void setUserRedirectioner(Consumer<Long> value) { userRedirectionerProperty().set(value); }
    public final Consumer<Long> getUserRedirectioner() { return userRedirectionerProperty().get(); }
    private ObjectProperty<Consumer<Long>> userRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return CommentBox.this;
        }

        @Override
        public String getName() {
            return "userRedirectioner";
        }
    };
}
