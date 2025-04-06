package controllers;

import api.Deezer;
import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.TrackSearch;
import components.containers.boxes.ArtistBox;
import components.containers.boxes.CommentBox;
import components.containers.boxes.PlaylistBox;
import components.containers.cards.AlbumCard;
import components.containers.cards.ArtistCard;
import components.containers.cards.PlaylistCard;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static api.LoginStatus.NOT_AUTHORIZED;

public class ArtistPageController {
    //<editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private ResourceBundle resources;
    @FXML
    private ImageView artistPicture;
    @FXML
    private Label artistNameLbl;
    @FXML
    private Label artistFansLbl;
    @FXML
    private Button artistPlayMixBtn;
    @FXML
    private Button artistAddToFavBtn;
    @FXML
    private ImageView artistFollowedImg;
    @FXML
    private TabPane artistTabPane;
    @FXML
    private Tab artistDiscographyTab;
    @FXML
    private Button artistMPTracksBtn;
    @FXML
    private Button artistMtPReleaseBtn;
    @FXML
    private Button artistPlaylistsBtn;
    @FXML
    private Button artistSimiliarBtn;
    @FXML
    private PlaylistBox artistPlaylistsBox;
    @FXML
    private TableView<TrackSearch> artistTopTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> artistTMPTIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> artistTMPTTitleCol;
    @FXML
    private ArtistBox artistRelatedBox;
    @FXML
    private ImageView artistTopAlbumImg;
    @FXML
    private Label artistTopAlbumName;
    @FXML
    private Label artistTopAlbumRelease;
    @FXML
    private TableView<TrackSearch> artistTopAlbumTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> artistTATIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> artistTATTitleCol;
    @FXML
    private FlowPane artistDiscographyFP;
    @FXML
    private Tab artistPopularTracksTab;
    @FXML
    private Button artistListenPopTracksBtn;
    @FXML
    private TextField artistPopTracksSearchBox;
    @FXML
    private TableView<TrackSearch> artistPopTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> artistMPTIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> artistMPTTitleCol;
    @FXML
    private TableColumn<TrackSearch, Artist> artistMPTArtistCol;
    @FXML
    private TableColumn<TrackSearch, Album> artistMPTAlbumCol;
    @FXML
    private TableColumn<TrackSearch, Integer> artistMPTPopCol;
    @FXML
    private TableColumn<TrackSearch, Integer> artistMPTLengthCol;
    @FXML
    private Tab artistRelatedTab;
    @FXML
    private FlowPane artistRelatedFP;
    @FXML
    private Tab artistPlaylistsTab;
    @FXML
    private FlowPane artistPlaylistsFP;
    @FXML
    private CommentBox artistCommentsBox;
    //</editor-fold>

    private Consumer<Album> albumRedirectioner = album -> {};
    private Consumer<Artist> artistRedirectioner = artist -> {};
    private Consumer<Playlist> playlistRedirectioner = playlist -> {};

    public void fillData(Artist artist, Deezer deezerClient) {
        artistPicture.setImage(new Image(artist.getPicture_medium().toString(), true));
        artistNameLbl.setText(artist.getName());
        artistFansLbl.setText(String.format("%s: %d", resources.getString("followers"), artist.getNb_fan()));
        if (deezerClient.getLoginStatus() == NOT_AUTHORIZED)
            artistAddToFavBtn.setVisible(false);
        else {
            artistAddToFavBtn.setVisible(true);
            artistFollowedImg.setImage(new Image("src/main/resources/img/icon-like.png"));
        }
        PartialSearchResponse<TrackSearch> popularTracks = deezerClient.getArtistTop(artist, 50);
        artistTopTracksTV.getItems().clear();
        artistTopTracksTV.getItems().addAll(popularTracks.getData().stream().limit(4).toList());
        artistPopTracksTV.getItems().clear();
        artistPopTracksTV.getItems().addAll(popularTracks.getData());

        PartialSearchResponse<Playlist> playlists = deezerClient.getArtistPlaylists(artist, 25);
        artistPlaylistsBox.fill(playlists.getData().stream().limit(3).collect(Collectors.toList()));
        artistPlaylistsFP.getChildren().clear();
        for (final Playlist playlist: playlists.getData()) {
            if (playlist.is_loved_track()) {
                continue;
            }
            final var playlistCard = new PlaylistCard();
            playlistCard.prefWidthProperty().bind(Bindings.add(-35, artistPlaylistsFP.widthProperty().divide(4.2)));
            playlistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistCard.setPlaylist(playlist);
            playlistCard.setAction(() -> playlistRedirectioner.accept(playlist));
            artistPlaylistsFP.getChildren().add(playlistCard);
        }

        PartialSearchResponse<Artist> similarArtists = deezerClient.getArtistRelated(artist, 25);
        artistRelatedBox.fill(similarArtists.getData().stream().limit(3).collect(Collectors.toList()));
        artistRelatedFP.getChildren().clear();
        for (final Artist similarArtist: similarArtists.getData()) {
            final var artistCard = new ArtistCard();
            artistCard.prefWidthProperty().bind(Bindings.add(-35, artistRelatedFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistCard.setArtist(similarArtist);
            artistCard.setAction(() -> artistRedirectioner.accept(artist));
            artistRelatedFP.getChildren().add(artistCard);
        }

        PartialSearchResponse<Album> discography = deezerClient.getArtistDiscography(artist);
        artistDiscographyFP.getChildren().clear();
        for (final Album album: discography.getData()) {
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, artistDiscographyFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(album);
            albumCard.setAlbumAction(() -> albumRedirectioner.accept(album));
            albumCard.setArtistAction(() -> artistRedirectioner.accept(album.getArtist()));
            artistDiscographyFP.getChildren().add(albumCard);
        }
        artistTopAlbumTracksTV.getItems().clear();

        boolean albumShowed = false;
        final Optional<Album> topAlbum = discography.getData().stream().max(Comparator.comparingInt(Album::getRating));
        if (topAlbum.isPresent()) {
            albumShowed = true;

            final Album topAlbumValue = topAlbum.get();
            artistTopAlbumImg.setImage(new Image(topAlbumValue.getCover_medium().toString(), true));
            artistTopAlbumName.setText(topAlbumValue.getTitle());
            artistTopAlbumRelease.setText(topAlbumValue.getRelease_date().toInstant()
                    .atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE));
            artistTopAlbumTracksTV.getItems().addAll(deezerClient.getAlbumTracks(topAlbumValue).getData()
                    .stream().map(track -> (TrackSearch)track).toList());
        }
        artistTopAlbumImg.setVisible(albumShowed);
        artistTopAlbumName.setVisible(albumShowed);
        artistTopAlbumRelease.setVisible(albumShowed);
        artistTopAlbumTracksTV.setVisible(albumShowed);
        artistCommentsBox.fill(deezerClient.getArtistComments(artist));
        artistTabPane.getSelectionModel().select(artistDiscographyTab);
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
    private void artistPlaylistsBtn_OnAction(ActionEvent actionEvent) {
        artistTabPane.getSelectionModel().select(artistPlaylistsTab);
    }

    @FXML
    private void artistMPTracksBtn_OnAction(ActionEvent actionEvent) {
        artistTabPane.getSelectionModel().select(artistPopularTracksTab);
    }

    @FXML
    private void artistSimiliarBtn_OnAction(ActionEvent actionEvent) {
        artistTabPane.getSelectionModel().select(artistRelatedTab);
    }
}
