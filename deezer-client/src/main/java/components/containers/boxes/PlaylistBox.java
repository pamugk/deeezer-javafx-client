package components.containers.boxes;

import api.objects.playables.Playlist;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class PlaylistBox extends VBox {
    public void fill(List<Playlist> playlists) {
        getChildren().clear();
        for (Playlist playlist : playlists) {
            HBox playlistBox = new HBox();
            playlistBox.setPadding(new Insets(0, 10, 0, 0));
            playlistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            ImageView playlistPicture = new ImageView(new Image(playlist.getPicture_small().toString(), true));
            playlistPicture.setFitWidth(56);
            playlistPicture.setFitHeight(56);
            Button playlistBtn = new Button(null, playlistPicture);
            //playlistBtn.setOnAction(event -> redirectToPlaylist(deezerClient.getPlaylist(playlist.getId())));
            playlistBtn.getStyleClass().add("deezer-button");
            playlistBox.getChildren().addAll(playlistBtn);

            VBox playlistInfoBox = new VBox();
            playlistInfoBox.setPadding(new Insets(10, 0, 0, 0));
            playlistInfoBox.setAlignment(Pos.CENTER);
            playlistInfoBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistInfoBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            playlistBox.getChildren().add(playlistInfoBox);

            Label playlistTitle = new Label(playlist.getTitle());
            playlistTitle.getStyleClass().add("deezer-secondary");
            playlistInfoBox.getChildren().add(playlistTitle);

            /*Label playlistFollowers = new Label(String.format("%s: %d/%s: %d",
                    resources.getString("tracks"), playlist.getNb_tracks(),
                    resources.getString("followers"), playlist.getFans()));
            playlistFollowers.getStyleClass().add("deezer-secondary");
            playlistInfoBox.getChildren().add(playlistFollowers);*/

            getChildren().add(playlistBox);
        }
    }
}
