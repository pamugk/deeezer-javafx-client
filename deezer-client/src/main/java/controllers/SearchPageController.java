package controllers;

import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.TrackSearch;
import api.objects.utils.User;
import api.objects.utils.search.FullSearchSet;
import components.cards.AlbumCard;
import components.cards.ArtistCard;
import components.cards.PlaylistCard;
import components.cards.UserCard;
import components.tables.TrackTable;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SearchPageController {
    private static final int HIGHLIGHTS_LIMIT = 4;

    @FXML
    private TabPane searchTabPane;
    @FXML
    private Tab allResultsTab;
    @FXML
    private Button tracksResultBtn;
    @FXML
    private TrackTable<TrackSearch> tracksResultTV;
    @FXML
    private Button albumsResultBtn;
    @FXML
    private FlowPane artistsResultsFP;
    @FXML
    private FlowPane albumsResultsFP;
    @FXML
    private Button artistsResultBtn;
    @FXML
    private Button playlistsResultsBtn;
    @FXML
    private FlowPane playlistsResultsFP;
    @FXML
    private Button profilesResultsBtn;
    @FXML
    private FlowPane profilesResultsFP;
    @FXML
    private Tab trackResultsTab;
    @FXML
    private Label foundTracksLbl;
    @FXML
    private TrackTable<TrackSearch> foundTracksTV;
    @FXML
    private Tab albumResultsTab;
    @FXML
    private Label foundAlbumsLbl;
    @FXML
    private FlowPane foundAlbumsFP;
    @FXML
    private Tab artistResultsTab;
    @FXML
    private Label foundArtistsLbl;
    @FXML
    private FlowPane foundArtistsFP;
    @FXML
    private Tab playlistResultsTab;
    @FXML
    private Label foundPlaylistsLabel;
    @FXML
    private FlowPane foundPlaylistsFP;
    @FXML
    private Tab profileResultsTab;
    @FXML
    private Label foundProfilesLbl;
    @FXML
    private FlowPane foundProfilesFP;

    private Consumer<Album> albumRedirectioner = album -> {};
    private Consumer<Artist> artistRedirectioner = artist -> {};
    private Consumer<Playlist> playlistRedirectioner = playlist -> {};
    private Consumer<User> userRedirectioner = user -> {};

    public void fillData(FullSearchSet searchSet) {
        searchTabPane.getTabs().clear();
        searchTabPane.getTabs().add(allResultsTab);

        boolean found = !searchSet.getTrackResponse().getData().isEmpty();
        if (found) {
            tracksResultTV.fill(
                    new PartialSearchResponse<>(searchSet.getTrackResponse().getData().stream().limit(6).collect(Collectors.toList())),
                    null, true);
            searchTabPane.getTabs().add(trackResultsTab);
        }
        foundTracksTV.fill(searchSet.getTrackResponse(), foundTracksLbl, true);
        tracksResultBtn.setVisible(found);
        tracksResultTV.setVisible(found);

        found = !searchSet.getAlbumResponse().getData().isEmpty();
        if (found) {
            searchTabPane.getTabs().add(albumResultsTab);
        }
        albumsResultBtn.setVisible(found);
        albumsResultsFP.setVisible(found);
        albumsResultsFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < searchSet.getAlbumResponse().getData().size(); i++) {
            final Album album = searchSet.getAlbumResponse().getData().get(i);
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, albumsResultsFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            albumCard.setAlbumAction(() -> albumRedirectioner.accept(album));
            albumCard.setArtistAction(() -> artistRedirectioner.accept(album.getArtist()));
            albumsResultsFP.getChildren().add(albumCard);
        }
        foundAlbumsFP.getChildren().clear();
        foundAlbumsLbl.setText(String.valueOf(searchSet.getAlbumResponse().getTotal()));
        for (final Album album: searchSet.getAlbumResponse().getData()) {
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, foundAlbumsFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            albumCard.setAlbumAction(() -> albumRedirectioner.accept(album));
            albumCard.setArtistAction(() -> artistRedirectioner.accept(album.getArtist()));
            foundAlbumsFP.getChildren().add(albumCard);
        }

        found = !searchSet.getArtistResponse().getData().isEmpty();
        if (found) {
            searchTabPane.getTabs().add(artistResultsTab);
        }
        artistsResultBtn.setVisible(found);
        artistsResultsFP.setVisible(found);
        artistsResultsFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < searchSet.getArtistResponse().getData().size(); i++) {
            final Artist artist = searchSet.getArtistResponse().getData().get(i);
            final var artistCard = new ArtistCard();
            artistCard.prefWidthProperty().bind(Bindings.add(-35, artistsResultsFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistCard.setArtist(artist);
            artistCard.setAction(() -> artistRedirectioner.accept(artist));
            artistsResultsFP.getChildren().add(artistCard);
        }
        foundArtistsFP.getChildren().clear();
        foundArtistsLbl.setText(String.valueOf(searchSet.getArtistResponse().getTotal()));
        for (final Artist artist: searchSet.getArtistResponse().getData()) {
            final var artistCard = new ArtistCard();
            artistCard.prefWidthProperty().bind(Bindings.add(-35, foundArtistsFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistCard.setArtist(artist);
            artistCard.setAction(() -> artistRedirectioner.accept(artist));
            foundArtistsFP.getChildren().add(artistCard);
        }

        found = !searchSet.getPlaylistResponse().getData().isEmpty();
        if (found) {
            searchTabPane.getTabs().add(playlistResultsTab);
        }
        playlistsResultsBtn.setVisible(found);
        playlistsResultsFP.setVisible(found);
        playlistsResultsFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < searchSet.getPlaylistResponse().getData().size(); i++) {
            final Playlist playlist = searchSet.getPlaylistResponse().getData().get(i);
            if (playlist.is_loved_track()) {
                continue;
            }
            final var playlistCard = new PlaylistCard();
            playlistCard.prefWidthProperty().bind(Bindings.add(-35, playlistsResultsFP.widthProperty().divide(4.2)));
            playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistCard.setPlaylist(playlist);
            playlistCard.setAction(() -> playlistRedirectioner.accept(playlist));
            playlistsResultsFP.getChildren().add(playlistCard);
        }
        foundPlaylistsFP.getChildren().clear();
        foundPlaylistsLabel.setText(String.valueOf(searchSet.getPlaylistResponse().getTotal() - 1));
        for (final Playlist playlist: searchSet.getPlaylistResponse().getData()) {
            if (playlist.is_loved_track()) {
                continue;
            }
            final var playlistCard = new PlaylistCard();
            playlistCard.prefWidthProperty().bind(Bindings.add(-35, foundPlaylistsFP.widthProperty().divide(4.2)));
            playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistCard.setPlaylist(playlist);
            playlistCard.setAction(() -> playlistRedirectioner.accept(playlist));
            foundPlaylistsFP.getChildren().add(playlistCard);
        }

        found = !searchSet.getUserResponse().getData().isEmpty();
        if (found) {
            searchTabPane.getTabs().add(profileResultsTab);
        }
        profilesResultsBtn.setVisible(found);
        profilesResultsFP.setVisible(found);
        profilesResultsFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < searchSet.getUserResponse().getData().size(); i++) {
            final User user = searchSet.getUserResponse().getData().get(i);
            final var userCard = new UserCard();
            userCard.prefWidthProperty().bind(Bindings.add(-35, profilesResultsFP.widthProperty().divide(4.2)));
            userCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            userCard.setUser(user);
            userCard.setAction(() -> userRedirectioner.accept(user));
            profilesResultsFP.getChildren().add(userCard);
        }
        foundProfilesFP.getChildren().clear();
        foundProfilesLbl.setText(String.valueOf(searchSet.getUserResponse().getTotal()));
        for (final User user : searchSet.getUserResponse().getData()) {
            final var userCard = new UserCard();
            userCard.prefWidthProperty().bind(Bindings.add(-35, foundProfilesFP.widthProperty().divide(4.2)));
            userCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            userCard.setUser(user);
            userCard.setAction(() -> userRedirectioner.accept(user));
            foundProfilesFP.getChildren().add(userCard);
        }
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

    public void setUserRedirectioner(Consumer<User> userRedirectioner) {
        this.userRedirectioner = userRedirectioner;
    }

    @FXML
    private void albumsResultBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(albumResultsTab);
    }

    @FXML
    private void artistsResultBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(artistResultsTab);
    }

    @FXML
    private void playlistsResultsBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(playlistResultsTab);
    }

    @FXML
    private void profilesResultsBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(profileResultsTab);
    }

    @FXML
    private void tracksResultBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(trackResultsTab);
    }
}
