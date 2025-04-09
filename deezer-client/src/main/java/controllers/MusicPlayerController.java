package controllers;

import api.objects.playables.TrackSearch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class MusicPlayerController {
    private TrackSearch selectedTrack;

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

    public TrackSearch getSelectedTrack() {
        return selectedTrack;
    }

    public void setSelectedTrack(TrackSearch newTrack) {
        selectedTrack = newTrack;
        if (newTrack == null) {
            trackLink.setText("");
            artistLink.setText("");
            trackInfoBox.setVisible(false);
            addToPlaylistBtn.setDisable(true);
        }
        else {
            trackLink.setText(newTrack.title());
            artistLink.setText(newTrack.artist().name());
            trackInfoBox.setVisible(true);
            addToPlaylistBtn.setDisable(false);
        }
    }

    @FXML
    private void addToPlaylistBtn_OnAction(ActionEvent event) {
    }

    @FXML
    private void likeBtn_OnAction(ActionEvent event) {

    }

    @FXML
    private void shuffleBtn_OnAction(ActionEvent event) {

    }

    @FXML
    private void repeatBtn_OnAction(ActionEvent event) {

    }

    @FXML
    private void textBtn_OnAction(ActionEvent event) {

    }
}
