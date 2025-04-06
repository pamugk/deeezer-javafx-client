package components.views;

import api.Deezer;
import api.PartialSearchResponse;
import api.events.handlers.DeezerListener;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import components.containers.cards.AlbumCard;
import components.containers.cards.ArtistCard;
import components.containers.cards.PlaylistCard;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
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
    private FlowPane recommendedPlaylistsFP;
    @FXML
    private FlowPane recommendedArtistsFP;
    @FXML
    private FlowPane recommendedAlbumsFP;

    public void setupDeezer(Deezer deezerClient) {
        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(event -> {
            alert.setVisible(!event.isLoggedIn());
            recommendationsBox.setVisible(event.isLoggedIn());
            if (event.isLoggedIn()) {
                final PartialSearchResponse<Playlist> recommendedPlaylists = deezerClient.getRecommendedPlaylists(12);
                recommendedPlaylistsFP.getChildren().clear();
                for (final Playlist playlist: recommendedPlaylists.getData()) {
                    if (playlist.is_loved_track()) {
                        continue;
                    }
                    final var playlistCard = new PlaylistCard();
                    playlistCard.setPlaylist(playlist);
                    playlistCard.prefWidthProperty().bind(Bindings.add(-35, recommendedPlaylistsFP.widthProperty().divide(4.2)));
                    playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    recommendedPlaylistsFP.getChildren().add(playlistCard);
                }

                final PartialSearchResponse<Artist> recommendedArtists = deezerClient.getRecommendedArtists(12);
                recommendedArtistsFP.getChildren().clear();
                for (final Artist artist: recommendedArtists.getData()) {
                    final var artistCard = new ArtistCard();
                    artistCard.setArtist(artist);
                    artistCard.prefWidthProperty().bind(Bindings.add(-35, recommendedArtistsFP.widthProperty().divide(4.2)));
                    artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    recommendedArtistsFP.getChildren().add(artistCard);
                }

                final PartialSearchResponse<Album> recommendedAlbums = deezerClient.getRecommendedAlbums(12);
                recommendedAlbumsFP.getChildren().clear();
                for (final Album album: recommendedAlbums.getData()) {
                    final var albumCard = new AlbumCard();
                    albumCard.prefWidthProperty().bind(Bindings.add(-35, recommendedAlbumsFP.widthProperty().divide(4.2)));
                    albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    albumCard.setAlbum(album);
                    recommendedAlbumsFP.getChildren().add(albumCard);
                }
            }
            else {
                recommendedPlaylistsFP.getChildren().clear();
                recommendedArtistsFP.getChildren().clear();
                recommendedAlbumsFP.getChildren().clear();
            }
        }));
    }
}
