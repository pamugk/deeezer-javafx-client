package components.views;

import api.Deezer;
import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.Track;
import api.objects.utils.User;
import api.objects.utils.search.SearchOrder;
import components.containers.flows.AlbumFlowPane;
import components.containers.flows.ArtistFlowPane;
import components.containers.flows.PlaylistFlowPane;
import components.containers.tables.TrackTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserView extends VBox {
    public enum Destinations{
        HIGHLIGHTS,
        TRACKS,
        PLAYLISTS,
        ALBUMS,
        ARTISTS,
    }

    public UserView() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userView.fxml"), bundle);
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
    private HBox viewedUserBox;
    @FXML
    private ImageView viewedUserImg;
    @FXML
    private Label viewedUserNameLbl;
    @FXML
    private Label viewedUserFollowers;
    @FXML
    private Label viewedUserFollowings;
    @FXML
    private HBox myMusicBox;
    @FXML
    private Button shuffleMusicBtn;
    @FXML
    private TabPane myMusicTabPane;
    @FXML
    private Tab highlightsTab;
    @FXML
    private VBox highlightsVBox;
    @FXML
    private FlowPane highlightsHistoryFP;
    @FXML
    private PlaylistFlowPane highlightsPlaylistFP;
    @FXML
    private Button favPlaylistsBtn;
    @FXML
    private AlbumFlowPane highlightsAlbumFP;
    @FXML
    private Button favAlbumsBtn;
    @FXML
    private ArtistFlowPane highlightsArtistFP;
    @FXML
    private Button favArtistsBtn;
    @FXML
    private Tab favTracksTab;
    @FXML
    private Label favTracksLbl;
    @FXML
    private TrackTable<Track> favTracksTV;
    @FXML
    private Tab myPlaylistsTab;
    @FXML
    private Label playlistsCntLbl;
    @FXML
    private Button addPlaylistBtn;
    @FXML
    private PlaylistFlowPane favPlaylistsFP;
    @FXML
    private Tab favAlbumsTab;
    @FXML
    private Label favAlbumsLbl;
    @FXML
    private AlbumFlowPane favAlbumsFP;
    @FXML
    private Tab favArtistsTab;
    @FXML
    private Label favArtistsLbl;
    @FXML
    private ArtistFlowPane favArtistsFP;
    //</editor-fold>

    @FXML
    void addPlaylistBtn_OnAction(ActionEvent actionEvent) {

    }

    @FXML
    void favAlbumsBtn_OnAction(ActionEvent actionEvent) {
        myMusicTabPane.getSelectionModel().select(favAlbumsTab);
    }

    @FXML
    void favArtistsBtn_OnAction(ActionEvent actionEvent) {
        myMusicTabPane.getSelectionModel().select(favArtistsTab);
    }

    @FXML
    void favPlaylistsBtn_OnAction(ActionEvent actionEvent) {
        myMusicTabPane.getSelectionModel().select(myPlaylistsTab);
    }

    @FXML
    void removeTrackFromFav(ActionEvent event) {

    }

    @FXML
    void shuffleMusicBtn_OnAction(ActionEvent actionEvent) {
    }

    public void setUser(User user, boolean loggedInUser, Deezer deezerClient) {
        if (!loggedInUser){
            viewedUserImg.setImage(new Image(user.getPicture_medium().toString(), true));
            viewedUserNameLbl.setText(user.getName());
        }

        addPlaylistBtn.setVisible(loggedInUser);
        PartialSearchResponse<Track> favTracks = deezerClient.getFavouredTracks(user, null);
        PartialSearchResponse<Playlist> playlists = deezerClient.getFavouredPlaylists(user, null);
        PartialSearchResponse<Album> favAlbums = deezerClient.getFavoredAlbums(user, SearchOrder.ALBUM_ASC);
        PartialSearchResponse<Artist> favArtists = deezerClient.getFavoredArtists(user, SearchOrder.ARTIST_ASC);

        highlightsPlaylistFP.fill(new PartialSearchResponse<>(playlists.getData().stream().limit(4).collect(Collectors.toList())),
                null, true, false);
        highlightsAlbumFP.fill(new PartialSearchResponse<>(favAlbums.getData().stream().limit(4).collect(Collectors.toList())),
                null, true, false);
        highlightsArtistFP.fill(new PartialSearchResponse<>(favArtists.getData().stream().limit(4).collect(Collectors.toList())),
                null, true, false);

        favTracksTV.fill(favTracks, favTracksLbl, true);
        favPlaylistsFP.fill(playlists, playlistsCntLbl, true, true);
        favAlbumsFP.fill(favAlbums, favAlbumsLbl, true, true);
        favArtistsFP.fill(favArtists, favArtistsLbl, true, true);

        myMusicBox.setVisible(loggedInUser);
        viewedUserBox.setVisible(!loggedInUser);
    }

    public void navigateTo(Destinations destination) {
        switch (destination){
            case HIGHLIGHTS:
                myMusicTabPane.getSelectionModel().select(highlightsTab);
                break;
            case TRACKS:
                myMusicTabPane.getSelectionModel().select(favTracksTab);
                break;
            case PLAYLISTS:
                myMusicTabPane.getSelectionModel().select(myPlaylistsTab);
                break;
            case ALBUMS:
                myMusicTabPane.getSelectionModel().select(favAlbumsTab);
                break;
            case ARTISTS:
                myMusicTabPane.getSelectionModel().select(favArtistsTab);
                break;
        }
    }
}
