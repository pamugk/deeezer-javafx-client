package controllers;

import api.Deezer;
import api.objects.utils.search.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.Track;
import api.objects.utils.User;
import api.objects.utils.search.SearchOrder;
import components.cards.AlbumCard;
import components.cards.ArtistCard;
import components.cards.PlaylistCard;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class UserPageController {
    public enum Destinations{
        HIGHLIGHTS,
        TRACKS,
        PLAYLISTS,
        ALBUMS,
        ARTISTS,
    }

    private static final int HIGHLIGHTS_LIMIT = 4;

    @FXML
    private HBox viewedUserBox;
    @FXML
    private ImageView viewedUserImg;
    @FXML
    private Label viewedUserNameLbl;
    @FXML
    private Label viewedUserFollowers;
    @FXML
    private Label viewedUserFollowings;
    @FXML
    private HBox myMusicBox;
    @FXML
    private Button shuffleMusicBtn;
    @FXML
    private TabPane myMusicTabPane;
    @FXML
    private Tab highlightsTab;
    @FXML
    private VBox highlightsVBox;
    @FXML
    private FlowPane highlightsHistoryFP;
    @FXML
    private FlowPane highlightsPlaylistFP;
    @FXML
    private Button favPlaylistsBtn;
    @FXML
    private FlowPane highlightsAlbumFP;
    @FXML
    private Button favAlbumsBtn;
    @FXML
    private FlowPane highlightsArtistFP;
    @FXML
    private Button favArtistsBtn;
    @FXML
    private Tab favTracksTab;
    @FXML
    private Label favTracksLbl;
    @FXML
    private TableView<Track> favTracksTV;
    @FXML
    private Tab myPlaylistsTab;
    @FXML
    private Label playlistsCntLbl;
    @FXML
    private Button addPlaylistBtn;
    @FXML
    private FlowPane favPlaylistsFP;
    @FXML
    private Tab favAlbumsTab;
    @FXML
    private Label favAlbumsLbl;
    @FXML
    private FlowPane favAlbumsFP;
    @FXML
    private Tab favArtistsTab;
    @FXML
    private Label favArtistsLbl;
    @FXML
    private FlowPane favArtistsFP;

    private Consumer<Album> albumRedirectioner = album -> {};
    private Consumer<Artist> artistRedirectioner = artist -> {};
    private Consumer<Playlist> playlistRedirectioner = playlist -> {};

    public void fillData(User user, boolean loggedInUser, Deezer deezerClient) {
        if (!loggedInUser) {
            viewedUserImg.setImage(new Image(user.pictureMedium().toString(), true));
            viewedUserNameLbl.setText(user.name());
        }

        addPlaylistBtn.setVisible(loggedInUser);
        PartialSearchResponse<Track> favTracks = deezerClient.getFavouredTracks(user, null);
        PartialSearchResponse<Playlist> playlists = deezerClient.getFavouredPlaylists(user, null);
        PartialSearchResponse<Album> favAlbums = deezerClient.getFavoredAlbums(user, SearchOrder.ALBUM_ASC);
        PartialSearchResponse<Artist> favArtists = deezerClient.getFavoredArtists(user, SearchOrder.ARTIST_ASC);

        highlightsPlaylistFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < playlists.data().size(); i++) {
            final Playlist playlist = playlists.data().get(i);
            if (playlist.lovedTrack()) {
                continue;
            }
            final var playlistCard = new PlaylistCard();
            playlistCard.prefWidthProperty().bind(Bindings.add(-35, highlightsPlaylistFP.widthProperty().divide(4.2)));
            playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistCard.setPlaylist(playlist);
            playlistCard.setAction(() -> playlistRedirectioner.accept(playlist));
            highlightsPlaylistFP.getChildren().add(playlistCard);
        }

        highlightsAlbumFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < favAlbums.data().size(); i++) {
            final Album album = favAlbums.data().get(i);
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, highlightsAlbumFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            albumCard.setAlbumAction(() -> albumRedirectioner.accept(album));
            albumCard.setArtistAction(() -> artistRedirectioner.accept(album.artist()));
            highlightsAlbumFP.getChildren().add(albumCard);
        }
        highlightsArtistFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < favArtists.data().size(); i++) {
            final Artist artist = favArtists.data().get(i);
            final var artistCard = new ArtistCard();
            artistCard.prefWidthProperty().bind(Bindings.add(-35, highlightsArtistFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistCard.setArtist(artist);
            artistCard.setAction(() -> artistRedirectioner.accept(artist));
            highlightsArtistFP.getChildren().add(artistCard);
        }

        favTracksTV.getItems().setAll(favTracks.data());
        favTracksLbl.setText(String.valueOf(favTracks.total()));

        favPlaylistsFP.getChildren().clear();
        playlistsCntLbl.setText(String.valueOf(playlists.total() - 1));
        for (final Playlist playlist: playlists.data()) {
            if (playlist.lovedTrack()) {
                continue;
            }
            final var playlistCard = new PlaylistCard();
            playlistCard.prefWidthProperty().bind(Bindings.add(-35, favPlaylistsFP.widthProperty().divide(4.2)));
            playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistCard.setPlaylist(playlist);
            playlistCard.setAction(() -> playlistRedirectioner.accept(playlist));
            favPlaylistsFP.getChildren().add(playlistCard);
        }

        favAlbumsFP.getChildren().clear();
        favAlbumsLbl.setText(String.valueOf(favAlbums.total()));
        for (final Album album: favAlbums.data()) {
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, favAlbumsFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            albumCard.setAlbumAction(() -> albumRedirectioner.accept(album));
            albumCard.setArtistAction(() -> artistRedirectioner.accept(album.artist()));
            favAlbumsFP.getChildren().add(albumCard);
        }

        favArtistsFP.getChildren().clear();
        favArtistsLbl.setText(String.valueOf(favArtists.total()));
        for (final Artist artist: favArtists.data()) {
            final var artistCard = new ArtistCard();
            artistCard.setArtist(artist);
            artistCard.prefWidthProperty().bind(Bindings.add(-35, favArtistsFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistCard.setAction(() -> artistRedirectioner.accept(artist));
            favArtistsFP.getChildren().add(artistCard);
        }

        myMusicBox.setVisible(loggedInUser);
        viewedUserBox.setVisible(!loggedInUser);
    }

    public void navigateTo(Destinations destination) {
        switch (destination) {
            case HIGHLIGHTS:
                myMusicTabPane.getSelectionModel().select(highlightsTab);
                break;
            case TRACKS:
                myMusicTabPane.getSelectionModel().select(favTracksTab);
                break;
            case PLAYLISTS:
                myMusicTabPane.getSelectionModel().select(myPlaylistsTab);
                break;
            case ALBUMS:
                myMusicTabPane.getSelectionModel().select(favAlbumsTab);
                break;
            case ARTISTS:
                myMusicTabPane.getSelectionModel().select(favArtistsTab);
                break;
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

    @FXML
    private void addPlaylistBtn_OnAction(ActionEvent actionEvent) {

    }

    @FXML
    private void favAlbumsBtn_OnAction(ActionEvent actionEvent) {
        myMusicTabPane.getSelectionModel().select(favAlbumsTab);
    }

    @FXML
    private void favArtistsBtn_OnAction(ActionEvent actionEvent) {
        myMusicTabPane.getSelectionModel().select(favArtistsTab);
    }

    @FXML
    private void favPlaylistsBtn_OnAction(ActionEvent actionEvent) {
        myMusicTabPane.getSelectionModel().select(myPlaylistsTab);
    }

    @FXML
    private void removeTrackFromFav(ActionEvent event) {

    }

    @FXML
    private void shuffleMusicBtn_OnAction(ActionEvent actionEvent) {
    }
}
