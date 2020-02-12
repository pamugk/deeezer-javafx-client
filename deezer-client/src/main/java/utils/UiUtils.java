package utils;

import api.PartialSearchResponse;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.User;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UiUtils {
    public static void fillFlowPaneWithAlbums(FlowPane flow, PartialSearchResponse<Album> albums, Label countLabel,
                                        boolean clear, boolean tryGetNext) {
        Platform.runLater(() -> {
            if (clear)
                flow.getChildren().clear();
            for (Album album : albums.getData()) {
                VBox albumBox = new VBox();
                albumBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
                albumBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
                albumBox.getStyleClass().add("albumcard");

                ImageView albumCover = new ImageView(album.getCover_medium() == null ? null : new Image(album.getCover_medium().toString(), true));
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
            if (!albums.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(albums.getTotal()));

            if (tryGetNext && albums.hasNext())
            {
                CompletableFuture<PartialSearchResponse<Album>> nextPart = new CompletableFuture<>();
                nextPart.completeAsync(() -> deezerClient.getNextPart(albums)).thenAccept(nextAlbums -> {
                    fillFlowPaneWithAlbums(flow, nextAlbums, countLabel, false, tryGetNext);
                });
            }
        });
    }

    public static void fillFlowPaneWithArtists(FlowPane flow, PartialSearchResponse<Artist> artists, Label countLabel,
                                         boolean clear, boolean tryGetNext) {
        Platform.runLater(() -> {
            if (clear)
                flow.getChildren().clear();
            for (Artist artist : artists.getData()) {
                VBox artistBox = new VBox();
                artistBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
                artistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
                artistBox.getStyleClass().add("artistcard");

                ImageView artistPicture = new ImageView(new Image(artist.getPicture_medium().toString(), true));
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
            if (!artists.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(artists.getTotal()));

            if (tryGetNext && artists.hasNext())
            {
                CompletableFuture<PartialSearchResponse<Artist>> nextPart = new CompletableFuture<>();
                nextPart.completeAsync(() -> deezerClient.getNextPart(artists))
                        .thenAccept(nextArtists -> {
                            fillFlowPaneWithArtists(flow, nextArtists, countLabel, false, tryGetNext);
                        });
            }
        });
    }

    public static void fillFlowPaneWithPlaylists(FlowPane flow, PartialSearchResponse<Playlist> playlists, Label countLabel,
                                           boolean clear, boolean tryGetNext) {
        Platform.runLater(() -> {
            if (clear)
                flow.getChildren().clear();
            for (Playlist playlist : playlists.getData()) {
                if (playlist.is_loved_track())
                    continue;
                VBox playlistBox = new VBox();
                playlistBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
                playlistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
                playlistBox.getStyleClass().add("playlistcard");

                ImageView playlistPicture = new ImageView(new Image(playlist.getPicture_medium().toString(), true));
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
            if (!playlists.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(playlists.getTotal() - 1));

            if (tryGetNext && playlists.hasNext())
            {
                CompletableFuture<PartialSearchResponse<Playlist>> nextPart = new CompletableFuture<>();
                nextPart.completeAsync(() -> deezerClient.getNextPart(playlists))
                        .thenAccept(nextPlaylists -> fillFlowPaneWithPlaylists(flow, nextPlaylists, countLabel, false, tryGetNext));
            }
        });
    }

    public static void fillFlowPaneWithRadios(FlowPane flow, PartialSearchResponse<Radio> radios, Label countLabel,
                                        boolean clear, boolean tryGetNext) {
        Platform.runLater(() -> {
            if (clear)
                flow.getChildren().clear();
            for (Radio radio : radios.getData()) {
                VBox radioBox = new VBox();
                radioBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
                radioBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
                radioBox.getStyleClass().add("radiocard");

                ImageView radioPicture = new ImageView(new Image(radio.getPicture_medium().toString(), true));
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
            if (!radios.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(radios.getTotal()));
            if (tryGetNext && radios.getTotal() > flow.getChildren().size())
            {
                CompletableFuture<PartialSearchResponse<Radio>> nextPart = new CompletableFuture<>();
                nextPart.completeAsync(() -> deezerClient.getNextPart(radios))
                        .thenAccept(nextRadios -> fillFlowPaneWithRadios(flow, nextRadios, countLabel, false, tryGetNext));
            }
        });
    }

    public static void fillFlowPaneWithUsers(FlowPane flow, PartialSearchResponse<User> users, Label countLabel,
                                       boolean clear, boolean tryGetNext) {
        Platform.runLater(() -> {
            if (clear)
                flow.getChildren().clear();
            for (User user : users.getData()) {
                VBox userBox = new VBox();
                userBox.prefWidthProperty().bind(Bindings.add(-35, flow.widthProperty().divide(4.2)));
                userBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
                userBox.getStyleClass().add("usercard");

                ImageView userPicture = new ImageView(new Image(user.getPicture_medium().toString(), true));
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
            if (!users.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(users.getTotal()));
            if (tryGetNext && users.hasNext())
            {
                CompletableFuture<PartialSearchResponse<User>> nextPart = new CompletableFuture<>();
                nextPart.completeAsync(() -> deezerClient.getNextPart(users))
                        .thenAccept(nextUsers -> fillFlowPaneWithUsers(flow, nextUsers, countLabel, false, tryGetNext));
            }
        });
    }

    public static  <T extends TrackSearch> void fillTableWithTracks(TableView<T> trackTable,
                                                             PartialSearchResponse<T> tracks, Label countLabel)
    {
        Platform.runLater(() -> {
            trackTable.getItems().addAll(tracks.getData());
            if (!tracks.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(tracks.getTotal()));
            if (tracks.hasNext())
            {
                CompletableFuture<PartialSearchResponse<T>> nextPart = new CompletableFuture<>();
                nextPart.completeAsync(() -> deezerClient.getNextPart(tracks))
                        .thenAccept(nextTracks -> fillTableWithTracks(trackTable, nextTracks, countLabel));
            }
        });
    }

    public static void fillVBoxWithArtists(VBox artistVBox, List<Artist> artists) {
        artistVBox.getChildren().clear();
        for (Artist artist : artists) {
            HBox artistBox = new HBox();
            artistBox.setPadding(new Insets(0, 10, 0, 0));
            artistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            ImageView artistPicture = new ImageView(new Image(artist.getPicture_small().toString(), true));
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

    public static void fillVBoxWithPlaylists(VBox playlistsVBox, List<Playlist> playlists) {
        playlistsVBox.getChildren().clear();
        for (Playlist playlist : playlists) {
            HBox playlistBox = new HBox();
            playlistBox.setPadding(new Insets(0, 10, 0, 0));
            playlistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

            ImageView playlistPicture = new ImageView(new Image(playlist.getPicture_small().toString(), true));
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

    public static void fillVBoxWithComments(VBox commentsBox, PartialSearchResponse<Comment> comments) {
        commentsBox.getChildren().clear();
        for (Comment comment : comments.getData()) {
            HBox commentBox = new HBox();
            commentBox.setPadding(new Insets(0, 24, 0, 24));
            commentBox.setSpacing(24);
            commentBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            commentBox.setPrefHeight(Region.USE_COMPUTED_SIZE);

            ImageView commentorImg = new ImageView(new Image(comment.getAuthor().getPicture_small().toString(), true));
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

    private <T extends TrackSearch> void setupBaseTrackTable(
            TableView<T> trackTable,
            TableColumn<T, T> idxCol,
            TableColumn<T, Album> albumCoverCol,
            TableColumn<T, String> titleCol,
            TableColumn<T, Artist> artistCol,
            TableColumn<T, Album> albumCol,
            TableColumn<T, Integer> durationCol,
            TableColumn<T, Integer> popularityCol) {

        trackTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) ->
                musicPlayer.setSelectedTrack(newVal));

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
                            imageview.setImage(new Image(album.getCover_small().toString(), true));
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
}
