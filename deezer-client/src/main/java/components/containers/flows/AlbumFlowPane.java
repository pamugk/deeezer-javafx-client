package components.containers.flows;

import api.PartialSearchResponse;
import api.objects.playables.Album;
import components.containers.cards.AlbumCard;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class AlbumFlowPane extends FlowPane {
    public void fill(PartialSearchResponse<Album> albums, Label countLabel, boolean clear, boolean tryGetNext) {
        if (clear)
            getChildren().clear();
        for (Album album : albums.getData()) {
            AlbumCard albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            getChildren().add(albumCard);
        }
        if (!albums.hasPrev() && countLabel != null)
            countLabel.setText(String.valueOf(albums.getTotal()));
    }
}
