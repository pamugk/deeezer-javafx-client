package controllers;

import api.Deezer;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.TrackSearch;
import components.containers.boxes.CommentBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import utils.TimeUtils;

import java.util.ResourceBundle;

import static api.LoginStatus.NOT_AUTHORIZED;

public class PlaylistPageController {

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
    private CommentBox playlistCommentariesBox;
    //</editor-fold>

    public void setPlaylist(Playlist playlist, Deezer deezerClient) {
        playlistPicture.setImage(new Image(playlist.getPicture_medium().toString(), true));
        playlistTitleLbl.setText(playlist.getTitle());
        playlistCreatorImg.setImage(new Image(playlist.getCreator().getPicture_small().toString(), true));
        playlistCreatorBtn.setText(playlist.getCreator().getName());
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
        playlistCommentariesBox.fill(deezerClient.getPlaylistComments(playlist));
    }

    @FXML
    private void onPlaylistEdit(ActionEvent event) {
    }

    @FXML
    private void playlistSearchBox_OnKeyPressed(KeyEvent keyEvent) {
    }

    @FXML
    private void removeTrackFromPlaylist(ActionEvent event) {

    }
}
