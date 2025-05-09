package controllers;

import api.Deezer;
import api.PartialSearchResponse;
import api.objects.comments.Comment;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.TrackSearch;
import api.objects.utils.User;
import components.cards.AlbumCard;
import components.cards.ArtistCard;
import components.cards.CommentCard;
import components.cards.PlaylistCard;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static api.LoginStatus.NOT_AUTHORIZED;

public class ArtistPageController {

    private static final int HIGHLIGHTS_LIMIT = 3;

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
    private VBox artistPlaylistsBox;
    @FXML
    private TableView<TrackSearch> artistTopTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> artistTMPTIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> artistTMPTTitleCol;
    @FXML
    private VBox artistRelatedBox;
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
    private VBox artistCommentsBox;
    //</editor-fold>

    private Consumer<Album> albumRedirectioner = album -> {};
    private Consumer<Artist> artistRedirectioner = artist -> {};
    private Consumer<Playlist> playlistRedirectioner = playlist -> {};
    private Consumer<User> userRedirectioner = user -> {};

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
        artistPlaylistsBox.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < playlists.getData().size(); i++) {
            final Playlist playlist = playlists.getData().get(i);
            final var playlistBox = new HBox();
            playlistBox.setPadding(new Insets(0, 10, 0, 0));
            playlistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            ImageView playlistPicture = new ImageView(new Image(playlist.getPicture_small().toString(), true));
            playlistPicture.setFitWidth(56);
            playlistPicture.setFitHeight(56);
            Button playlistBtn = new Button(null, playlistPicture);
            playlistBtn.getStyleClass().add("deezer-button");
            playlistBox.getChildren().addAll(playlistBtn);

            VBox playlistInfoBox = new VBox();
            playlistInfoBox.setPadding(new Insets(10, 0, 0, 0));
            playlistInfoBox.setAlignment(Pos.CENTER);
            playlistInfoBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistInfoBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            playlistBox.getChildren().add(playlistInfoBox);

            Label playlistTitle = new Label(playlist.getTitle());
            playlistTitle.getStyleClass().add("deezer-secondary");
            playlistInfoBox.getChildren().add(playlistTitle);

            /*Label playlistFollowers = new Label(String.format("%s: %d/%s: %d",
                    resources.getString("tracks"), playlist.getNb_tracks(),
                    resources.getString("followers"), playlist.getFans()));
            playlistFollowers.getStyleClass().add("deezer-secondary");
            playlistInfoBox.getChildren().add(playlistFollowers);*/

            artistPlaylistsBox.getChildren().add(playlistBox);
        }
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
        artistRelatedBox.getChildren().clear();
        for (int i = 0; i < HIGHLIGHTS_LIMIT && i < similarArtists.getData().size(); i++) {
            final Artist similarArtist = similarArtists.getData().get(i);
            final var artistBox = new HBox();
            artistBox.setPadding(new Insets(0, 10, 0, 0));
            artistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            ImageView artistPicture = new ImageView(new Image(artist.getPicture_small().toString(), true));
            artistPicture.setFitWidth(56);
            artistPicture.setFitHeight(56);
            Button artistBtn = new Button(null, artistPicture);
            artistBtn.getStyleClass().add("deezer-button");
            artistBox.getChildren().addAll(artistBtn);

            VBox artistInfoBox = new VBox();
            artistInfoBox.setPadding(new Insets(10, 0, 0, 0));
            artistInfoBox.setAlignment(Pos.CENTER);
            artistInfoBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistInfoBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            artistBox.getChildren().add(artistInfoBox);

            Label artistName = new Label(artist.getName());
            artistName.getStyleClass().add("deezer-secondary");
            artistInfoBox.getChildren().add(artistName);

            /*Label artistFollowers = new Label(String.format("%s: %d",
                    resources.getString("followers"), artist.getNb_fan()));
            artistFollowers.getStyleClass().add("deezer-secondary");
            artistInfoBox.getChildren().add(artistFollowers);*/

            artistRelatedBox.getChildren().add(artistBox);
        }
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
        artistCommentsBox.getChildren().clear();
        for (final Comment comment : deezerClient.getArtistComments(artist).getData()) {
            CommentCard commentCard = new CommentCard();
            commentCard.setComment(comment);
            commentCard.setUserAction(() -> userRedirectioner.accept(comment.getAuthor()));
            artistCommentsBox.getChildren().add(commentCard);
        }
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

    public void setUserRedirectioner(Consumer<User> userRedirectioner) {
        this.userRedirectioner = userRedirectioner;
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
