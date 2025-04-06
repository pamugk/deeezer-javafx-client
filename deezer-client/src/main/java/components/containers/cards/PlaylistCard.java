package components.containers.cards;

import api.objects.playables.Playlist;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;

public class PlaylistCard extends VBox {

    @FXML
    private ResourceBundle resources;
    @FXML
    private Button playlistRedirectButton;
    @FXML
    private ImageView picture;
    @FXML
    private Label title;
    @FXML
    private Label trackCount;

    private Runnable action = () -> {};

    public PlaylistCard() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("playlistCard.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void setPlaylist(Playlist playlist) {
        picture.setImage(new Image(playlist.getPicture_medium().toString(), true));
        picture.fitWidthProperty().bind(this.prefWidthProperty());
        picture.fitHeightProperty().bind(picture.fitWidthProperty());
        playlistRedirectButton.prefWidthProperty().bind(picture.fitWidthProperty());
        playlistRedirectButton.prefHeightProperty().bind(picture.fitHeightProperty());
        title.setText(playlist.getTitle());
        trackCount.setText(String.format("%s: %d", resources.getString("tracksCnt"),
                playlist.getNb_tracks()));
    }

    @FXML
    private void onPlaylistRedirection() {
        action.run();
    }
}
