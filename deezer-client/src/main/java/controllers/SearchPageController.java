package controllers;

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
    private TableView<TrackSearch> tracksResultTV;
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
    private TableView<TrackSearch> foundTracksTV;
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

        boolean found = !searchSet.trackResponse().data().isEmpty();
        if (found) {
            searchTabPane.getTabs().add(trackResultsTab);
        }
        tracksResultTV.getItems().setAll(searchSet.trackResponse().data().stream().limit(6).collect(Collectors.toList()));
        foundTracksTV.getItems().setAll(searchSet.trackResponse().data());
        foundTracksLbl.setText(String.valueOf(searchSet.trackResponse().total()));
        tracksResultBtn.setVisible(found);
        tracksResultTV.setVisible(found);

        found = !searchSet.albumResponse().data().isEmpty();
        if (found) {
            searchTabPane.getTabs().add(albumResultsTab);
        }
        albumsResultBtn.setVisible(found);
        albumsResultsFP.setVisible(found);
        albumsResultsFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < searchSet.albumResponse().data().size(); i++) {
            final Album album = searchSet.albumResponse().data().get(i);
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, albumsResultsFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            albumCard.setAlbumAction(() -> albumRedirectioner.accept(album));
            albumCard.setArtistAction(() -> artistRedirectioner.accept(album.artist()));
            albumsResultsFP.getChildren().add(albumCard);
        }
        foundAlbumsFP.getChildren().clear();
        foundAlbumsLbl.setText(String.valueOf(searchSet.albumResponse().total()));
        for (final Album album: searchSet.albumResponse().data()) {
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, foundAlbumsFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            albumCard.setAlbumAction(() -> albumRedirectioner.accept(album));
            albumCard.setArtistAction(() -> artistRedirectioner.accept(album.artist()));
            foundAlbumsFP.getChildren().add(albumCard);
        }

        found = !searchSet.artistResponse().data().isEmpty();
        if (found) {
            searchTabPane.getTabs().add(artistResultsTab);
        }
        artistsResultBtn.setVisible(found);
        artistsResultsFP.setVisible(found);
        artistsResultsFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < searchSet.artistResponse().data().size(); i++) {
            final Artist artist = searchSet.artistResponse().data().get(i);
            final var artistCard = new ArtistCard();
            artistCard.prefWidthProperty().bind(Bindings.add(-35, artistsResultsFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistCard.setArtist(artist);
            artistCard.setAction(() -> artistRedirectioner.accept(artist));
            artistsResultsFP.getChildren().add(artistCard);
        }
        foundArtistsFP.getChildren().clear();
        foundArtistsLbl.setText(String.valueOf(searchSet.artistResponse().total()));
        for (final Artist artist: searchSet.artistResponse().data()) {
            final var artistCard = new ArtistCard();
            artistCard.prefWidthProperty().bind(Bindings.add(-35, foundArtistsFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistCard.setArtist(artist);
            artistCard.setAction(() -> artistRedirectioner.accept(artist));
            foundArtistsFP.getChildren().add(artistCard);
        }

        found = !searchSet.playlistResponse().data().isEmpty();
        if (found) {
            searchTabPane.getTabs().add(playlistResultsTab);
        }
        playlistsResultsBtn.setVisible(found);
        playlistsResultsFP.setVisible(found);
        playlistsResultsFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < searchSet.playlistResponse().data().size(); i++) {
            final Playlist playlist = searchSet.playlistResponse().data().get(i);
            if (playlist.lovedTrack()) {
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
        foundPlaylistsLabel.setText(String.valueOf(searchSet.playlistResponse().total() - 1));
        for (final Playlist playlist: searchSet.playlistResponse().data()) {
            if (playlist.lovedTrack()) {
                continue;
            }
            final var playlistCard = new PlaylistCard();
            playlistCard.prefWidthProperty().bind(Bindings.add(-35, foundPlaylistsFP.widthProperty().divide(4.2)));
            playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistCard.setPlaylist(playlist);
            playlistCard.setAction(() -> playlistRedirectioner.accept(playlist));
            foundPlaylistsFP.getChildren().add(playlistCard);
        }

        found = !searchSet.userResponse().data().isEmpty();
        if (found) {
            searchTabPane.getTabs().add(profileResultsTab);
        }
        profilesResultsBtn.setVisible(found);
        profilesResultsFP.setVisible(found);
        profilesResultsFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < searchSet.userResponse().data().size(); i++) {
            final User user = searchSet.userResponse().data().get(i);
            final var userCard = new UserCard();
            userCard.prefWidthProperty().bind(Bindings.add(-35, profilesResultsFP.widthProperty().divide(4.2)));
            userCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            userCard.setUser(user);
            userCard.setAction(() -> userRedirectioner.accept(user));
            profilesResultsFP.getChildren().add(userCard);
        }
        foundProfilesFP.getChildren().clear();
        foundProfilesLbl.setText(String.valueOf(searchSet.userResponse().total()));
        for (final User user : searchSet.userResponse().data()) {
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
