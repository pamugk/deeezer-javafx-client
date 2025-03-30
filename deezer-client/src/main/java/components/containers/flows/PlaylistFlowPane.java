package components.containers.flows;

import api.PartialSearchResponse;
import api.objects.playables.Playlist;
import components.containers.cards.PlaylistCard;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

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
            playlistCard.setPlaylistRedirectioner(getPlaylistRedirectioner());
            getChildren().add(playlistCard);
        }
        if (!playlists.hasPrev() && countLabel != null)
            countLabel.setText(String.valueOf(playlists.getTotal() - 1));
    }

    public final ObjectProperty<Consumer<Long>> playlistRedirectionerProperty() {
        return playlistRedirectioner;
    }

    public final void setPlaylistRedirectioner(Consumer<Long> value) {
        playlistRedirectionerProperty().set(value);
    }

    public final Consumer<Long> getPlaylistRedirectioner() {
        return playlistRedirectionerProperty().get();
    }

    private final ObjectProperty<Consumer<Long>> playlistRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return PlaylistFlowPane.this;
        }

        @Override
        public String getName() {
            return "playlistRedirectioner";
        }
    };
}
