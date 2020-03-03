package components.containers.cards;

import api.objects.playables.Playlist;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class PlaylistCard extends VBox {
    private Playlist playlist;

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

    @FXML
    private void onPlaylistRedirection() {
        getPlaylistRedirectioner().accept(playlist.getId());
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        picture.setImage(new Image(playlist.getPicture_medium().toString(), true));
        picture.fitWidthProperty().bind(this.prefWidthProperty());
        picture.fitHeightProperty().bind(picture.fitWidthProperty());
        playlistRedirectButton.prefWidthProperty().bind(picture.fitWidthProperty());
        playlistRedirectButton.prefHeightProperty().bind(picture.fitHeightProperty());
        title.setText(playlist.getTitle());
        trackCount.setText(String.format("%s: %d", resources.getString("tracksCnt"),
                playlist.getNb_tracks()));
    }

    public final ObjectProperty<Consumer<Long>> playlistRedirectionerProperty() { return playlistRedirectioner; }
    public final void setPlaylistRedirectioner(Consumer<Long> value) { playlistRedirectionerProperty().set(value); }
    public final Consumer<Long> getPlaylistRedirectioner() { return playlistRedirectionerProperty().get(); }
    private ObjectProperty<Consumer<Long>> playlistRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return PlaylistCard.this;
        }

        @Override
        public String getName() {
            return "playlistRedirectioner";
        }
    };
}
