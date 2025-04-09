package controllers;

import api.Deezer;
import api.PartialSearchResponse;
import api.events.handlers.DeezerListener;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import components.cards.AlbumCard;
import components.cards.ArtistCard;
import components.cards.PlaylistCard;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class HomePageController {
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

    private Consumer<Album> albumRedirectioner = album -> {};
    private Consumer<Artist> artistRedirectioner = artist -> {};
    private Consumer<Playlist> playlistRedirectioner = playlist -> {};

    public void setupDeezer(Deezer deezerClient) {
        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(event -> {
            alert.setVisible(!event.isLoggedIn());
            recommendationsBox.setVisible(event.isLoggedIn());
            if (event.isLoggedIn()) {
                final PartialSearchResponse<Playlist> recommendedPlaylists = deezerClient.getRecommendedPlaylists(12);
                recommendedPlaylistsFP.getChildren().clear();
                for (final Playlist playlist: recommendedPlaylists.data()) {
                    if (playlist.is_loved_track()) {
                        continue;
                    }
                    final var playlistCard = new PlaylistCard();
                    playlistCard.prefWidthProperty().bind(Bindings.add(-35, recommendedPlaylistsFP.widthProperty().divide(4.2)));
                    playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    playlistCard.setPlaylist(playlist);
                    playlistCard.setAction(() -> playlistRedirectioner.accept(playlist));
                    recommendedPlaylistsFP.getChildren().add(playlistCard);
                }

                final PartialSearchResponse<Artist> recommendedArtists = deezerClient.getRecommendedArtists(12);
                recommendedArtistsFP.getChildren().clear();
                for (final Artist artist: recommendedArtists.data()) {
                    final var artistCard = new ArtistCard();
                    artistCard.prefWidthProperty().bind(Bindings.add(-35, recommendedArtistsFP.widthProperty().divide(4.2)));
                    artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    artistCard.setArtist(artist);
                    artistCard.setAction(() -> artistRedirectioner.accept(artist));
                    recommendedArtistsFP.getChildren().add(artistCard);
                }

                final PartialSearchResponse<Album> recommendedAlbums = deezerClient.getRecommendedAlbums(12);
                recommendedAlbumsFP.getChildren().clear();
                for (final Album album: recommendedAlbums.data()) {
                    final var albumCard = new AlbumCard();
                    albumCard.prefWidthProperty().bind(Bindings.add(-35, recommendedAlbumsFP.widthProperty().divide(4.2)));
                    albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    albumCard.setAlbum(album);
                    albumCard.setAlbumAction(() -> albumRedirectioner.accept(album));
                    albumCard.setArtistAction(() -> artistRedirectioner.accept(album.artist()));
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

    public void setAlbumRedirectioner(Consumer<Album> albumRedirectioner) {
        this.albumRedirectioner = albumRedirectioner;
    }

    public void setArtistRedirectioner(Consumer<Artist> artistRedirectioner) {
        this.artistRedirectioner = artistRedirectioner;
    }

    public void setPlaylistRedirectioner(Consumer<Playlist> playlistRedirectioner) {
        this.playlistRedirectioner = playlistRedirectioner;
    }
}
