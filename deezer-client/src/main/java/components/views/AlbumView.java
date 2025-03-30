package components.views;

import api.Deezer;
import api.objects.playables.Album;
import api.objects.playables.TrackSearch;
import components.containers.flows.AlbumFlowPane;
import components.containers.flows.ArtistFlowPane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import utils.TimeUtils;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static api.LoginStatus.NOT_AUTHORIZED;

public class AlbumView extends VBox {
    private Album album;

    public AlbumView() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("albumView.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private ResourceBundle resources;
    @FXML
    private ImageView albumCover;
    @FXML
    private Label albumName;
    @FXML
    private Button albumArtist;
    @FXML
    private ImageView albumArtistImg;
    @FXML
    private Label albumTracksLbl;
    @FXML
    private Label albumDurationLbl;
    @FXML
    private Label albumOutLbl;
    @FXML
    private Label albumFollowersLbl;
    @FXML
    private Button listenAlbumBtn;
    @FXML
    private Button addAlbumToLibrary;
    @FXML
    private ImageView albumAddToLibImg;
    @FXML
    private TableView<TrackSearch> albumTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> albumTrackIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> albumTrackName;
    @FXML
    private TableColumn<TrackSearch, Integer> albumTrackDurCol;
    @FXML
    private TableColumn<TrackSearch, Integer> albumTrackPopCol;
    @FXML
    private AlbumFlowPane albumArtistDiscographyFP;
    @FXML
    private ArtistFlowPane albumArtistRelatedFP;
    //</editor-fold>

    public void setAlbum(Album album, Deezer deezerClient) {
        this.album = album;
        albumCover.setImage(new Image(album.getCover_medium().toString(), true));
        albumName.setText(album.getTitle());
        albumArtistImg.setImage(new Image(album.getArtist().getPicture_small().toString(), true));
        albumArtist.setText(album.getArtist().getName());
        albumArtist.setOnAction(event -> getArtistRedirectioner().accept(album.getArtist().getId()));
        albumTracksLbl.setText(String.format("%s: %d",
                resources.getString("tracksCnt"),album.getNb_tracks()));
        albumDurationLbl.setText(String.format("%s", TimeUtils.secondsToNormalTime(album.getDuration(), resources)));
        albumOutLbl.setText(String.format("%s", album.getRelease_date().toInstant().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE)));
        albumFollowersLbl.setText(String.format("%s: %d",
                resources.getString("followers"), album.getFans()));

        albumTracksTV.getItems().clear();
        albumTracksTV.getItems().addAll(album.getTracks().getData());

        albumArtistDiscographyFP.fill(deezerClient.getArtistDiscography(album.getArtist()), null, true, true);
        albumArtistRelatedFP.fill(deezerClient.getArtistRelated(album.getArtist(), 25), null, true, false);
        if (deezerClient.getLoginStatus() == NOT_AUTHORIZED)
            addAlbumToLibrary.setVisible(false);
        else {
            addAlbumToLibrary.setVisible(true);
            addAlbumToLibrary.setText(resources.getString("addToMyMusic"));
            albumAddToLibImg.setImage(new Image("src/main/resources/img/icon-like.png"));
        }
    }

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
            return AlbumView.this;
        }

        @Override
        public String getName() {
            return "artistRedirectioner";
        }
    };

    public  Album getAlbum() {
        return album;
    }
}
