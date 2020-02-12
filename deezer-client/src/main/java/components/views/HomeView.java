package components.views;

import api.Deezer;
import api.events.handlers.DeezerListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import static utils.UiUtils.*;

public class HomeView extends VBox {
    @FXML
    private Label alert;
    @FXML
    private VBox recommendationsBox;
    @FXML
    private FlowPane recommendedPlaylistsFP;
    @FXML
    private FlowPane recommendedArtistsFP;
    @FXML
    private FlowPane recommendedAlbumsFP;

    public void setupDeezer(Deezer deezerClient) {
        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(event -> {
            alert.setVisible(!event.isLoggedIn());
            recommendationsBox.setVisible(event.isLoggedIn());
            if (event.isLoggedIn()){
                fillFlowPaneWithPlaylists(recommendedPlaylistsFP, deezerClient.getRecommendedPlaylists(12), null, true, false);
                fillFlowPaneWithArtists(recommendedArtistsFP, deezerClient.getRecommendedArtists(12), null, true, false);
                fillFlowPaneWithAlbums(recommendedAlbumsFP, deezerClient.getRecommendedAlbums(12), null, true, false);
            }
            else {
                recommendedPlaylistsFP.getChildren().clear();
                recommendedArtistsFP.getChildren().clear();
                recommendedAlbumsFP.getChildren().clear();
            }
        }));
    }
}
