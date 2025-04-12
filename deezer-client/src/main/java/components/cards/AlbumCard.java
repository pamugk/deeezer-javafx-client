package components.cards;

import api.objects.playables.Album;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;

public class AlbumCard extends VBox {

    @FXML
    private ResourceBundle resources;
    @FXML
    private ImageView cover;
    @FXML
    private Label title;
    @FXML
    private Button artistRedirectButton;
    @FXML
    private Button albumRedirectButton;

    private Runnable albumAction = () -> {};
    private Runnable artistAction = () -> {};

    public AlbumCard() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("albumCard.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setAlbum(Album album) {
        cover.setImage(album.coverMedium() == null ? null : new Image(album.coverMedium().toString(), true));
        cover.fitWidthProperty().bind(this.prefWidthProperty());
        cover.fitHeightProperty().bind(cover.fitWidthProperty());
        albumRedirectButton.prefWidthProperty().bind(cover.fitWidthProperty());
        albumRedirectButton.prefHeightProperty().bind(cover.fitHeightProperty());
        title.setText(album.title());
        if (album.artist() == null) {
            artistRedirectButton.setVisible(false);
        } else {
            artistRedirectButton.setText(String.format("%s %s", resources.getString("by"), album.artist().name()));
        }
    }

    public void setAlbumAction(Runnable albumAction) {
        this.albumAction = albumAction;
    }

    public void setArtistAction(Runnable artistAction) {
        this.artistAction = artistAction;
    }

    @FXML
    private void onAlbumRedirection() {
        albumAction.run();
    }

    @FXML
    private void onArtistRedirection() {
        artistAction.run();
    }
}
