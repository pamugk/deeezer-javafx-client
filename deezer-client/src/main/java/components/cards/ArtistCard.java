package components.cards;

import api.objects.playables.Artist;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;

public class ArtistCard extends VBox {

    @FXML
    private ResourceBundle resources;
    @FXML
    private Button artistRedirectButton;
    @FXML
    private ImageView picture;
    @FXML
    private Label name;
    @FXML
    private Label followers;

    private Runnable action = () -> {};

    public ArtistCard() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("artistCard.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void onArtistRedirection() {
        action.run();
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setArtist(Artist artist) {
        picture.setImage(new Image(artist.getPicture_medium().toString(), true));
        picture.fitWidthProperty().bind(this.prefWidthProperty());
        picture.fitHeightProperty().bind(picture.fitWidthProperty());
        artistRedirectButton.prefWidthProperty().bind(picture.fitWidthProperty());
        artistRedirectButton.prefHeightProperty().bind(picture.fitHeightProperty());
        name.setText(artist.getName());
        followers.setText(String.format("%s: %s", resources.getString("followers"), artist.getNb_fan()));
        followers.setVisible(artist.getNb_fan() > 0);
    }
}
