package components.containers.flows;

import api.PartialSearchResponse;
import api.objects.playables.Artist;
import components.containers.cards.ArtistCard;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

public class ArtistFlowPane extends FlowPane {
    public void fill(PartialSearchResponse<Artist> artists, Label countLabel, boolean clear, boolean tryGetNext) {
        Platform.runLater(() -> {
            if (clear)
                getChildren().clear();
            for (Artist artist : artists.getData()) {
                ArtistCard artistCard = new ArtistCard();
                artistCard.setArtist(artist);
                artistCard.prefWidthProperty().bind(Bindings.add(-35, widthProperty().divide(4.2)));
                artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
                artistCard.setArtistRedirectioner(getArtistRedirectioner());
                getChildren().add(artistCard);
            }
            if (!artists.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(artists.getTotal()));
        });
    }

    public final ObjectProperty<Consumer<Long>> artistRedirectionerProperty() { return artistRedirectioner; }
    public final void setArtistRedirectioner(Consumer<Long> value) { artistRedirectionerProperty().set(value); }
    public final Consumer<Long> getArtistRedirectioner() { return artistRedirectionerProperty().get(); }
    private ObjectProperty<Consumer<Long>> artistRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return ArtistFlowPane.this;
        }

        @Override
        public String getName() {
            return "artistRedirectioner";
        }
    };
}