package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import api.Deezer;
import api.PartialSearchResponse;
import api.events.authentication.AuthenticationEvent;
import api.events.base.DeezerListener;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.User;
import api.objects.utils.search.FullSearchSet;
import api.objects.utils.search.SearchOrder;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import static api.LoginStatus.NOT_AUTHORIZED;

public class IndexController {
    private Deezer deezerClient;
    private Alert standbyAlert;
    private TrackSearch selectedTrack;
    private Playlist currentPlaylist;

    public static void show(Stage primaryStage) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader loader = new FXMLLoader(IndexController.class.getResource("/fxml/index.fxml"), bundle);
        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.getIcons().add(
                new Image(IndexController.class.getResourceAsStream("/img/deezer-icon.jpg")));
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(100);
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        IndexController controller = loader.getController();
        controller.standbyAlert = new Alert(Alert.AlertType.INFORMATION);
        controller.standbyAlert.initModality(Modality.APPLICATION_MODAL);
        controller.standbyAlert.initOwner(primaryStage);
        primaryStage.setOnCloseRequest(event -> controller.deezerClient.stop());
        primaryStage.show();
    }

    //<editor-fold defaultstate="collapsed" desc="Auxiliary methods">\
    private void changeInterfaceState(boolean logout) {
        alert.setVisible(logout);
        recommendationsBox.setVisible(!logout);
        if (logout) {
            recommendedPlaylistsFP.getChildren().clear();
            recommendedArtistsFP.getChildren().clear();
            recommendedAlbumsFP.getChildren().clear();
            homeBtn.getOnAction().handle(new ActionEvent());
        }
        loginBtn.setVisible(logout);
        userMenuBar.setVisible(!logout);
        exploreBtn.setDisable(logout);
        myMusicBtn.setDisable(logout);
        favouriteTracksBtn.setDisable(logout);
        playlistsBtn.setDisable(logout);
        albumsBtn.setDisable(logout);
        artistsBtn.setDisable(logout);
        playerBox.setDisable(logout);
    }

    private void fillFlowPaneWithAlbums(FlowPane flow, PartialSearchResponse<Album> albums, Label countLabel) {
        flow.getChildren().clear();
        for (Album album : albums.getData()) {
            VBox albumBox = new VBox();
            albumBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
            albumBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumBox.getStyleClass().add("albumcard");

            ImageView albumCover = new ImageView(album.getCover_medium().toString());
            albumCover.fitWidthProperty().bind(albumBox.prefWidthProperty());
            albumCover.fitHeightProperty().bind(albumCover.fitWidthProperty());
            Button albumRedirectButton = new Button(null, albumCover);
            albumRedirectButton.setOnAction(event -> redirectToAlbum(deezerClient.getAlbum(album.getId())));
            albumRedirectButton.prefWidthProperty().bind(albumCover.fitWidthProperty());
            albumRedirectButton.prefHeightProperty().bind(albumCover.fitHeightProperty());
            albumRedirectButton.getStyleClass().add("albumcard-album");
            albumBox.getChildren().add(albumRedirectButton);

            Label albumTitle = new Label(album.getTitle());
            albumTitle.setWrapText(true);
            albumTitle.getStyleClass().add("albumcard-name");
            albumTitle.setPrefWidth(Region.USE_COMPUTED_SIZE);
            albumTitle.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumBox.getChildren().add(albumTitle);

            Button artistRedirectButton = new Button(
                    String.format("%s %s", resources.getString("by"), album.getArtist().getName()));
            artistRedirectButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
            artistRedirectButton.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistRedirectButton.setOnAction(event -> redirectToArtist(album.getArtist()));
            artistRedirectButton.getStyleClass().add("albumcard-artist");
            albumBox.getChildren().add(artistRedirectButton);

            flow.getChildren().add(albumBox);
        }
        if (countLabel != null)
            countLabel.setText(String.valueOf(albums.getTotal()));
    }

    private void fillFlowPaneWithArtists(FlowPane flow, PartialSearchResponse<Artist> artists, Label countLabel) {
        flow.getChildren().clear();
        for (Artist artist : artists.getData()) {
            VBox artistBox = new VBox();
            artistBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
            artistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistBox.getStyleClass().add("artistcard");

            ImageView artistPicture = new ImageView(artist.getPicture_medium().toString());
            artistPicture.fitWidthProperty().bind(artistBox.prefWidthProperty());
            artistPicture.fitHeightProperty().bind(artistPicture.fitWidthProperty());

            Button artistRedirectButton = new Button(null, artistPicture);
            artistRedirectButton.setOnAction(event -> redirectToArtist(deezerClient.getArtist(artist.getId())));
            artistRedirectButton.prefWidthProperty().bind(artistPicture.fitWidthProperty());
            artistRedirectButton.prefHeightProperty().bind(artistPicture.fitHeightProperty());
            artistRedirectButton.getStyleClass().add("artistcard-artist");
            artistBox.getChildren().add(artistRedirectButton);

            Label artistName = new Label(artist.getName());
            artistName.setWrapText(true);
            artistName.getStyleClass().add("artistcard-name");
            artistName.setPrefWidth(Region.USE_COMPUTED_SIZE);
            artistName.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistBox.getChildren().add(artistName);

            if (artist.getNb_fan() > 0) {
                Label artistFollowers = new Label(String.format("%s: %s", resources.getString("followers"),
                        artist.getNb_fan()));
                artistName.getStyleClass().add("artistcard-followers");
                artistName.setPrefWidth(Region.USE_COMPUTED_SIZE);
                artistName.setPrefHeight(Region.USE_COMPUTED_SIZE);
                artistBox.getChildren().add(artistFollowers);
            }

            flow.getChildren().add(artistBox);
        }
        if (countLabel != null)
            countLabel.setText(String.valueOf(artists.getTotal()));
    }

    private void fillFlowPaneWithPlaylists(FlowPane flow, PartialSearchResponse<Playlist> playlists, Label countLabel) {
        flow.getChildren().clear();
        for (Playlist playlist : playlists.getData()) {
            if (playlist.isIs_loved_track())
                continue;
            VBox playlistBox = new VBox();
            playlistBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
            playlistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistBox.getStyleClass().add("playlistcard");

            ImageView playlistPicture = new ImageView(playlist.getPicture_medium().toString());
            playlistPicture.fitWidthProperty().bind(playlistBox.prefWidthProperty());
            playlistPicture.fitHeightProperty().bind(playlistPicture.fitWidthProperty());

            Button playlistRedirectButton = new Button(null, playlistPicture);
            playlistRedirectButton.setOnAction(event -> redirectToPlaylist(deezerClient.getPlaylist(playlist.getId())));
            playlistRedirectButton.prefWidthProperty().bind(playlistPicture.fitWidthProperty());
            playlistRedirectButton.prefHeightProperty().bind(playlistPicture.fitHeightProperty());
            playlistRedirectButton.getStyleClass().add("playlistcard-playlist");
            playlistBox.getChildren().add(playlistRedirectButton);

            Label playlistTitle = new Label(playlist.getTitle());
            playlistTitle.setWrapText(true);
            playlistTitle.getStyleClass().add("playlistcard-title");
            playlistTitle.setPrefWidth(Region.USE_COMPUTED_SIZE);
            playlistTitle.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistBox.getChildren().add(playlistTitle);

            Label playlistTrackCount = new Label(String.format("%s: %d", resources.getString("tracksCnt"),
                    playlist.getNb_tracks()));
            playlistTrackCount.getStyleClass().add("playlistcard-tracks");
            playlistTrackCount.setPrefWidth(Region.USE_COMPUTED_SIZE);
            playlistTrackCount.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistBox.getChildren().add(playlistTrackCount);

            flow.getChildren().add(playlistBox);
        }
        if (countLabel != null)
            countLabel.setText(String.valueOf(playlists.getTotal() - 1));
    }

    private void fillFlowPaneWithRadios(FlowPane flow, PartialSearchResponse<Radio> radios, Label countLabel) {
        flow.getChildren().clear();
        for (Radio radio : radios.getData()) {
            VBox radioBox = new VBox();
            radioBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
            radioBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            radioBox.getStyleClass().add("radiocard");

            ImageView radioPicture = new ImageView(radio.getPicture_medium().toString());
            radioPicture.fitWidthProperty().bind(radioBox.prefWidthProperty());
            radioPicture.fitHeightProperty().bind(radioPicture.fitWidthProperty());

            Button radioRedirectButton = new Button(null, radioPicture);
            radioRedirectButton.setOnAction(event -> redirectToRadio(deezerClient.getRadio(radio.getId())));
            radioRedirectButton.prefWidthProperty().bind(radioPicture.fitWidthProperty());
            radioRedirectButton.prefHeightProperty().bind(radioPicture.fitHeightProperty());
            radioRedirectButton.getStyleClass().add("radiocard-radio");
            radioBox.getChildren().add(radioRedirectButton);

            flow.getChildren().add(radioBox);
        }
        if (countLabel != null)
            countLabel.setText(String.valueOf(radios.getTotal()));
    }

    private void fillFlowPaneWithUsers(FlowPane flow, PartialSearchResponse<User> users, Label countLabel) {
        flow.getChildren().clear();
        for (User user : users.getData()) {
            VBox userBox = new VBox();
            userBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
            userBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            userBox.getStyleClass().add("usercard");

            ImageView userPicture = new ImageView(user.getPicture_medium().toString());
            userPicture.fitWidthProperty().bind(userBox.prefWidthProperty());
            userPicture.fitHeightProperty().bind(userPicture.fitWidthProperty());

            Button userRedirectButton = new Button(null, userPicture);
            userRedirectButton.setOnAction(event -> redirectToUser(deezerClient.getUser(user.getId())));
            userRedirectButton.prefWidthProperty().bind(userPicture.fitWidthProperty());
            userRedirectButton.prefHeightProperty().bind(userPicture.fitHeightProperty());
            userRedirectButton.getStyleClass().add("usercard-user");
            userBox.getChildren().add(userRedirectButton);

            Label userName = new Label(user.getName());
            userName.setWrapText(true);
            userName.getStyleClass().add("usercard-name");
            userName.setPrefWidth(Region.USE_COMPUTED_SIZE);
            userName.setPrefHeight(Region.USE_COMPUTED_SIZE);
            userBox.getChildren().add(userName);

            flow.getChildren().add(userBox);
        }
        if (countLabel != null)
            countLabel.setText(String.valueOf(users.getTotal()));
    }

    private void fillVBoxWithComments(VBox commentsBox, PartialSearchResponse<Comment> comments) {
        commentsBox.getChildren().clear();
        for (Comment comment : comments.getData()) {
            HBox commentBox = new HBox();
            commentBox.setPadding(new Insets(0, 24, 0, 24));
            commentBox.setSpacing(24);
            commentBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            commentBox.setPrefHeight(Region.USE_COMPUTED_SIZE);

            ImageView commentorImg = new ImageView(comment.getAuthor().getPicture_small().toString());
            commentorImg.setFitHeight(30);
            commentorImg.setFitWidth(30);
            Button commentorBtn = new Button(null, commentorImg);
            commentorBtn.getStyleClass().add("deezer-button");
            commentorBtn.setOnAction(event -> redirectToUser(deezerClient.getUser(comment.getAuthor().getId())));
            commentBox.getChildren().add(commentorBtn);

            VBox commentInfoBox = new VBox();
            commentInfoBox.getStyleClass().add("comment-card");
            commentInfoBox.setPadding(new Insets(12, 0, 12, 0));
            commentInfoBox.setSpacing(10);
            commentInfoBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            commentInfoBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            Label commentCreationInfo = new Label(String.format("%s - %s", comment.getAuthor().getName(),
                    Instant.ofEpochSecond(comment.getDate()).atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ISO_LOCAL_DATE)));
            commentCreationInfo.getStyleClass().add("deezer-secondary");
            commentInfoBox.getChildren().add(commentCreationInfo);
            Label commentText = new Label(comment.getText());
            commentText.getStyleClass().add("deezer-secondary");
            commentText.setWrapText(true);
            commentInfoBox.getChildren().add(commentText);
            commentBox.getChildren().add(commentInfoBox);

            commentsBox.getChildren().add(commentBox);
        }
    }

    private void fillVBoxWithArtists(VBox artistVBox, List<Artist> artists) {
        artistVBox.getChildren().clear();
        for (Artist artist : artists) {
            HBox artistBox = new HBox();
            artistBox.setPadding(new Insets(0, 10, 0, 0));
            artistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            ImageView artistPicture = new ImageView(artist.getPicture_small().toString());
            artistPicture.setFitWidth(56);
            artistPicture.setFitHeight(56);
            Button artistBtn = new Button(null, artistPicture);
            artistBtn.getStyleClass().add("deezer-button");
            artistBtn.setOnAction(event -> redirectToArtist(deezerClient.getArtist(artist.getId())));
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

            Label artistFollowers = new Label(String.format("%s: %d",
                    resources.getString("followers"), artist.getNb_fan()));
            artistFollowers.getStyleClass().add("deezer-secondary");
            artistInfoBox.getChildren().add(artistFollowers);

            artistVBox.getChildren().add(artistBox);
        }
    }

    private void fillVBoxWithPlaylists(VBox playlistsVBox, List<Playlist> playlists) {
        playlistsVBox.getChildren().clear();
        for (Playlist playlist : playlists) {
            HBox playlistBox = new HBox();
            playlistBox.setPadding(new Insets(0, 10, 0, 0));
            playlistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            ImageView playlistPicture = new ImageView(playlist.getPicture_small().toString());
            playlistPicture.setFitWidth(56);
            playlistPicture.setFitHeight(56);
            Button playlistBtn = new Button(null, playlistPicture);
            playlistBtn.setOnAction(event -> redirectToPlaylist(deezerClient.getPlaylist(playlist.getId())));
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

            Label playlistFollowers = new Label(String.format("%s: %d/%s: %d",
                    resources.getString("tracks"), playlist.getNb_tracks(),
                    resources.getString("followers"), playlist.getFans()));
            playlistFollowers.getStyleClass().add("deezer-secondary");
            playlistInfoBox.getChildren().add(playlistFollowers);

            playlistsVBox.getChildren().add(playlistBox);
        }
    }

    private void onLoginResponse (AuthenticationEvent event) {
        loginBtn.setDisable(false);
        if (!event.isAuthenticationSuccessful())
            return;

        Platform.runLater(() -> {
            User currentUser = deezerClient.getLoggedInUser();
            Image avatar = new Image(currentUser.getPicture_small().toString());
            userAvatar.setImage(avatar);
            userAccounttem.setText(currentUser.getName());
            userMenuAvatar.setImage(avatar);

            fillFlowPaneWithPlaylists(recommendedPlaylistsFP, deezerClient.getRecommendedPlaylists(12), null);
            fillFlowPaneWithArtists(recommendedArtistsFP, deezerClient.getRecommendedArtists(12), null);
            fillFlowPaneWithAlbums(recommendedAlbumsFP, deezerClient.getRecommendedAlbums(12), null);

            changeInterfaceState(false);
        });
    }

    private void redirectToAlbum(Album album) {
        albumCover.setImage(new Image(album.getCover_medium().toString()));
        albumName.setText(album.getTitle());
        albumArtistImg.setImage(new Image(album.getArtist().getPicture_small().toString()));
        albumArtist.setText(album.getArtist().getName());
        albumArtist.setOnAction(event -> redirectToArtist(deezerClient.getArtist(album.getArtist().getId())));
        albumTracksLbl.setText(String.format("%s: %d",
                resources.getString("tracksCnt"),album.getNb_tracks()));
        albumDurationLbl.setText(String.format("%s", secondsToNormalTime(album.getDuration())));
        albumOutLbl.setText(String.format("%s", album.getRelease_date().toInstant().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE)));
        albumFollowersLbl.setText(String.format("%s: %d",
                resources.getString("followers"), album.getFans()));

        albumTracksTV.getItems().clear();
        albumTracksTV.getItems().addAll(album.getTracks().getData());

        fillFlowPaneWithAlbums(albumArtistDiscographyFP,
                deezerClient.getArtistDiscography(album.getArtist()), null);
        fillFlowPaneWithArtists(albumArtistRelatedFP,
                deezerClient.getArtistRelated(album.getArtist(), 25), null);
        if (deezerClient.getLoginStatus() == NOT_AUTHORIZED)
            addAlbumToLibrary.setVisible(false);
        else {
            addAlbumToLibrary.setVisible(true);
            addAlbumToLibrary.setText(resources.getString("addToMyMusic"));
            albumAddToLibImg.setImage(new Image("img/icon-like.png"));
        }
        mainTabPane.getSelectionModel().select(albumTab);
    }

    private void redirectToArtist(Artist artist) {
        artistPicture.setImage(new Image(artist.getPicture_medium().toString()));
        artistNameLbl.setText(artist.getName());
        artistFansLbl.setText(String.format("%s: %d", resources.getString("followers"), artist.getNb_fan()));
        if (deezerClient.getLoginStatus() == NOT_AUTHORIZED)
            artistAddToFavBtn.setVisible(false);
        else {
            artistAddToFavBtn.setVisible(true);
            artistFollowedImg.setImage(new Image("img/icon-like.png"));
        }
        PartialSearchResponse<TrackSearch> popularTracks = deezerClient.getArtistTop(artist, 50);
        artistTopTracksTV.getItems().clear();
        artistTopTracksTV.getItems().addAll(popularTracks.getData().stream().limit(4).collect(Collectors.toList()));
        artistPopTracksTV.getItems().clear();
        artistPopTracksTV.getItems().addAll(popularTracks.getData());

        PartialSearchResponse<Playlist> playlists = deezerClient.getArtistPlaylists(artist, 25);
        fillVBoxWithPlaylists(artistPlaylistsVBox, playlists.getData().stream().limit(3).collect(Collectors.toList()));
        fillFlowPaneWithPlaylists(artistPlaylistsFP, playlists, null);

        PartialSearchResponse<Artist> similiarArtists = deezerClient.getArtistRelated(artist, 25);
        fillVBoxWithArtists(artistRelatedVBox, similiarArtists.getData().stream().limit(3).collect(Collectors.toList()));
        fillFlowPaneWithArtists(artistRelatedFP, similiarArtists, null);

        PartialSearchResponse<Album> discography = deezerClient.getArtistDiscography(artist);
        fillFlowPaneWithAlbums(artistDiscographyFP, discography, null);
        artistTopAlbumTracksTV.getItems().clear();
        var ref = new Object() {
            boolean albumShowed = false;
        };
        discography.getData().stream().max(Comparator.comparingInt(Album::getRating))
                .ifPresent(album -> {
            ref.albumShowed = true;
            artistTopAlbumImg.setImage(new Image(album.getCover_medium().toString()));
            artistTopAlbumName.setText(album.getTitle());
            artistTopAlbumRelease.setText(album.getRelease_date().toInstant()
                    .atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE));
                    artistTopAlbumTracksTV.getItems().addAll(deezerClient.getAlbumTracks(album).getData()
                    .stream().map(track -> (TrackSearch)track).collect(Collectors.toList()));
        });
        artistTopAlbumImg.setVisible(ref.albumShowed);
        artistTopAlbumName.setVisible(ref.albumShowed);
        artistTopAlbumRelease.setVisible(ref.albumShowed);
        artistTopAlbumTracksTV.setVisible(ref.albumShowed);

        fillVBoxWithComments(artistCommentsVBox, deezerClient.getArtistComments(artist));

        artistTabPane.getSelectionModel().select(artistDiscographyTab);
        mainTabPane.getSelectionModel().select(artistTab);
    }

    private void redirectToPlaylist(Playlist playlist) {
        currentPlaylist = playlist;
        playlistPicture.setImage(new Image(playlist.getPicture_medium().toString()));
        playlistTitleLbl.setText(playlist.getTitle());
        playlistCreatorImg.setImage(new Image(playlist.getCreator().getPicture_small().toString()));
        playlistCreatorBtn.setText(playlist.getCreator().getName());
        playlistCreatorBtn.setOnAction(event -> redirectToUser(deezerClient.getUser(playlist.getCreator().getId())));
        playlistDescriptionLbl.setText(playlist.getDescription());
        playlistTracksCountLbl.setText(String.format("%s: %d",
                resources.getString("tracksCnt"), playlist.getNb_tracks()));
        playlistDurationLbl.setText(secondsToNormalTime(playlist.getDuration()));
        playlistFollowersLbl.setText(String.format("%s: %d", resources.getString("followers"), playlist.getFans()));
        if (deezerClient.getLoginStatus() == NOT_AUTHORIZED)
            playlistAddToLibBtn.setVisible(false);
        else {
            playlistAddToLibBtn.setVisible(true);
            playlistFollowingImg.setImage(new Image("img/icon-like.png"));
        }
        editPlaylistBtn.setOnAction(event -> {
            try {
                Playlist updatedPlaylist = PlaylistDialog.showAndWait(playlist);
                if (updatedPlaylist == null) {
                    if (deezerClient.removePlaylist(playlist))
                        homeBtn.getOnAction().handle(new ActionEvent());
                }
                else {
                    if (deezerClient.updatePlaylist(playlist))
                        redirectToPlaylist(playlist);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        playlistTracksTV.getItems().clear();
        playlistTracksTV.getItems().addAll(playlist.getTracks().getData());
        fillVBoxWithComments(playlistCommentariesBox, deezerClient.getPlaylistComments(playlist));
        mainTabPane.getSelectionModel().select(playlistTab);
    }

    private void redirectToRadio(Radio radio) {
        mainTabPane.getSelectionModel().select(radioTab);
    }

    private void redirectToUser(User user) {
        boolean loggedInUser = deezerClient.getLoginStatus() != NOT_AUTHORIZED &&
                deezerClient.getLoggedInUser().getId() == user.getId();
        if (!loggedInUser){
            viewedUserImg.setImage(new Image(user.getPicture_medium().toString()));
            viewedUserNameLbl.setText(user.getName());
        }

        addPlaylistBtn.setVisible(loggedInUser);
        PartialSearchResponse<Track> favTracks = deezerClient.getFavouredTracks(user, null);
        PartialSearchResponse<Playlist> playlists = deezerClient.getFavouredPlaylists(user, null);
        PartialSearchResponse<Album> favAlbums = deezerClient.getFavoredAlbums(user, SearchOrder.ALBUM_ASC);
        PartialSearchResponse<Artist> favArtists = deezerClient.getFavoredArtists(user, SearchOrder.ARTIST_ASC);

        fillFlowPaneWithPlaylists(highlightsPlaylistFP,
                new PartialSearchResponse<>(playlists.getData().stream().limit(4).collect(Collectors.toList())),
                null);
        fillFlowPaneWithAlbums(highlightsAlbumFP,
                new PartialSearchResponse<>(favAlbums.getData().stream().limit(4).collect(Collectors.toList())),
                null);
        fillFlowPaneWithArtists(highlightsArtistFP,
                new PartialSearchResponse<>(favArtists.getData().stream().limit(4).collect(Collectors.toList())),
                null);

        favTracksTV.getItems().clear();
        favTracksTV.getItems().addAll(favTracks.getData());
        favTracksLbl.setText(String.valueOf(favTracks.getTotal()));
        fillFlowPaneWithPlaylists(favPlaylistsFP, playlists, playlistsCntLbl);
        fillFlowPaneWithAlbums(favAlbumsFP, favAlbums, favAlbumsLbl);
        fillFlowPaneWithArtists(favArtistsFP, favArtists, favArtistsLbl);

        myMusicBox.setVisible(loggedInUser);
        viewedUserBox.setVisible(!loggedInUser);
        mainTabPane.getSelectionModel().select(myMusicTab);
    }

    private void search(String query) {
        CompletableFuture<FullSearchSet> searchSet = new CompletableFuture<>();
        searchSet.thenAccept(searchResults -> {
            Platform.runLater(() -> showSearchResults(searchResults));
        });
        searchSet.completeAsync(() -> deezerClient.search(query, null));
    }

    private String secondsToNormalTime(int seconds) {
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        return (time.getHour() > 0 ? String.format("%d %s", time.getHour(), resources.getString("hours")) : "") +
                (time.getMinute() > 0 ? String.format("%d %s", time.getMinute(), resources.getString("minutes")) : "") +
                (time.getSecond() > 0 ? String.format("%d %s", time.getSecond(), resources.getString("seconds")) : "");
    }

    private <T extends TrackSearch> void setupBaseTrackTable(
            TableView<T> trackTable,
            TableColumn<T, T> idxCol,
            TableColumn<T, Album> albumCoverCol,
            TableColumn<T, String> titleCol,
            TableColumn<T, Artist> artistCol,
            TableColumn<T, Album> albumCol,
            TableColumn<T, Integer> durationCol,
            TableColumn<T, Integer> popularityCol) {
        trackTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observableValue, T oldVal, T newVal) {
                selectedTrack = newVal;
                if (newVal == null) {
                    trackLink.setText("");
                    artistLink.setText("");
                    trackInfoBox.setVisible(false);
                    addToPlaylistBtn.setDisable(true);
                }
                else {
                    trackLink.setText(newVal.getTitle());
                    artistLink.setText(newVal.getArtist().getName());
                    trackInfoBox.setVisible(true);
                    addToPlaylistBtn.setDisable(false);
                }
            }
        });

        trackTable.setFixedCellSize(28);
        trackTable.prefHeightProperty().bind(trackTable.fixedCellSizeProperty()
                .multiply(Bindings.size(trackTable.getItems()).add(1.01)));
        trackTable.minHeightProperty().bind(trackTable.prefHeightProperty());
        trackTable.maxHeightProperty().bind(trackTable.prefHeightProperty());

        if (idxCol != null) {
            idxCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
            idxCol.setCellFactory(new Callback<>() {
                @Override
                public TableCell<T, T> call(TableColumn<T, T> param) {
                    return new TableCell<>() {
                        @Override
                        protected void updateItem(T item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(this.getTableRow() == null || item == null ? "" :
                                    String.valueOf(this.getTableRow().getIndex() + 1));
                        }
                    };
                }
            });
        }
        if (albumCoverCol != null) {
            albumCoverCol.setCellValueFactory(new PropertyValueFactory<>("album"));
            albumCoverCol.setCellFactory(param -> {
                final ImageView imageview = new ImageView();
                imageview.setFitHeight(28);
                imageview.setFitWidth(28);
                TableCell<T, Album> cell = new TableCell<>() {
                    @Override
                    public void updateItem(Album album, boolean empty) {
                        if (album != null && imageview.getImage() == null) {
                            imageview.setImage(new Image(album.getCover_small().toString()));
                        } else imageview.setImage(null);
                    }
                };
                cell.setGraphic(imageview);
                return cell;
            });
        }
        if (titleCol != null)
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        if (artistCol != null)
            artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        if (albumCol != null)
            albumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        if (durationCol != null) {
            durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
            durationCol.setCellFactory(new Callback<>() {
                @Override
                public TableCell<T, Integer> call(TableColumn<T, Integer> param) {
                    return new TableCell<>() {
                        @Override
                        public void updateItem(Integer duration, boolean empty) {
                            if (duration != null)
                                this.setText(LocalTime.ofSecondOfDay(duration)
                                        .format(DateTimeFormatter.ofPattern("mm:ss")));
                            else this.setText(null);
                        }
                    };
                }
            });
        }
        if (popularityCol != null)
            popularityCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
    }

    private void showSearchResults(FullSearchSet searchSet) {
        searchTabPane.getTabs().clear();
        searchTabPane.getTabs().add(allResultsTab);

        foundTracksTV.getItems().clear();
        boolean found = searchSet.getTrackResponse().getData().size() > 0;
        if (found) {
            tracksResultTV.getItems().addAll(
                    searchSet.getTrackResponse().getData().stream().limit(6).collect(Collectors.toList()));
            searchTabPane.getTabs().add(trackResultsTab);
            foundTracksTV.getItems().addAll(searchSet.getTrackResponse().getData().stream().filter(TrackSearch::isReadable).collect(Collectors.toList()));
            foundTracksLbl.setText(String.valueOf(searchSet.getTrackResponse().getTotal()));
        }
        tracksResultBtn.setVisible(found);
        tracksResultTV.setVisible(found);

        found = searchSet.getAlbumResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(albumResultsTab);
        albumsResultBtn.setVisible(found);
        albumsResultsFP.setVisible(found);
        fillFlowPaneWithAlbums(albumsResultsFP, new PartialSearchResponse<>(searchSet.getAlbumResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null);
        fillFlowPaneWithAlbums(foundAlbumsFP, searchSet.getAlbumResponse(), foundAlbumsLbl);

        found=searchSet.getArtistResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(artistResultsTab);
        artistsResultBtn.setVisible(found);
        artistsResultsFP.setVisible(found);
        fillFlowPaneWithArtists(artistsResultsFP, new PartialSearchResponse<>(searchSet.getArtistResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null);
        fillFlowPaneWithArtists(foundArtistsFP, searchSet.getArtistResponse(), foundArtistsLbl);

        found = searchSet.getPlaylistResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(playlistResultsTab);
        playlistsResultsBtn.setVisible(found);
        playlistsResultsFP.setVisible(found);
        fillFlowPaneWithPlaylists(playlistsResultsFP, new PartialSearchResponse<>(searchSet.getPlaylistResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null);
        fillFlowPaneWithPlaylists(foundPlaylistsFP, searchSet.getPlaylistResponse(), foundPlaylistsLabel);

        //if (searchSet.getRadioResponse().getData().size() > 0)
        //   searchTabPane.getTabs().add(mixResultsTab);
        //fillFlowPaneWithRadios(foundMixesFP, searchSet.getRadioResponse(), foundMixesLbl);

        found = searchSet.getUserResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(profileResultsTab);
        profilesResultsBtn.setVisible(found);
        profilesResultsFP.setVisible(found);
        fillFlowPaneWithUsers(profilesResultsFP, new PartialSearchResponse<>(searchSet.getUserResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null);
        fillFlowPaneWithUsers(foundProfilesFP, searchSet.getUserResponse(), foundProfilesLbl);

        mainTabPane.getSelectionModel().select(searchTab);
    }

    private void showSettings() {
        User loggedInUser = deezerClient.getLoggedInUser();
        userImg.setImage(new Image(loggedInUser.getPicture_medium().toString()));
        emailTextField.setText(loggedInUser.getEmail());
        passwordTextField.setText("****");
        maleRadioBtn.setSelected(loggedInUser.getGender().equals("M"));
        femaleRadioBtn.setSelected(loggedInUser.getGender().equals("F"));
        usernameTextField.setText(loggedInUser.getName());
        firstnameTextField.setText(loggedInUser.getFirstname());
        lastnameTextField.setText(loggedInUser.getLastname());
        birthdayTextField.setText(loggedInUser.getBirthday().toInstant().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE));
        mainTabPane.getSelectionModel().select(settingsTab);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Controls">
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button homeBtn;
    @FXML
    private Button exploreBtn;
    @FXML
    private Button myMusicBtn;
    @FXML
    private Button favouriteTracksBtn;
    @FXML
    private Button playlistsBtn;
    @FXML
    private Button albumsBtn;
    @FXML
    private Button artistsBtn;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button cancelSearchBtn;
    @FXML
    private Button loginBtn;
    @FXML
    private MenuBar userMenuBar;
    @FXML
    private Menu userMenu;
    @FXML
    private ImageView userAvatar;
    @FXML
    private MenuItem userAccounttem;
    @FXML
    private ImageView userMenuAvatar;
    @FXML
    private MenuItem accountSettingsItem;
    @FXML
    private MenuItem logoutItem;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab searchTab;
    @FXML
    private TabPane searchTabPane;
    @FXML
    private Tab allResultsTab;
    @FXML
    private VBox fullFoundBox;
    @FXML
    private Button tracksResultBtn;
    @FXML
    private TableView<TrackSearch> tracksResultTV;
    @FXML
    private TableColumn<TrackSearch, Album> trackResultImgCol;
    @FXML
    private TableColumn<TrackSearch, String> trackResultTitleCol;
    @FXML
    private TableColumn<TrackSearch, Artist> trackResultArtistCol;
    @FXML
    private TableColumn<TrackSearch, Album> trackResultAlbumCol;
    @FXML
    private TableColumn<TrackSearch, Integer> trackResultLengthCol;
    @FXML
    private TableColumn<TrackSearch, Integer> trackResultPopCol;
    @FXML
    private Button albumsResultBtn;
    @FXML
    private FlowPane artistsResultsFP;
    @FXML
    private FlowPane albumsResultsFP;
    @FXML
    private Button artistsResultBtn;
    @FXML
    private FlowPane tracksResultsFP;
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
    private TableColumn<TrackSearch, Album> foundTrAlbImgCol;
    @FXML
    private TableColumn<TrackSearch, String> foundTrNameCol;
    @FXML
    private TableColumn<TrackSearch, Artist> foundTrArtistCol;
    @FXML
    private TableColumn<TrackSearch, Album> foundTrAlbumCol;
    @FXML
    private TableColumn<TrackSearch, Integer> foundTrLengthCol;
    @FXML
    private TableColumn<TrackSearch, Integer> foundTrPopCol;
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
    private Tab mixResultsTab;
    @FXML
    private Label foundMixesLbl;
    @FXML
    private FlowPane foundMixesFP;
    @FXML
    private Tab profileResultsTab;
    @FXML
    private Label foundProfilesLbl;
    @FXML
    private FlowPane foundProfilesFP;
    @FXML
    private Tab homeTab;
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
    @FXML
    private Tab exploreTab;
    @FXML
    private FlowPane exploreChannelsFP;
    @FXML
    private Tab myMusicTab;
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
    private TableColumn<Track, Track> favTrackIdxCol;
    @FXML
    private TableColumn<Track, String> favTrackTitleCol;
    @FXML
    private TableColumn<Track, Artist> favTrackArtistCol;
    @FXML
    private TableColumn<Track, Album> favTrackAlbumCol;
    @FXML
    private TableColumn<Track, Integer> favTrackLengthCol;
    @FXML
    private TableColumn<Track, Integer> favTrackPopCol;
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
    @FXML
    private Tab albumTab;
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
    @FXML
    private Tab artistTab;
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
    private VBox artistPlaylistsVBox;
    @FXML
    private TableView<TrackSearch> artistTopTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> artistTMPTIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> artistTMPTTitleCol;
    @FXML
    private VBox artistRelatedVBox;
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
    private VBox artistCommentsVBox;
    @FXML
    private Tab playlistTab;
    @FXML
    private ImageView playlistPicture;
    @FXML
    private Label playlistTitleLbl;
    @FXML
    private Button playlistCreatorBtn;
    @FXML
    private ImageView playlistCreatorImg;
    @FXML
    private Label playlistDescriptionLbl;
    @FXML
    private Label playlistTracksCountLbl;
    @FXML
    private Label playlistDurationLbl;
    @FXML
    private Label playlistFollowersLbl;
    @FXML
    private Button playlistListenBtn;
    @FXML
    private Button playlistAddToLibBtn;
    @FXML
    private Button editPlaylistBtn;
    @FXML
    private ImageView playlistFollowingImg;
    @FXML
    private TextField playlistSearchBox;
    @FXML
    private TableView<TrackSearch> playlistTracksTV;
    @FXML
    private TableColumn<TrackSearch, TrackSearch> playlistTrackIdxCol;
    @FXML
    private TableColumn<TrackSearch, String> playlistTrackTitleCol;
    @FXML
    private TableColumn<TrackSearch, Artist> playlistTrackArtistCol;
    @FXML
    private TableColumn<TrackSearch, Album> playlistTrackAlbumCol;
    @FXML
    private TableColumn<TrackSearch, Integer> playlistTrackLengthCol;
    @FXML
    private TableColumn<TrackSearch, Integer> playlistTrackPopularityCol;
    @FXML
    private VBox playlistCommentariesBox;
    @FXML
    private Tab radioTab;
    @FXML
    private Tab settingsTab;
    @FXML
    private ImageView userImg;
    @FXML
    private Label mySubscriptionPlanLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Button emailBtn;
    @FXML
    private Button passwordBtn;
    @FXML
    private TextField passwordTextField;
    @FXML
    private RadioButton maleRadioBtn;
    @FXML
    private ToggleGroup genderToggleGroup;
    @FXML
    private RadioButton femaleRadioBtn;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField birthdayTextField;
    @FXML
    private HBox playerBox;
    @FXML
    private HBox trackInfoBox;
    @FXML
    private Hyperlink trackLink;
    @FXML
    private Hyperlink artistLink;
    @FXML
    private Button textBtn;
    @FXML
    private Button addToPlaylistBtn;
    @FXML
    private Button likeBtn;
    @FXML
    private Slider trackSlider;
    @FXML
    private Label durationLabel;
    @FXML
    private Button repeatBtn;
    @FXML
    private Button shuffleBtn;
    @FXML
    private Button soundBtn;
    @FXML
    private ImageView soundImg;
    @FXML
    private ImageView audioSettingsBtn;
    @FXML
    private ImageView playingImg;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    @FXML
    void accountSettingsItem_OnAction(ActionEvent event) {
        showSettings();
    }

    @FXML
    void addPlaylistBtn_OnAction(ActionEvent actionEvent) {
        try {
            Playlist playlist = PlaylistDialog.showAndWait(null);
            if (playlist != null) {
                deezerClient.createPlaylist(playlist);
                playlistsBtn.getOnAction().handle(new ActionEvent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addToPlaylistBtn_OnAction(ActionEvent event) {
        ChoiceDialog<Playlist> playlistChoicer = new ChoiceDialog<>(null,
                deezerClient.getFavouredPlaylists(deezerClient.getLoggedInUser(), null).getData());
        playlistChoicer.showAndWait().ifPresent(playlist -> {
            if (deezerClient.addTracksToPlaylist(playlist, Collections.singletonList(selectedTrack)))
                new Alert(Alert.AlertType.INFORMATION, "Трек успешно добавлен в плейлист").showAndWait();
        });
    }

    @FXML
    void albumsBtn_OnAction(ActionEvent event) {
        redirectToUser(deezerClient.getLoggedInUser());
        mainTabPane.getSelectionModel().select(myMusicTab);
        myMusicTabPane.getSelectionModel().select(favAlbumsTab);
    }

    @FXML
    void albumsResultBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(albumResultsTab);
    }

    @FXML
    void artistsBtn_OnAction(ActionEvent event) {
        redirectToUser(deezerClient.getLoggedInUser());
        mainTabPane.getSelectionModel().select(myMusicTab);
        myMusicTabPane.getSelectionModel().select(favArtistsTab);
    }

    @FXML
    void artistsResultBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(artistResultsTab);
    }

    @FXML
    void artistPlaylistsBtn_OnAction(ActionEvent actionEvent) {
        artistTabPane.getSelectionModel().select(artistPlaylistsTab);
    }

    @FXML
    void artistMPTracksBtn_OnAction(ActionEvent actionEvent) {
        artistTabPane.getSelectionModel().select(artistPopularTracksTab);
    }

    @FXML
     void artistSimiliarBtn_OnAction(ActionEvent actionEvent) {
        artistTabPane.getSelectionModel().select(artistRelatedTab);
    }

    @FXML
    void cancelSearchBtn_OnAction(ActionEvent event) {
        searchTextField.setText("");
    }

    @FXML
    void emailBtn_OnAction(ActionEvent actionEvent) {
    }

    @FXML
    void exploreBtn_OnAction(ActionEvent event) {
        mainTabPane.getSelectionModel().select(exploreTab);
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
    void homeBtn_OnAction(ActionEvent actionEvent) {
        mainTabPane.getSelectionModel().select(homeTab);
    }

    @FXML
    void favouriteTracksBtn_OnAction(ActionEvent event) {
        redirectToUser(deezerClient.getLoggedInUser());
        mainTabPane.getSelectionModel().select(myMusicTab);
        myMusicTabPane.getSelectionModel().select(favTracksTab);
    }

    @FXML
    void likeBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void loginBtn_OnAction(ActionEvent event) {
        loginBtn.setDisable(true);
        deezerClient.login();
    }

    @FXML
    void logoutItem_OnAction(ActionEvent event) {
        deezerClient.logout();
        changeInterfaceState(true);
    }

    @FXML
    void myMusicBtn_OnAction(ActionEvent event) {
        redirectToUser(deezerClient.getLoggedInUser());
        mainTabPane.getSelectionModel().select(myMusicTab);
        myMusicTabPane.getSelectionModel().select(highlightsTab);
    }

    @FXML
    void passwordBtn_OnAction(ActionEvent actionEvent) {
    }

    @FXML
    void playlistsBtn_OnAction(ActionEvent event) {
        redirectToUser(deezerClient.getLoggedInUser());
        mainTabPane.getSelectionModel().select(myMusicTab);
        myMusicTabPane.getSelectionModel().select(myPlaylistsTab);
    }

    @FXML
    void playlistSearchBox_OnKeyPressed(KeyEvent keyEvent) {
    }

    @FXML
    void playlistsResultsBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(playlistResultsTab);
    }

    @FXML
    void profilesResultsBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(profileResultsTab);
    }

    @FXML
    void tracksResultBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(trackResultsTab);
    }

    @FXML
    void searchTextField_OnKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && searchTextField.getText() != null) {
            if (searchTextField.getText().trim().length() == 0)
                searchTextField.setText(null);
            else search(searchTextField.getText());
        }
    }

    @FXML
    void shuffleBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void shuffleMusicBtn_OnAction(ActionEvent actionEvent) {
    }

    @FXML
    void repeatBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void textBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void userAccountItem_OnAction(ActionEvent event) {
        myMusicBtn.getOnAction().handle(new ActionEvent());
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Controller initialisation">
    @FXML
    void initialize() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) ->
                cancelSearchBtn.setVisible(newValue != null && newValue.length() > 0));

        setupBaseTrackTable(favTracksTV, favTrackIdxCol, null, favTrackTitleCol, favTrackArtistCol,
                favTrackAlbumCol, favTrackLengthCol, favTrackPopCol);

        setupBaseTrackTable(tracksResultTV, null, trackResultImgCol, trackResultTitleCol, trackResultArtistCol,
                trackResultAlbumCol, trackResultLengthCol, trackResultPopCol);
        setupBaseTrackTable(foundTracksTV,null, foundTrAlbImgCol, foundTrNameCol, foundTrArtistCol,
                foundTrAlbumCol, foundTrLengthCol, foundTrPopCol);
        setupBaseTrackTable(albumTracksTV, albumTrackIdxCol, null, albumTrackName, null, null,
                albumTrackDurCol, albumTrackPopCol);
        setupBaseTrackTable(artistTopTracksTV, artistTMPTIdxCol, null, artistTMPTTitleCol, null,
                null, null, null);
        setupBaseTrackTable(artistTopAlbumTracksTV, artistTATIdxCol, null, artistTATTitleCol, null,
                null, null, null);
        setupBaseTrackTable(artistPopTracksTV, artistMPTIdxCol, null, artistMPTTitleCol, artistMPTArtistCol,
                artistMPTAlbumCol, artistMPTLengthCol, artistMPTPopCol);
        setupBaseTrackTable(playlistTracksTV, playlistTrackIdxCol, null, playlistTrackTitleCol,
                playlistTrackArtistCol, playlistTrackAlbumCol, playlistTrackLengthCol, playlistTrackPopularityCol);

        try {
            deezerClient = new Deezer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(this::onLoginResponse));
        if (deezerClient.getLoginStatus() != NOT_AUTHORIZED)
            loginBtn.getOnAction().handle(new ActionEvent());
    }

    @FXML
    void removeTrackFromFav(ActionEvent event) {
        if (deezerClient.removeTrackFromFavourites(selectedTrack)){
            new Alert(Alert.AlertType.INFORMATION, "Удаление успешно");
            playlistsBtn.getOnAction().handle(new ActionEvent());
        }
        else new Alert(Alert.AlertType.INFORMATION, "Удаление отклонено сервером");
    }

    public void removeTrackFromPlaylist(ActionEvent event) {
        if (deezerClient.removeTracksFromPlaylist(currentPlaylist, Collections.singletonList(selectedTrack))){
            new Alert(Alert.AlertType.INFORMATION, "Удаление успешно");
            playlistsBtn.getOnAction().handle(new ActionEvent());
        }
        else new Alert(Alert.AlertType.INFORMATION, "Удаление отклонено сервером");
    }
    //</editor-fold>
}
