package components.containers.cards;

import api.objects.playables.Artist;
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

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ArtistCard extends VBox {
    private Artist artist;

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
    private ResourceBundle resources;
    @FXML
    private Button artistRedirectButton;
    @FXML
    private ImageView picture;
    @FXML
    private Label name;
    @FXML
    private Label followers;

    @FXML
    private void onArtistRedirection() {
        artistRedirectioner.getValue().accept(artist.getId());
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
        picture.setImage(new Image(artist.getPicture_medium().toString(), true));
        picture.fitWidthProperty().bind(this.prefWidthProperty());
        picture.fitHeightProperty().bind(picture.fitWidthProperty());
        artistRedirectButton.prefWidthProperty().bind(picture.fitWidthProperty());
        artistRedirectButton.prefHeightProperty().bind(picture.fitHeightProperty());
        name.setText(artist.getName());
        name.setPrefWidth(Region.USE_COMPUTED_SIZE);
        name.setPrefHeight(Region.USE_COMPUTED_SIZE);
        followers.setText(String.format("%s: %s", resources.getString("followers"), artist.getNb_fan()));
        followers.setPrefWidth(Region.USE_COMPUTED_SIZE);
        followers.setPrefHeight(Region.USE_COMPUTED_SIZE);
        followers.setVisible(artist.getNb_fan() > 0);
    }

    public final ObjectProperty<Consumer<Long>> artistRedirectionerProperty() { return artistRedirectioner; }
    public final void setArtistRedirectioner(Consumer<Long> value) { artistRedirectionerProperty().set(value); }
    public final Consumer<Long> getArtistRedirectioner() { return artistRedirectionerProperty().get(); }
    private ObjectProperty<Consumer<Long>> artistRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return ArtistCard.this;
        }

        @Override
        public String getName() {
            return "artistRedirectioner";
        }
    };
}
