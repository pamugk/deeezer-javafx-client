package components.views;

import api.Deezer;
import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.TrackSearch;
import components.containers.boxes.ArtistBox;
import components.containers.boxes.CommentBox;
import components.containers.boxes.PlaylistBox;
import components.containers.flows.AlbumFlowPane;
import components.containers.flows.ArtistFlowPane;
import components.containers.flows.PlaylistFlowPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static api.LoginStatus.NOT_AUTHORIZED;

public class ArtistView extends VBox {

    public ArtistView() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("artistView.fxml"), bundle);
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
    private ImageView artistPicture;
    @FXML
    private Label artistNameLbl;
    @FXML
    private Label artistFansLbl;
    @FXML
    private Button artistPlayMixBtn;
    @FXML
    private Button artistAddToFavBtn;
    @FXML
    private ImageView artistFollowedImg;
    @FXML
    private TabPane artistTabPane;
    @FXML
    private Tab artistDiscographyTab;
    @FXML
    private Button artistMPTracksBtn;
    @FXML
    private Button artistMtPReleaseBtn;
    @FXML
    private Button artistPlaylistsBtn;
    @FXML
    private Button artistSimiliarBtn;
    @FXML
    private PlaylistBox artistPlaylistsBox;
    @FXML
    private TableView<TrackSearch> artistTopTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> artistTMPTIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> artistTMPTTitleCol;
    @FXML
    private ArtistBox artistRelatedBox;
    @FXML
    private ImageView artistTopAlbumImg;
    @FXML
    private Label artistTopAlbumName;
    @FXML
    private Label artistTopAlbumRelease;
    @FXML
    private TableView<TrackSearch> artistTopAlbumTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> artistTATIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> artistTATTitleCol;
    @FXML
    private AlbumFlowPane artistDiscographyFP;
    @FXML
    private Tab artistPopularTracksTab;
    @FXML
    private Button artistListenPopTracksBtn;
    @FXML
    private TextField artistPopTracksSearchBox;
    @FXML
    private TableView<TrackSearch> artistPopTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> artistMPTIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> artistMPTTitleCol;
    @FXML
    private TableColumn<TrackSearch, Artist> artistMPTArtistCol;
    @FXML
    private TableColumn<TrackSearch, Album> artistMPTAlbumCol;
    @FXML
    private TableColumn<TrackSearch, Integer> artistMPTPopCol;
    @FXML
    private TableColumn<TrackSearch, Integer> artistMPTLengthCol;
    @FXML
    private Tab artistRelatedTab;
    @FXML
    private ArtistFlowPane artistRelatedFP;
    @FXML
    private Tab artistPlaylistsTab;
    @FXML
    private PlaylistFlowPane artistPlaylistsFP;
    @FXML
    private CommentBox artistCommentsBox;
    //</editor-fold>

    @FXML
    void artistPlaylistsBtn_OnAction(ActionEvent actionEvent) {
        artistTabPane.getSelectionModel().select(artistPlaylistsTab);
    }

    @FXML
    void artistMPTracksBtn_OnAction(ActionEvent actionEvent) {
        artistTabPane.getSelectionModel().select(artistPopularTracksTab);
    }

    @FXML
    void artistSimiliarBtn_OnAction(ActionEvent actionEvent) {
        artistTabPane.getSelectionModel().select(artistRelatedTab);
    }

    public void setArtist(Artist artist, Deezer deezerClient) {
        artistPicture.setImage(new Image(artist.getPicture_medium().toString(), true));
        artistNameLbl.setText(artist.getName());
        artistFansLbl.setText(String.format("%s: %d", resources.getString("followers"), artist.getNb_fan()));
        if (deezerClient.getLoginStatus() == NOT_AUTHORIZED)
            artistAddToFavBtn.setVisible(false);
        else {
            artistAddToFavBtn.setVisible(true);
            artistFollowedImg.setImage(new Image("src/main/resources/img/icon-like.png"));
        }
        PartialSearchResponse<TrackSearch> popularTracks = deezerClient.getArtistTop(artist, 50);
        artistTopTracksTV.getItems().clear();
        artistTopTracksTV.getItems().addAll(popularTracks.getData().stream().limit(4).collect(Collectors.toList()));
        artistPopTracksTV.getItems().clear();
        artistPopTracksTV.getItems().addAll(popularTracks.getData());

        PartialSearchResponse<Playlist> playlists = deezerClient.getArtistPlaylists(artist, 25);
        artistPlaylistsBox.fill(playlists.getData().stream().limit(3).collect(Collectors.toList()));
        artistPlaylistsFP.fill(playlists, null, true, true);

        PartialSearchResponse<Artist> similiarArtists = deezerClient.getArtistRelated(artist, 25);
        artistRelatedBox.fill(similiarArtists.getData().stream().limit(3).collect(Collectors.toList()));
        artistRelatedFP.fill(similiarArtists, null, true, true);

        PartialSearchResponse<Album> discography = deezerClient.getArtistDiscography(artist);
        artistDiscographyFP.fill(discography, null, true, true);
        artistTopAlbumTracksTV.getItems().clear();
        var ref = new Object() {
            boolean albumShowed = false;
        };
        discography.getData().stream().max(Comparator.comparingInt(Album::getRating))
                .ifPresent(album -> {
                    ref.albumShowed = true;
                    artistTopAlbumImg.setImage(new Image(album.getCover_medium().toString(), true));
                    artistTopAlbumName.setText(album.getTitle());
                    artistTopAlbumRelease.setText(album.getRelease_date().toInstant()
                            .atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE));
                    artistTopAlbumTracksTV.getItems().addAll(deezerClient.getAlbumTracks(album).getData()
                            .stream().map(track -> (TrackSearch)track).collect(Collectors.toList()));
                });
        artistTopAlbumImg.setVisible(ref.albumShowed);
        artistTopAlbumName.setVisible(ref.albumShowed);
        artistTopAlbumRelease.setVisible(ref.albumShowed);
        artistTopAlbumTracksTV.setVisible(ref.albumShowed);
        artistCommentsBox.fill(deezerClient.getArtistComments(artist));
        artistTabPane.getSelectionModel().select(artistDiscographyTab);
    }
}
