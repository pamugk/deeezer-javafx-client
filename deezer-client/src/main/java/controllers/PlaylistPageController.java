package controllers;

import api.Deezer;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.User;
import components.cards.CommentCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import utils.TimeUtils;

import java.util.ResourceBundle;
import java.util.function.Consumer;

import static api.LoginStatus.NOT_AUTHORIZED;

public class PlaylistPageController {

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
    private Button playlistAddToLibBtn;
    @FXML
    private ImageView playlistFollowingImg;
    @FXML
    private TableView<Track> playlistTracksTV;
    @FXML
    private VBox playlistCommentariesBox;

    private Consumer<User> userRedirectioner = user -> {};

    public void fillData(Playlist playlist, Deezer deezerClient) {
        playlistPicture.setImage(new Image(playlist.pictureMedium().toString(), true));
        playlistTitleLbl.setText(playlist.title());
        playlistCreatorImg.setImage(new Image(playlist.creator().pictureSmall().toString(), true));
        playlistCreatorBtn.setText(playlist.creator().name());
        playlistDescriptionLbl.setText(playlist.description());
        playlistTracksCountLbl.setText(String.format("%s: %d",
                resources.getString("tracksCnt"), playlist.trackCount()));
        playlistDurationLbl.setText(TimeUtils.secondsToNormalTime(playlist.duration(), resources));
        playlistFollowersLbl.setText(String.format("%s: %d", resources.getString("followers"), playlist.fans()));
        if (deezerClient.getLoginStatus() == NOT_AUTHORIZED)
            playlistAddToLibBtn.setVisible(false);
        else {
            playlistAddToLibBtn.setVisible(true);
            playlistFollowingImg.setImage(new Image("src/main/resources/img/icon-like.png"));
        }
        playlistTracksTV.getItems().setAll(playlist.tracks().data());
        playlistCommentariesBox.getChildren().clear();
        for (final Comment comment : deezerClient.getPlaylistComments(playlist).data()) {
            CommentCard commentCard = new CommentCard();
            commentCard.setComment(comment);
            commentCard.setUserAction(() -> userRedirectioner.accept(comment.author()));
            playlistCommentariesBox.getChildren().add(commentCard);
        }
    }

    public void setUserRedirectioner(Consumer<User> userRedirectioner) {
        this.userRedirectioner = userRedirectioner;
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
