package controllers;

import api.Deezer;
import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.Track;
import api.objects.utils.User;
import api.objects.utils.search.SearchOrder;
import components.containers.cards.AlbumCard;
import components.containers.cards.ArtistCard;
import components.containers.cards.PlaylistCard;
import components.containers.tables.TrackTable;
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

public class UserPageController {
    public enum Destinations{
        HIGHLIGHTS,
        TRACKS,
        PLAYLISTS,
        ALBUMS,
        ARTISTS,
    }

    private static final int HIGHLIGHTS_LIMIT = 4;

    //<editor-fold defaultstate="collapsed" desc="Controls">
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
    private TrackTable<Track> favTracksTV;
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
    //</editor-fold>

    @FXML
    void addPlaylistBtn_OnAction(ActionEvent actionEvent) {

    }

    @FXML
    void favAlbumsBtn_OnAction(ActionEvent actionEvent) {
        myMusicTabPane.getSelectionModel().select(favAlbumsTab);
    }

    @FXML
    void favArtistsBtn_OnAction(ActionEvent actionEvent) {
        myMusicTabPane.getSelectionModel().select(favArtistsTab);
    }

    @FXML
    void favPlaylistsBtn_OnAction(ActionEvent actionEvent) {
        myMusicTabPane.getSelectionModel().select(myPlaylistsTab);
    }

    @FXML
    void removeTrackFromFav(ActionEvent event) {

    }

    @FXML
    void shuffleMusicBtn_OnAction(ActionEvent actionEvent) {
    }

    public void setUser(User user, boolean loggedInUser, Deezer deezerClient) {
        if (!loggedInUser){
            viewedUserImg.setImage(new Image(user.getPicture_medium().toString(), true));
            viewedUserNameLbl.setText(user.getName());
        }

        addPlaylistBtn.setVisible(loggedInUser);
        PartialSearchResponse<Track> favTracks = deezerClient.getFavouredTracks(user, null);
        PartialSearchResponse<Playlist> playlists = deezerClient.getFavouredPlaylists(user, null);
        PartialSearchResponse<Album> favAlbums = deezerClient.getFavoredAlbums(user, SearchOrder.ALBUM_ASC);
        PartialSearchResponse<Artist> favArtists = deezerClient.getFavoredArtists(user, SearchOrder.ARTIST_ASC);

        highlightsPlaylistFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < playlists.getData().size(); i++) {
            final Playlist playlist = playlists.getData().get(i);
            if (playlist.is_loved_track()) {
                continue;
            }
            final var playlistCard = new PlaylistCard();
            playlistCard.setPlaylist(playlist);
            playlistCard.prefWidthProperty().bind(Bindings.add(-35, highlightsPlaylistFP.widthProperty().divide(4.2)));
            playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            highlightsPlaylistFP.getChildren().add(playlistCard);
        }

        highlightsAlbumFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < favAlbums.getData().size(); i++) {
            final Album album = favAlbums.getData().get(i);
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, highlightsAlbumFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            highlightsAlbumFP.getChildren().add(albumCard);
        }
        highlightsArtistFP.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < favArtists.getData().size(); i++) {
            final Artist artist = favArtists.getData().get(i);
            final var artistCard = new ArtistCard();
            artistCard.setArtist(artist);
            artistCard.prefWidthProperty().bind(Bindings.add(-35, highlightsArtistFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            highlightsArtistFP.getChildren().add(artistCard);
        }

        favTracksTV.fill(favTracks, favTracksLbl, true);

        favPlaylistsFP.getChildren().clear();
        playlistsCntLbl.setText(String.valueOf(playlists.getTotal() - 1));
        for (final Playlist playlist: playlists.getData()) {
            if (playlist.is_loved_track()) {
                continue;
            }
            final var playlistCard = new PlaylistCard();
            playlistCard.setPlaylist(playlist);
            playlistCard.prefWidthProperty().bind(Bindings.add(-35, favPlaylistsFP.widthProperty().divide(4.2)));
            playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            favPlaylistsFP.getChildren().add(playlistCard);
        }

        favAlbumsFP.getChildren().clear();
        favAlbumsLbl.setText(String.valueOf(favAlbums.getTotal()));
        for (final Album album: favAlbums.getData()) {
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, favAlbumsFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            favAlbumsFP.getChildren().add(albumCard);
        }

        favArtistsFP.getChildren().clear();
        favArtistsLbl.setText(String.valueOf(favArtists.getTotal()));
        for (final Artist artist: favArtists.getData()) {
            final var artistCard = new ArtistCard();
            artistCard.setArtist(artist);
            artistCard.prefWidthProperty().bind(Bindings.add(-35, favArtistsFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            favArtistsFP.getChildren().add(artistCard);
        }

        myMusicBox.setVisible(loggedInUser);
        viewedUserBox.setVisible(!loggedInUser);
    }

    public void navigateTo(Destinations destination) {
        switch (destination){
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
}
