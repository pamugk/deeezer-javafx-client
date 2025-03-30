package components.containers.cards;

import api.objects.playables.Album;
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

public class AlbumCard extends VBox {
    private Album album;

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

    @FXML
    private void onAlbumRedirection() {
        artistRedirectioner.getValue().accept(album.getId());
    }

    @FXML
    private void onArtistRedirection() { artistRedirectioner.getValue().accept(album.getArtist().getId()); }

    public void setAlbum(Album album) {
        this.album = album;
        cover.setImage(album.getCover_medium() == null ? null : new Image(album.getCover_medium().toString(), true));
        cover.fitWidthProperty().bind(this.prefWidthProperty());
        cover.fitHeightProperty().bind(cover.fitWidthProperty());
        albumRedirectButton.prefWidthProperty().bind(cover.fitWidthProperty());
        albumRedirectButton.prefHeightProperty().bind(cover.fitHeightProperty());
        title.setText(album.getTitle());
        artistRedirectButton.setText(String.format("%s %s", resources.getString("by"), album.getArtist().getName()));
    }

    public final ObjectProperty<Consumer<Long>> albumRedirectionerProperty() {
        return albumRedirectioner;
    }

    public final void setAlbumRedirectioner(Consumer<Long> value) {
        albumRedirectionerProperty().set(value);
    }

    public final Consumer<Long> getAlbumRedirectioner() {
        return albumRedirectionerProperty().get();
    }

    private final ObjectProperty<Consumer<Long>> albumRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return AlbumCard.this;
        }

        @Override
        public String getName() {
            return "albumRedirectioner";
        }
    };

    public final ObjectProperty<Consumer<Long>> artistRedirectionerProperty() {
        return artistRedirectioner;
    }

    public final void setArtistRedirectioner(Consumer<Long> value) {
        artistRedirectionerProperty().set(value);
    }

    public final Consumer<Long> getArtistRedirectioner() {
        return artistRedirectionerProperty().get();
    }

    private final ObjectProperty<Consumer<Long>> artistRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return AlbumCard.this;
        }

        @Override
        public String getName() {
            return "artistRedirectioner";
        }
    };
}
