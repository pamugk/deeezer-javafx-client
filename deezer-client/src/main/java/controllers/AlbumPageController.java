package controllers;

import api.Deezer;
import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Track;
import components.cards.AlbumCard;
import components.cards.ArtistCard;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import utils.TimeUtils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static api.LoginStatus.NOT_AUTHORIZED;

public class AlbumPageController {

    @FXML
    private ResourceBundle resources;
    @FXML
    private ImageView albumCover;
    @FXML
    private Label albumName;
    @FXML
    private Button albumArtist;
    @FXML
    private ImageView albumArtistImg;
    @FXML
    private Label albumTracksLbl;
    @FXML
    private Label albumDurationLbl;
    @FXML
    private Label albumOutLbl;
    @FXML
    private Label albumFollowersLbl;
    @FXML
    private Button listenAlbumBtn;
    @FXML
    private Button addAlbumToLibrary;
    @FXML
    private ImageView albumAddToLibImg;
    @FXML
    private TableView<Track> albumTracksTV;
    @FXML
    private FlowPane albumArtistDiscographyFP;
    @FXML
    private FlowPane albumArtistRelatedFP;

    private Consumer<Album> albumRedirectioner = album -> {};
    private Consumer<Artist> artistRedirectioner = artist -> {};

    public void fillData(Album album, Deezer deezerClient) {
        albumCover.setImage(new Image(album.coverMedium().toString(), true));
        albumName.setText(album.title());
        albumArtistImg.setImage(new Image(album.artist().pictureSmall().toString(), true));
        albumArtist.setText(album.artist().name());
        albumTracksLbl.setText(String.format("%s: %d",
                resources.getString("tracksCnt"),album.trackCount()));
        albumDurationLbl.setText(String.format("%s", TimeUtils.secondsToNormalTime(album.duration(), resources)));
        albumOutLbl.setText(String.format("%s", album.releaseDate().toInstant().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE)));
        albumFollowersLbl.setText(String.format("%s: %d",
                resources.getString("followers"), album.fans()));

        albumTracksTV.getItems().setAll(album.tracks().data());

        final PartialSearchResponse<Album> albumArtistDiscography = deezerClient.getArtistDiscography(album.artist());
        albumArtistDiscographyFP.getChildren().clear();
        for (final Album discographyAlbum: albumArtistDiscography.data()) {
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, albumArtistDiscographyFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(discographyAlbum);
            albumCard.setAlbumAction(() -> albumRedirectioner.accept(discographyAlbum));
            albumCard.setArtistAction(() -> artistRedirectioner.accept(album.artist()));
            albumArtistDiscographyFP.getChildren().add(albumCard);
        }

        final PartialSearchResponse<Artist> albumArtistRelated = deezerClient.getArtistRelated(album.artist(), 25);
        albumArtistRelatedFP.getChildren().clear();
        for (final Artist artist: albumArtistRelated.data()) {
            final var artistCard = new ArtistCard();
            artistCard.setArtist(artist);
            artistCard.prefWidthProperty().bind(Bindings.add(-35, albumArtistRelatedFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistCard.setAction(() -> artistRedirectioner.accept(artist));
            albumArtistRelatedFP.getChildren().add(artistCard);
        }

        if (deezerClient.getLoginStatus() == NOT_AUTHORIZED) {
            addAlbumToLibrary.setVisible(false);
        } else {
            addAlbumToLibrary.setVisible(true);
            addAlbumToLibrary.setText(resources.getString("addToMyMusic"));
            albumAddToLibImg.setImage(new Image("src/main/resources/img/icon-like.png"));
        }
    }

    public void setAlbumRedirectioner(Consumer<Album> albumRedirectioner) {
        this.albumRedirectioner = albumRedirectioner;
    }

    public void setArtistRedirectioner(Consumer<Artist> artistRedirectioner) {
        this.artistRedirectioner = artistRedirectioner;
    }
}
