package components.containers.cards;

import api.objects.utils.User;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class UserCard extends VBox {
    private User user;

    public UserCard() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userCard.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private ImageView image;
    @FXML
    private Label name;
    @FXML
    private Button userRedirectButton;

    @FXML
    private void onUserRedirection() {
        userRedirectioner.getValue().accept(user.getId());
    }

    public void setUser(User user) {
        this.user = user;
        image.setImage(user.getPicture_medium() == null ? null : new Image(user.getPicture_medium().toString(), true));
        image.fitWidthProperty().bind(this.prefWidthProperty());
        image.fitHeightProperty().bind(image.fitWidthProperty());
        userRedirectButton.prefWidthProperty().bind(image.fitWidthProperty());
        userRedirectButton.prefHeightProperty().bind(image.fitHeightProperty());
        name.setText(user.getName());
    }

    public final ObjectProperty<Consumer<Long>> userRedirectionerProperty() { return userRedirectioner; }
    public final void setUserRedirectioner(Consumer<Long> value) { userRedirectionerProperty().set(value); }
    public final Consumer<Long> getUserRedirectioner() { return userRedirectionerProperty().get(); }
    private ObjectProperty<Consumer<Long>> userRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return UserCard.this;
        }

        @Override
        public String getName() {
            return "userRedirectioner";
        }
    };
}
