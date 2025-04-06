package controllers;

import api.Deezer;
import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.TrackSearch;
import components.containers.cards.AlbumCard;
import components.containers.cards.ArtistCard;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import utils.TimeUtils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static api.LoginStatus.NOT_AUTHORIZED;

public class AlbumPageController {

    //<editor-fold defaultstate="collapsed" desc="Controls">
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
    private TableView<TrackSearch> albumTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> albumTrackIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> albumTrackName;
    @FXML
    private TableColumn<TrackSearch, Integer> albumTrackDurCol;
    @FXML
    private TableColumn<TrackSearch, Integer> albumTrackPopCol;
    @FXML
    private FlowPane albumArtistDiscographyFP;
    @FXML
    private FlowPane albumArtistRelatedFP;
    //</editor-fold>

    public void setAlbum(Album album, Deezer deezerClient) {
        albumCover.setImage(new Image(album.getCover_medium().toString(), true));
        albumName.setText(album.getTitle());
        albumArtistImg.setImage(new Image(album.getArtist().getPicture_small().toString(), true));
        albumArtist.setText(album.getArtist().getName());
        albumTracksLbl.setText(String.format("%s: %d",
                resources.getString("tracksCnt"),album.getNb_tracks()));
        albumDurationLbl.setText(String.format("%s", TimeUtils.secondsToNormalTime(album.getDuration(), resources)));
        albumOutLbl.setText(String.format("%s", album.getRelease_date().toInstant().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE)));
        albumFollowersLbl.setText(String.format("%s: %d",
                resources.getString("followers"), album.getFans()));

        albumTracksTV.getItems().clear();
        albumTracksTV.getItems().addAll(album.getTracks().getData());

        final PartialSearchResponse<Album> albumArtistDiscography = deezerClient.getArtistDiscography(album.getArtist());
        albumArtistDiscographyFP.getChildren().clear();
        for (final Album discographyAlbum: albumArtistDiscography.getData()) {
            final var albumCard = new AlbumCard();
            albumCard.prefWidthProperty().bind(Bindings.add(-35, albumArtistDiscographyFP.widthProperty().divide(4.2)));
            albumCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumCard.setAlbum(discographyAlbum);
            albumArtistDiscographyFP.getChildren().add(albumCard);
        }

        final PartialSearchResponse<Artist> albumArtistRelated = deezerClient.getArtistRelated(album.getArtist(), 25);
        albumArtistRelatedFP.getChildren().clear();
        for (final Artist artist: albumArtistRelated.getData()) {
            final var artistCard = new ArtistCard();
            artistCard.setArtist(artist);
            artistCard.prefWidthProperty().bind(Bindings.add(-35, albumArtistRelatedFP.widthProperty().divide(4.2)));
            artistCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
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
}
