package components.views;

import api.Deezer;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.TrackSearch;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import utils.TimeUtils;
import utils.UiUtils;

import java.util.ResourceBundle;
import java.util.function.Consumer;

import static api.LoginStatus.NOT_AUTHORIZED;

public class PlaylistView extends VBox {
    private Playlist playlist;
    //<editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private ResourceBundle resources;
    @FXML
    private ImageView playlistPicture;
    @FXML
    private Label playlistTitleLbl;
    @FXML
    private Button playlistCreatorBtn;
    @FXML
    private ImageView playlistCreatorImg;
    @FXML
    private Label playlistDescriptionLbl;
    @FXML
    private Label playlistTracksCountLbl;
    @FXML
    private Label playlistDurationLbl;
    @FXML
    private Label playlistFollowersLbl;
    @FXML
    private Button playlistListenBtn;
    @FXML
    private Button playlistAddToLibBtn;
    @FXML
    private Button editPlaylistBtn;
    @FXML
    private ImageView playlistFollowingImg;
    @FXML
    private TextField playlistSearchBox;
    @FXML
    private TableView<TrackSearch> playlistTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> playlistTrackIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> playlistTrackTitleCol;
    @FXML
    private TableColumn<TrackSearch, Artist> playlistTrackArtistCol;
    @FXML
    private TableColumn<TrackSearch, Album> playlistTrackAlbumCol;
    @FXML
    private TableColumn<TrackSearch, Integer> playlistTrackLengthCol;
    @FXML
    private TableColumn<TrackSearch, Integer> playlistTrackPopularityCol;
    @FXML
    private VBox playlistCommentariesBox;
    //</editor-fold>

    @FXML
    private void onPlaylistEdit(ActionEvent event) {
        getPlaylistEditor().accept(playlist);
    }

    @FXML
    private void playlistSearchBox_OnKeyPressed(KeyEvent keyEvent) {
    }

    @FXML
    private void removeTrackFromPlaylist(ActionEvent event) {

    }

    public void setPlaylist(Playlist playlist, Deezer deezerClient){
        this.playlist = playlist;
        playlistPicture.setImage(new Image(playlist.getPicture_medium().toString(), true));
        playlistTitleLbl.setText(playlist.getTitle());
        playlistCreatorImg.setImage(new Image(playlist.getCreator().getPicture_small().toString(), true));
        playlistCreatorBtn.setText(playlist.getCreator().getName());
        playlistCreatorBtn.setOnAction(event -> getUserRedirectioner().accept(playlist.getCreator().getId()));
        playlistDescriptionLbl.setText(playlist.getDescription());
        playlistTracksCountLbl.setText(String.format("%s: %d",
                resources.getString("tracksCnt"), playlist.getNb_tracks()));
        playlistDurationLbl.setText(TimeUtils.secondsToNormalTime(playlist.getDuration(), resources));
        playlistFollowersLbl.setText(String.format("%s: %d", resources.getString("followers"), playlist.getFans()));
        if (deezerClient.getLoginStatus() == NOT_AUTHORIZED)
            playlistAddToLibBtn.setVisible(false);
        else {
            playlistAddToLibBtn.setVisible(true);
            playlistFollowingImg.setImage(new Image("src/main/resources/img/icon-like.png"));
        }
        playlistTracksTV.getItems().clear();
        playlistTracksTV.getItems().addAll(playlist.getTracks().getData());
        UiUtils.fillVBoxWithComments(playlistCommentariesBox, deezerClient.getPlaylistComments(playlist));
    }

    public final ObjectProperty<Consumer<Long>> userRedirectionerProperty() { return userRedirectioner; }
    public final void setUserRedirectioner(Consumer<Long> value) { userRedirectionerProperty().set(value); }
    public final Consumer<Long> getUserRedirectioner() { return userRedirectionerProperty().get(); }
    private ObjectProperty<Consumer<Long>> userRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return PlaylistView.this;
        }

        @Override
        public String getName() {
            return "userRedirectioner";
        }
    };

    public final ObjectProperty<Consumer<Playlist>> playlistEditorProperty() { return playlistEditor; }
    public final void setPlaylistEditor(Consumer<Playlist> value) { playlistEditorProperty().set(value); }
    public final Consumer<Playlist> getPlaylistEditor() { return playlistEditorProperty().get(); }
    private ObjectProperty<Consumer<Playlist>> playlistEditor = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return PlaylistView.this;
        }

        @Override
        public String getName() {
            return "playlistEditor";
        }
    };
}
