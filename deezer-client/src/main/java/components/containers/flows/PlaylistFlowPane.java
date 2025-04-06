package components.containers.flows;

import api.PartialSearchResponse;
import api.objects.playables.Playlist;
import components.containers.cards.PlaylistCard;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class PlaylistFlowPane extends FlowPane {

    public void fill(PartialSearchResponse<Playlist> playlists, Label countLabel,
                                                 boolean clear, boolean tryGetNext) {
        if (clear)
            getChildren().clear();
        for (Playlist playlist : playlists.getData()) {
            if (playlist.is_loved_track())
                continue;
            PlaylistCard playlistCard = new PlaylistCard();
            playlistCard.setPlaylist(playlist);
            playlistCard.prefWidthProperty().bind(Bindings.add(-35, widthProperty().divide(4.2)));
            playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            getChildren().add(playlistCard);
        }
        if (!playlists.hasPrev() && countLabel != null)
            countLabel.setText(String.valueOf(playlists.getTotal() - 1));
    }
}
