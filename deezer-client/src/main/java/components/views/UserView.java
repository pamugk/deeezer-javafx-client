package components.views;

import api.Deezer;
import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.Track;
import api.objects.utils.User;
import api.objects.utils.search.SearchOrder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.UiUtils;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static utils.UiUtils.*;
import static utils.UiUtils.fillFlowPaneWithArtists;

public class UserView extends VBox {
    public enum Destinations{
        HIGHLIGHTS,
        TRACKS,
        PLAYLISTS,
        ALBUMS,
        ARTISTS,
    }

    private User user;

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
    private FlowPane highlightsPlaylistFP;
    @FXML
    private Button favPlaylistsBtn;
    @FXML
    private FlowPane highlightsAlbumFP;
    @FXML
    private Button favAlbumsBtn;
    @FXML
    private FlowPane highlightsArtistFP;
    @FXML
    private Button favArtistsBtn;
    @FXML
    private Tab favTracksTab;
    @FXML
    private Label favTracksLbl;
    @FXML
    private TableView<Track> favTracksTV;
    @FXML
    private TableColumn<Track, Track> favTrackIdxCol;
    @FXML
    private TableColumn<Track, String> favTrackTitleCol;
    @FXML
    private TableColumn<Track, Artist> favTrackArtistCol;
    @FXML
    private TableColumn<Track, Album> favTrackAlbumCol;
    @FXML
    private TableColumn<Track, Integer> favTrackLengthCol;
    @FXML
    private TableColumn<Track, Integer> favTrackPopCol;
    @FXML
    private Tab myPlaylistsTab;
    @FXML
    private Label playlistsCntLbl;
    @FXML
    private Button addPlaylistBtn;
    @FXML
    private FlowPane favPlaylistsFP;
    @FXML
    private Tab favAlbumsTab;
    @FXML
    private Label favAlbumsLbl;
    @FXML
    private FlowPane favAlbumsFP;
    @FXML
    private Tab favArtistsTab;
    @FXML
    private Label favArtistsLbl;
    @FXML
    private FlowPane favArtistsFP;
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
        this.user = user;
        if (!loggedInUser){
            viewedUserImg.setImage(new Image(user.getPicture_medium().toString(), true));
            viewedUserNameLbl.setText(user.getName());
        }

        addPlaylistBtn.setVisible(loggedInUser);
        PartialSearchResponse<Track> favTracks = deezerClient.getFavouredTracks(user, null);
        PartialSearchResponse<Playlist> playlists = deezerClient.getFavouredPlaylists(user, null);
        PartialSearchResponse<Album> favAlbums = deezerClient.getFavoredAlbums(user, SearchOrder.ALBUM_ASC);
        PartialSearchResponse<Artist> favArtists = deezerClient.getFavoredArtists(user, SearchOrder.ARTIST_ASC);

        fillFlowPaneWithPlaylists(highlightsPlaylistFP,
                new PartialSearchResponse<>(playlists.getData().stream().limit(4).collect(Collectors.toList())),
                null, true, false);
        fillFlowPaneWithAlbums(highlightsAlbumFP,
                new PartialSearchResponse<>(favAlbums.getData().stream().limit(4).collect(Collectors.toList())),
                null, true, false);
        fillFlowPaneWithArtists(highlightsArtistFP,
                new PartialSearchResponse<>(favArtists.getData().stream().limit(4).collect(Collectors.toList())),
                null, true, false);

        favTracksTV.getItems().clear();
        UiUtils.fillTableWithTracks(favTracksTV, favTracks, favTracksLbl);
        fillFlowPaneWithPlaylists(favPlaylistsFP, playlists, playlistsCntLbl, true, true);
        fillFlowPaneWithAlbums(favAlbumsFP, favAlbums, favAlbumsLbl, true, true);
        fillFlowPaneWithArtists(favArtistsFP, favArtists, favArtistsLbl, true, true);

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
