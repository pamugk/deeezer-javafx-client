package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import api.Deezer;
import api.PartialSearchResponse;
import api.events.authentication.AuthenticationEvent;
import api.events.base.DeezerListener;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.User;
import api.objects.utils.search.FullSearchSet;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import static api.LoginStatus.NOT_AUTHORIZED;

public class IndexController {
    private Deezer deezerClient;

    public static void show(Stage primaryStage) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader loader = new FXMLLoader(IndexController.class.getResource("/fxml/index.fxml"), bundle);
        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.getIcons().add(
                new Image(IndexController.class.getResourceAsStream("/img/deezer-icon.jpg")));
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(100);
        IndexController controller = loader.getController();
        //controller.mainTabPane.prefHeightProperty().bind(Bindings.add(primaryStage.heightProperty(), -100));
        primaryStage.setOnCloseRequest(event -> controller.deezerClient.stop());
        primaryStage.show();
    }

    //<editor-fold defaultstate="collapsed" desc="Auxiliary methods">\
    private void changeInterfaceState(boolean logout) {
        if (logout)
            homeBtn.getOnAction().handle(new ActionEvent());
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
            albumBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4)));
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
            artistBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4)));
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

            Label artistFollowers = new Label(String.format("%s: %s", resources.getString("followers"),
                    artist.getNb_fan()));
            artistName.getStyleClass().add("artistcard-followers");
            artistName.setPrefWidth(Region.USE_COMPUTED_SIZE);
            artistName.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistBox.getChildren().add(artistFollowers);

            flow.getChildren().add(artistBox);
        }
        if (countLabel != null)
            countLabel.setText(String.valueOf(artists.getTotal()));
    }

    private void fillFlowPaneWithPlaylists(FlowPane flow, PartialSearchResponse<Playlist> playlists, Label countLabel) {
        flow.getChildren().clear();
        for (Playlist playlist : playlists.getData()) {
            VBox playlistBox = new VBox();
            playlistBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4)));
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
            countLabel.setText(String.valueOf(playlists.getTotal()));
    }

    private void fillFlowPaneWithRadios(FlowPane flow, PartialSearchResponse<Radio> radios, Label countLabel) {
        flow.getChildren().clear();
        for (Radio radio : radios.getData()) {
            VBox radioBox = new VBox();
            radioBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4)));
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
            userBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4)));
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
                    comment.getDate().toInstant().atZone(ZoneId.systemDefault())
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

    private void fillVBoxWithTracks(VBox tracksVBox, List<TrackSearch> tracks) {
        tracksVBox.getChildren().clear();
        for (TrackSearch track : tracks) {
            HBox trackBox = new HBox();
            trackBox.setPadding(new Insets(0, 10, 0, 0));
            trackBox.setAlignment(Pos.CENTER);
            trackBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            trackBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            if (deezerClient.getLoginStatus() != NOT_AUTHORIZED) {
                ImageView trackLikeImg = new ImageView("img/icon-like.png");
                var ref = new Object() {
                    boolean liked = false;
                };
                trackLikeImg.setFitHeight(12);
                trackLikeImg.setFitWidth(12);
                Button likeTrack = new Button(null, trackLikeImg);
                likeTrack.getStyleClass().add("deezer-button");
                likeTrack.setOnAction(event -> {
                    if (ref.liked) {
                        deezerClient.removeTrackFromFavourites(track);
                        ref.liked = false;
                    }
                    else {
                        deezerClient.addTrackToFavourites(track);
                        ref.liked = true;
                    }

                });
                trackBox.getChildren().add(likeTrack);
            }

            Label trackTitle = new Label(track.getTitle());
            trackTitle.getStyleClass().add("deezer-secondary");
            trackBox.getChildren().add(trackTitle);

            tracksVBox.getChildren().add(trackBox);
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
            changeInterfaceState(false);
        });
    }

    private void redirectToAlbum(Album album) {
        albumCover.setImage(new Image(album.getCover_medium().toString()));
        albumName.setText(album.getTitle());
        albumArtistImg.setImage(new Image(album.getArtist().getPicture_small().toString()));
        albumArtist.setText(album.getArtist().getName());
        albumArtist.setOnAction(event -> deezerClient.getArtist(album.getArtist().getId()));
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
        fillVBoxWithTracks(artistTopTracksVBox, popularTracks.getData().stream().limit(4).collect(Collectors.toList()));
        artistPopTracksTV.getItems().clear();
        artistPopTracksTV.getItems().addAll(popularTracks.getData());

        PartialSearchResponse<Artist> similiarArtists = deezerClient.getArtistRelated(artist, 25);
        fillVBoxWithArtists(artistRelatedVBox, similiarArtists.getData().stream().limit(3).collect(Collectors.toList()));
        fillFlowPaneWithArtists(artistRelatedFP, similiarArtists, null);

        PartialSearchResponse<Playlist> playlists = deezerClient.getArtistPlaylists(artist, 25);
        fillVBoxWithPlaylists(artistPlaylistsVBox, playlists.getData().stream().limit(3).collect(Collectors.toList()));
        fillFlowPaneWithPlaylists(artistPlaylistsFP, playlists, null);

        PartialSearchResponse<Album> discography = deezerClient.getArtistDiscography(artist);
        fillFlowPaneWithAlbums(artistDiscographyFP, discography, null);


        fillVBoxWithComments(artistCommentsVBox, deezerClient.getArtistComments(artist));

        artistTabPane.getSelectionModel().select(artistDiscographyTab);
        mainTabPane.getSelectionModel().select(artistTab);
    }

    private void redirectToPlaylist(Playlist playlist) {
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
        playlistTracksTV.getItems().clear();
        playlistTracksTV.getItems().addAll(playlist.getTracks().getData());
        fillVBoxWithComments(playlistCommentariesBox, deezerClient.getPlaylistComments(playlist));
        mainTabPane.getSelectionModel().select(playlistTab);
    }

    private void redirectToRadio(Radio radio) {
        mainTabPane.getSelectionModel().select(radioTab);
    }

    private void redirectToUser(User user) {

        mainTabPane.getSelectionModel().select(myMusicTab);
    }

    private void search(String query) {
        FullSearchSet searchSet = deezerClient.search(query, null);
        showSearchResults(searchSet);
    }

    private String secondsToNormalTime(int seconds) {
        LocalTime time = LocalTime.ofSecondOfDay(seconds);
        return (time.getHour() > 0 ? String.format("%d %s", time.getHour(), resources.getString("hours")) : "") +
                (time.getMinute() > 0 ? String.format("%d %s", time.getMinute(), resources.getString("minutes")) : "") +
                (time.getSecond() > 0 ? String.format("%d %s", time.getSecond(), resources.getString("seconds")) : "");
    }

    private void setupBaseTrackTable(
            TableView<TrackSearch> trackTable,
            TableColumn<TrackSearch, TrackSearch> idxCol,
            TableColumn<TrackSearch, Album> albumCoverCol,
            TableColumn<TrackSearch, String> titleCol,
            TableColumn<TrackSearch, Artist> artistCol,
            TableColumn<TrackSearch, Album> albumCol,
            TableColumn<TrackSearch, Integer> durationCol,
            TableColumn<TrackSearch, Integer> popularityCol) {

        trackTable.setFixedCellSize(50);
        trackTable.prefHeightProperty().bind(trackTable.fixedCellSizeProperty().multiply(Bindings.size(trackTable.getItems()).add(1.01)));
        trackTable.minHeightProperty().bind(trackTable.prefHeightProperty());
        trackTable.maxHeightProperty().bind(trackTable.prefHeightProperty());

        if (idxCol != null) {
            idxCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
            idxCol.setCellFactory(new Callback<>() {
                @Override
                public TableCell<TrackSearch, TrackSearch> call(TableColumn<TrackSearch, TrackSearch> param) {
                    return new TableCell<>() {
                        @Override
                        protected void updateItem(TrackSearch item, boolean empty) {
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
                TableCell<TrackSearch, Album> cell = new TableCell<>() {
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
                public TableCell<TrackSearch, Integer> call(TableColumn<TrackSearch, Integer> param) {
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
        if (searchSet.getTrackResponse().getData().size() > 0) {
            searchTabPane.getTabs().add(trackResultsTab);
            foundTracksTV.getItems().addAll(searchSet.getTrackResponse().getData().stream().filter(TrackSearch::isReadable).collect(Collectors.toList()));
            foundTracksLbl.setText(String.valueOf(searchSet.getTrackResponse().getTotal()));
        }

        if (searchSet.getAlbumResponse().getData().size() > 0)
            searchTabPane.getTabs().add(albumResultsTab);
        fillFlowPaneWithAlbums(foundAlbumsFP, searchSet.getAlbumResponse(), foundAlbumsLbl);

        if (searchSet.getArtistResponse().getData().size() > 0)
            searchTabPane.getTabs().add(artistResultsTab);
        fillFlowPaneWithArtists(foundArtistsFP, searchSet.getArtistResponse(), foundArtistsLbl);

        if (searchSet.getPlaylistResponse().getData().size() > 0)
            searchTabPane.getTabs().add(playlistResultsTab);
        fillFlowPaneWithPlaylists(foundPlaylistsFP, searchSet.getPlaylistResponse(), foundPlaylistsLabel);

        if (searchSet.getRadioResponse().getData().size() > 0)
            searchTabPane.getTabs().add(mixResultsTab);
        fillFlowPaneWithRadios(foundMixesFP, searchSet.getRadioResponse(), foundMixesLbl);

        if (searchSet.getUserResponse().getData().size() > 0)
            searchTabPane.getTabs().add(profileResultsTab);
        fillFlowPaneWithUsers(foundProfilesFP, searchSet.getUserResponse(), foundProfilesLbl);

        mainTabPane.getSelectionModel().select(searchTab);
    }

    private void showSettings() {
        User loggedInUser = deezerClient.getLoggedInUser();
        userImg.setImage(new Image(loggedInUser.getPicture_medium().toString()));
        emailTextField.setText(loggedInUser.getEmail());
        passwordTextField.setText("****");
        maleRadioBtn.setSelected(loggedInUser.getGender().equals("M"));
        usernameTextField.setText(loggedInUser.getName());
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
    private Tab exploreTab;
    @FXML
    private FlowPane exploreChannelsFP;
    @FXML
    private Tab myMusicTab;
    @FXML
    private HBox myMusicBox;
    @FXML
    private Button shuffleMusicBtn;
    @FXML
    private TabPane myMusicTabPane;
    @FXML
    private Tab highlightsTab;
    @FXML
    private FlowPane recentFP;
    @FXML
    private FlowPane highlightsPlaylistFP;
    @FXML
    private FlowPane highlightsAlbumFP;
    @FXML
    private FlowPane highlightsArtistFP;
    @FXML
    private Tab favTracksTab;
    @FXML
    private Label favTracksLbl;
    @FXML
    private TableView<Track> favTracksTV;
    @FXML
    private Tab myPlaylistsTab;
    @FXML
    private Label favPlaylistsLabel;
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
    private ScrollPane albumScrollPane;
    @FXML
    private VBox albumVBox;
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
    private VBox artistTopTracksVBox;
    @FXML
    private VBox artistRelatedVBox;
    @FXML
    private ImageView artistTopAlbumImg;
    @FXML
    private Label artistTopAlbumName;
    @FXML
    private Label artistTopAlbumRelease;
    @FXML
    private VBox artistTopAlbumTracksBox;
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
    public VBox artistCommentsVBox;
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
    private ImageView playlistFollowingImg;
    @FXML
    private TextField playlistSearchBox;
    @FXML
    private TableView<TrackSearch> playlistTracksTV;
    @FXML
    public TableColumn<TrackSearch, TrackSearch> playlistTrackIdxCol;
    @FXML
    public TableColumn<TrackSearch, String> playlistTrackTitleCol;
    @FXML
    public TableColumn<TrackSearch, Artist> playlistTrackArtistCol;
    @FXML
    public TableColumn<TrackSearch, Album> playlistTrackAlbumCol;
    @FXML
    public TableColumn<TrackSearch, Integer> playlistTrackLengthCol;
    @FXML
    public TableColumn<TrackSearch, Integer> playlistTrackPopularityCol;
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
    public RadioButton maleRadioBtn;
    @FXML
    public ToggleGroup genderToggleGroup;
    @FXML
    public RadioButton femaleRadioBtn;
    @FXML
    public TextField usernameTextField;
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
    void addToPlaylistBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void albumsBtn_OnAction(ActionEvent event) {
        redirectToUser(deezerClient.getLoggedInUser());
        myMusicTabPane.getSelectionModel().select(favAlbumsTab);
    }

    @FXML
    void artistsBtn_OnAction(ActionEvent event) {
        redirectToUser(deezerClient.getLoggedInUser());
        myMusicTabPane.getSelectionModel().select(favArtistsTab);
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
    void homeBtn_OnAction(ActionEvent actionEvent) {
        mainTabPane.getSelectionModel().select(homeTab);
    }

    @FXML
    void favouriteTracksBtn_OnAction(ActionEvent event) {
        redirectToUser(deezerClient.getLoggedInUser());
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
        mainTabPane.getSelectionModel().select(highlightsTab);
    }

    @FXML
    void passwordBtn_OnAction(ActionEvent actionEvent) {
    }

    @FXML
    void playlistsBtn_OnAction(ActionEvent event) {
        redirectToUser(deezerClient.getLoggedInUser());
        myMusicTabPane.getSelectionModel().select(myPlaylistsTab);
    }

    @FXML
    void playlistSearchBox_OnKeyPressed(KeyEvent keyEvent) {
    }

    @FXML
    void repeatBtn_OnAction(ActionEvent event) {

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

        setupBaseTrackTable(foundTracksTV,null, foundTrAlbImgCol, foundTrNameCol, foundTrArtistCol,
                foundTrAlbumCol, foundTrLengthCol, foundTrPopCol);
        setupBaseTrackTable(albumTracksTV, albumTrackIdxCol, null, albumTrackName, null, null,
                albumTrackDurCol, albumTrackPopCol);
        setupBaseTrackTable(artistPopTracksTV, artistMPTIdxCol, null, artistMPTTitleCol, artistMPTArtistCol,
                artistMPTAlbumCol, artistMPTLengthCol, artistMPTPopCol);
        setupBaseTrackTable(playlistTracksTV, playlistTrackIdxCol, null, playlistTrackTitleCol, playlistTrackArtistCol,
                playlistTrackAlbumCol, playlistTrackLengthCol, playlistTrackPopularityCol);

        try {
            deezerClient = new Deezer();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(this::onLoginResponse));
    }
    //</editor-fold>
}
