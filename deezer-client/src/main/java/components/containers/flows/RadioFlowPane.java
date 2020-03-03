package components.containers.flows;

import api.PartialSearchResponse;
import api.objects.playables.Radio;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class RadioFlowPane extends FlowPane {
    public void fill(PartialSearchResponse<Radio> radios, Label countLabel,
                                              boolean clear, boolean tryGetNext) {
        Platform.runLater(() -> {
            if (clear)
                getChildren().clear();
            for (Radio radio : radios.getData()) {
                VBox radioBox = new VBox();
                radioBox.prefWidthProperty().bind(Bindings.add(-35, widthProperty().divide(4.2)));
                radioBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
                radioBox.getStyleClass().add("radiocard");

                ImageView radioPicture = new ImageView(new Image(radio.getPicture_medium().toString(), true));
                radioPicture.fitWidthProperty().bind(radioBox.prefWidthProperty());
                radioPicture.fitHeightProperty().bind(radioPicture.fitWidthProperty());

                Button radioRedirectButton = new Button(null, radioPicture);
                //radioRedirectButton.setOnAction(event -> redirectToRadio(deezerClient.getRadio(radio.getId())));
                radioRedirectButton.prefWidthProperty().bind(radioPicture.fitWidthProperty());
                radioRedirectButton.prefHeightProperty().bind(radioPicture.fitHeightProperty());
                radioRedirectButton.getStyleClass().add("radiocard-radio");
                radioBox.getChildren().add(radioRedirectButton);
                getChildren().add(radioBox);
            }
            if (!radios.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(radios.getTotal()));
        });
    }
}
