package components.views;

import api.Deezer;
import api.events.handlers.DeezerListener;
import components.containers.flows.AlbumFlowPane;
import components.containers.flows.ArtistFlowPane;
import components.containers.flows.PlaylistFlowPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;

public class HomeView extends VBox {
    public HomeView() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homeView.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private Label alert;
    @FXML
    private VBox recommendationsBox;
    @FXML
    private PlaylistFlowPane recommendedPlaylistsFP;
    @FXML
    private ArtistFlowPane recommendedArtistsFP;
    @FXML
    private AlbumFlowPane recommendedAlbumsFP;

    public void setupDeezer(Deezer deezerClient) {
        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(event -> {
            alert.setVisible(!event.isLoggedIn());
            recommendationsBox.setVisible(event.isLoggedIn());
            if (event.isLoggedIn()){
                recommendedPlaylistsFP.fill(deezerClient.getRecommendedPlaylists(12), null, true, false);
                recommendedArtistsFP.fill(deezerClient.getRecommendedArtists(12), null, true, false);
                recommendedAlbumsFP.fill(deezerClient.getRecommendedAlbums(12), null, true, false);
            }
            else {
                recommendedPlaylistsFP.getChildren().clear();
                recommendedArtistsFP.getChildren().clear();
                recommendedAlbumsFP.getChildren().clear();
            }
        }));
    }
}
