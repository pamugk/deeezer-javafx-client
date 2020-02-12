package components;

import api.objects.playables.TrackSearch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class MusicPlayer extends HBox {
    private TrackSearch selectedTrack;

    public MusicPlayer() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("musicPlayer.fxml"));
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private HBox trackInfoBox;
    @FXML
    private Hyperlink trackLink;
    @FXML
    private Hyperlink artistLink;
    @FXML
    private Button textBtn;
    @FXML
    private Button addToPlaylistBtn;
    @FXML
    private Button likeBtn;
    @FXML
    private Slider trackSlider;
    @FXML
    private Label durationLabel;
    @FXML
    private Button repeatBtn;
    @FXML
    private Button shuffleBtn;
    @FXML
    private Button soundBtn;
    @FXML
    private ImageView soundImg;
    @FXML
    private ImageView audioSettingsBtn;
    @FXML
    private ImageView playingImg;
    //</editor-fold>
    @FXML
    void addToPlaylistBtn_OnAction(ActionEvent event) {
        /*ChoiceDialog<Playlist> playlistChoicer = new ChoiceDialog<>(null,
                deezerClient.getFavouredPlaylists(deezerClient.getLoggedInUser(), null).getData());
        playlistChoicer.showAndWait().ifPresent(playlist -> {
            if (deezerClient.addTracksToPlaylist(playlist, Collections.singletonList(musicPlayer.getSelectedTrack())))
                new Alert(Alert.AlertType.INFORMATION, "Трек успешно добавлен в плейлист").showAndWait();
        });*/
    }

    @FXML
    void shuffleBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void repeatBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void textBtn_OnAction(ActionEvent event) {

    }

    public TrackSearch getSelectedTrack(){
        return selectedTrack;
    }

    public void setSelectedTrack(TrackSearch newTrack){
        selectedTrack = newTrack;
        if (newTrack == null) {
            trackLink.setText("");
            artistLink.setText("");
            trackInfoBox.setVisible(false);
            addToPlaylistBtn.setDisable(true);
        }
        else {
            trackLink.setText(newTrack.getTitle());
            artistLink.setText(newTrack.getArtist().getName());
            trackInfoBox.setVisible(true);
            addToPlaylistBtn.setDisable(false);
        }
    }
}
