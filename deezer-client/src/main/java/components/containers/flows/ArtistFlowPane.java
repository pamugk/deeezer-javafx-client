package components.containers.flows;

import api.PartialSearchResponse;
import api.objects.playables.Artist;
import components.containers.cards.ArtistCard;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

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
                getChildren().add(artistCard);
            }
            if (!artists.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(artists.getTotal()));
        });
    }
}