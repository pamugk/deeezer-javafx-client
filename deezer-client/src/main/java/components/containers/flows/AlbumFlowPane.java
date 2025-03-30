package components.containers.flows;

import api.PartialSearchResponse;
import api.objects.playables.Album;
import components.containers.cards.AlbumCard;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

public class AlbumFlowPane extends FlowPane {
    public void fill(PartialSearchResponse<Album> albums, Label countLabel, boolean clear, boolean tryGetNext) {
        if (clear)
            getChildren().clear();
        for (Album album : albums.getData()) {
            AlbumCard albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            albumCard.setAlbumRedirectioner(getAlbumRedirectioner());
            albumCard.setArtistRedirectioner(getArtistRedirectioner());
            getChildren().add(albumCard);
        }
        if (!albums.hasPrev() && countLabel != null)
            countLabel.setText(String.valueOf(albums.getTotal()));
    }

    public final ObjectProperty<Consumer<Long>> albumRedirectionerProperty() {
        return albumRedirectioner;
    }

    public final void setAlbumRedirectioner(Consumer<Long> value) {
        albumRedirectionerProperty().set(value);
    }

    public final Consumer<Long> getAlbumRedirectioner() {
        return albumRedirectionerProperty().get();
    }

    private final ObjectProperty<Consumer<Long>> albumRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return AlbumFlowPane.this;
        }

        @Override
        public String getName() {
            return "albumRedirectioner";
        }
    };

    public final ObjectProperty<Consumer<Long>> artistRedirectionerProperty() {
        return artistRedirectioner;
    }

    public final void setArtistRedirectioner(Consumer<Long> value) {
        artistRedirectionerProperty().set(value);
    }

    public final Consumer<Long> getArtistRedirectioner() {
        return artistRedirectionerProperty().get();
    }

    private final ObjectProperty<Consumer<Long>> artistRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return AlbumFlowPane.this;
        }

        @Override
        public String getName() {
            return "artistRedirectioner";
        }
    };
}
