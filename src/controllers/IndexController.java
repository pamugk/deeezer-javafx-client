package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import api.Deezer;
import api.events.authentication.AuthenticationEvent;
import api.events.base.DeezerListener;
import api.objects.playables.*;
import api.objects.utils.User;
import api.objects.utils.search.FullSearchSet;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

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
        IndexController controller = loader.getController();
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

    private void fillFlowPaneWithAlbums(FlowPane flow, List<Album> albums, Label countLabel) {
        flow.getChildren().clear();
        for (Album album : albums) {
            VBox albumBox = new VBox();
            albumBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            albumBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumBox.getStyleClass().add("albumcard");

            Button albumRedirectButton = new Button(null, new ImageView(album.getCover_medium().toString()));
            albumRedirectButton.setOnAction(event -> redirectToAlbum(album));
            albumRedirectButton.getStyleClass().add("albumcard-album");
            albumRedirectButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
            albumRedirectButton.setPrefHeight(Region.USE_COMPUTED_SIZE);
            albumBox.getChildren().add(albumRedirectButton);

            Label albumTitle = new Label(album.getTitle());
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
        countLabel.setText(String.valueOf(albums.size()));
    }

    private void fillFlowPaneWithArtists(FlowPane flow, List<Artist> artists, Label countLabel) {
        flow.getChildren().clear();
        for (Artist artist : artists) {
            VBox artistBox = new VBox();
            artistBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            artistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistBox.getStyleClass().add("artistcard");

            Button artistRedirectButton = new Button(null,
                    new ImageView(artist.getPicture_medium().toString()));
            artistRedirectButton.setOnAction(event -> redirectToArtist(artist));
            artistRedirectButton.getStyleClass().add("artistcard-artist");
            artistRedirectButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
            artistRedirectButton.setPrefHeight(Region.USE_COMPUTED_SIZE);
            artistBox.getChildren().add(artistRedirectButton);

            Label artistName = new Label(artist.getName());
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
        countLabel.setText(String.valueOf(artists.size()));
    }

    private void fillFlowPaneWithPlaylists(FlowPane flow, List<Playlist> playlists, Label countLabel) {
        flow.getChildren().clear();
        for (Playlist playlist : playlists) {
            VBox playlistBox = new VBox();
            playlistBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            playlistBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistBox.getStyleClass().add("playlistcard");

            Button playlistRedirectButton = new Button(null,
                    new ImageView(playlist.getPicture_medium().toString()));
            playlistRedirectButton.setOnAction(event -> redirectToPlaylist(playlist));
            playlistRedirectButton.getStyleClass().add("playlistcard-playlist");
            playlistRedirectButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
            playlistRedirectButton.setPrefHeight(Region.USE_COMPUTED_SIZE);
            playlistBox.getChildren().add(playlistRedirectButton);

            Label playlistTitle = new Label(playlist.getTitle());
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
        countLabel.setText(String.valueOf(playlists.size()));
    }

    private void fillFlowPaneWithRadios(FlowPane flow, List<Radio> radios, Label countLabel) {
        flow.getChildren().clear();
        for (Radio radio : radios) {
            VBox radioBox = new VBox();
            radioBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            radioBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            radioBox.getStyleClass().add("radiocard");

            Button radioRedirectButton = new Button(null,
                    new ImageView(radio.getPicture_medium().toString()));
            radioRedirectButton.setOnAction(event -> redirectToRadio(radio));
            radioRedirectButton.getStyleClass().add("radiocard-radio");
            radioRedirectButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
            radioRedirectButton.setPrefHeight(Region.USE_COMPUTED_SIZE);
            radioBox.getChildren().add(radioRedirectButton);

            flow.getChildren().add(radioBox);
        }
        countLabel.setText(String.valueOf(radios.size()));
    }

    private void fillFlowPaneWithUsers(FlowPane flow, List<User> users, Label countLabel) {
        flow.getChildren().clear();
        for (User user : users) {
            VBox userBox = new VBox();
            userBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            userBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            userBox.getStyleClass().add("usercard");

            Button userRedirectButton = new Button(null,
                    new ImageView(user.getPicture_medium().toString()));
            userRedirectButton.setOnAction(event -> redirectToUser(user));
            userRedirectButton.getStyleClass().add("usercard-user");
            userRedirectButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
            userRedirectButton.setPrefHeight(Region.USE_COMPUTED_SIZE);
            userBox.getChildren().add(userRedirectButton);

            Label userName = new Label(user.getName());
            userName.getStyleClass().add("usercard-name");
            userName.setPrefWidth(Region.USE_COMPUTED_SIZE);
            userName.setPrefHeight(Region.USE_COMPUTED_SIZE);
            userBox.getChildren().add(userName);

            flow.getChildren().add(userBox);
        }
        countLabel.setText(String.valueOf(users.size()));
    }

    private void fillTableWithTracks(TableView<TrackSearch> trackTable, List<TrackSearch> tracks, Label countLabel) {
        trackTable.getItems().clear();
        trackTable.getItems().addAll(tracks.stream().filter(TrackSearch::isReadable).collect(Collectors.toList()));
        countLabel.setText(String.valueOf(tracks.size()));
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

    }

    private void redirectToArtist(Artist artist) {

    }

    private void redirectToPlaylist(Playlist playlist) {

    }

    private void redirectToRadio(Radio radio) {

    }

    private void redirectToUser(User user) {

    }

    private void showSearchResults(FullSearchSet searchSet) {
        searchTabPane.getTabs().clear();
        searchTabPane.getTabs().add(allResultsTab);

        if (searchSet.getTrackResponse().getData().size() > 0)
            searchTabPane.getTabs().add(trackResultsTab);
        fillTableWithTracks(foundTracksTV, searchSet.getTrackResponse().getData(), foundTracksLbl);

        if (searchSet.getAlbumResponse().getData().size() > 0)
            searchTabPane.getTabs().add(albumResultsTab);
        fillFlowPaneWithAlbums(foundAlbumsFP, searchSet.getAlbumResponse().getData(), foundAlbumsLbl);

        if (searchSet.getArtistResponse().getData().size() > 0)
            searchTabPane.getTabs().add(artistResultsTab);
        fillFlowPaneWithArtists(foundArtistsFP, searchSet.getArtistResponse().getData(), foundArtistsLbl);

        if (searchSet.getPlaylistResponse().getData().size() > 0)
            searchTabPane.getTabs().add(playlistResultsTab);
        fillFlowPaneWithPlaylists(foundPlaylistsFP, searchSet.getPlaylistResponse().getData(), foundPlaylistsLabel);

        if (searchSet.getRadioResponse().getData().size() > 0)
            searchTabPane.getTabs().add(mixResultsTab);
        fillFlowPaneWithRadios(foundMixesFP, searchSet.getRadioResponse().getData(), foundMixesLbl);

        if (searchSet.getUserResponse().getData().size() > 0)
            searchTabPane.getTabs().add(profileResultsTab);
        fillFlowPaneWithUsers(foundProfilesFP, searchSet.getUserResponse().getData(), foundProfilesLbl);

        mainTabPane.getSelectionModel().select(searchTab);
    }

    private void search(String query) {
        FullSearchSet searchSet = deezerClient.search(query, null);
        showSearchResults(searchSet);
    }

    private void setupFoundTracksTable() {
        foundTrAlbImgCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        foundTrAlbImgCol.setCellFactory(param -> {
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

        foundTrNameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        foundTrArtistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        foundTrAlbumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        foundTrLengthCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        foundTrLengthCol.setCellFactory(new Callback<>() {
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
        foundTrPopCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
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
    private ScrollPane foundTracksScroll;
    @FXML
    private Label foundTracksLbl;
    @FXML
    private TableView<TrackSearch> foundTracksTV;
    @FXML
    public TableColumn<TrackSearch, Album> foundTrAlbImgCol;
    @FXML
    public TableColumn<TrackSearch, String> foundTrNameCol;
    @FXML
    public TableColumn<TrackSearch, Artist> foundTrArtistCol;
    @FXML
    public TableColumn<TrackSearch, Album> foundTrAlbumCol;
    @FXML
    public TableColumn<TrackSearch, Integer> foundTrLengthCol;
    @FXML
    public TableColumn<TrackSearch, Integer> foundTrPopCol;
    @FXML
    private Tab albumResultsTab;
    @FXML
    public ScrollPane foundAlbumsScroll;
    @FXML
    private Label foundAlbumsLbl;
    @FXML
    private FlowPane foundAlbumsFP;
    @FXML
    private Tab artistResultsTab;
    @FXML
    public ScrollPane foundArtistsScroll;
    @FXML
    private Label foundArtistsLbl;
    @FXML
    private FlowPane foundArtistsFP;
    @FXML
    private Tab playlistResultsTab;
    @FXML
    public ScrollPane foundPlaylistsScroll;
    @FXML
    private Label foundPlaylistsLabel;
    @FXML
    private FlowPane foundPlaylistsFP;
    @FXML
    private Tab mixResultsTab;
    @FXML
    public ScrollPane foundMixesScroll;
    @FXML
    private Label foundMixesLbl;
    @FXML
    private FlowPane foundMixesFP;
    @FXML
    private Tab profileResultsTab;
    @FXML
    public ScrollPane foundProfilesScroll;
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
    public Tab highlightsTab;
    @FXML
    private FlowPane recentFP;
    @FXML
    private FlowPane highlightsPlaylistFP;
    @FXML
    private FlowPane highlightsAlbumFP;
    @FXML
    private FlowPane highlightsArtistFP;
    @FXML
    public Tab favTracksTab;
    @FXML
    private Label favTracksLbl;
    @FXML
    private TableView<Track> favTracksTV;
    @FXML
    public Tab myPlaylistsTab;
    @FXML
    private Label favPlaylistsLabel;
    @FXML
    private FlowPane favPlaylistsFP;
    @FXML
    public Tab favAlbumsTab;
    @FXML
    private Label favAlbumsLbl;
    @FXML
    private FlowPane favAlbumsFP;
    @FXML
    public Tab favArtistsTab;
    @FXML
    private Label favArtistsLbl;
    @FXML
    private FlowPane favArtistsFP;
    @FXML
    private Tab albumTab;
    @FXML
    public ImageView albumCover;
    @FXML
    public Label albumName;
    @FXML
    public Button albumArtist;
    @FXML
    public Button listenAlbumBtn;
    @FXML
    public Button addAlbumToLibrary;
    @FXML
    public TableView<Track> albumTracksTV;
    @FXML
    public FlowPane albumArtistDiscographyFP;
    @FXML
    private Tab artistTab;
    @FXML
    private Tab playlistTab;
    @FXML
    private Tab settingsTab;
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
        mainTabPane.getSelectionModel().select(settingsTab);
    }

    @FXML
    void addToPlaylistBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void albumsBtn_OnAction(ActionEvent event) {
        mainTabPane.getSelectionModel().select(myMusicTab);
        myMusicTabPane.getSelectionModel().select(favAlbumsTab);
    }

    @FXML
    void artistsBtn_OnAction(ActionEvent event) {
        mainTabPane.getSelectionModel().select(myMusicTab);
        myMusicTabPane.getSelectionModel().select(favArtistsTab);
    }

    @FXML
    void cancelSearchBtn_OnAction(ActionEvent event) {
        searchTextField.setText("");
    }

    @FXML
    void exploreBtn_OnAction(ActionEvent event) {
        mainTabPane.getSelectionModel().select(exploreTab);
    }

    @FXML
    public void homeBtn_OnAction(ActionEvent actionEvent) {
        mainTabPane.getSelectionModel().select(homeTab);
    }

    @FXML
    void favouriteTracksBtn_OnAction(ActionEvent event) {
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
        mainTabPane.getSelectionModel().select(myMusicTab);
        mainTabPane.getSelectionModel().select(highlightsTab);
    }

    @FXML
    void playlistsBtn_OnAction(ActionEvent event) {
        mainTabPane.getSelectionModel().select(myMusicTab);
        myMusicTabPane.getSelectionModel().select(myPlaylistsTab);
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
    public void shuffleMusicBtn_OnAction(ActionEvent actionEvent) {
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
        assert homeBtn != null : "fx:id=\"homeBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert exploreBtn != null : "fx:id=\"exploreBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert myMusicBtn != null : "fx:id=\"myMusicBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert favouriteTracksBtn != null : "fx:id=\"favouriteTracksBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert playlistsBtn != null : "fx:id=\"playlistsBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert albumsBtn != null : "fx:id=\"albumsBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert artistsBtn != null : "fx:id=\"artistsBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert searchTextField != null : "fx:id=\"searchTextField\" was not injected: check your FXML file 'index.fxml'.";
        assert cancelSearchBtn != null : "fx:id=\"cancelSearchBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert loginBtn != null : "fx:id=\"loginBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert userMenuBar != null : "fx:id=\"userMenuBar\" was not injected: check your FXML file 'index.fxml'.";
        assert userMenu != null : "fx:id=\"userMenu\" was not injected: check your FXML file 'index.fxml'.";
        assert userAvatar != null : "fx:id=\"userAvatar\" was not injected: check your FXML file 'index.fxml'.";
        assert userAccounttem != null : "fx:id=\"userAccounttem\" was not injected: check your FXML file 'index.fxml'.";
        assert userMenuAvatar != null : "fx:id=\"userMenuAvatar\" was not injected: check your FXML file 'index.fxml'.";
        assert accountSettingsItem != null : "fx:id=\"accountSettingsItem\" was not injected: check your FXML file 'index.fxml'.";
        assert logoutItem != null : "fx:id=\"logoutItem\" was not injected: check your FXML file 'index.fxml'.";
        assert mainTabPane != null : "fx:id=\"mainTabPane\" was not injected: check your FXML file 'index.fxml'.";
        assert searchTab != null : "fx:id=\"searchTab\" was not injected: check your FXML file 'index.fxml'.";
        assert allResultsTab != null : "fx:id=\"allResultsTab\" was not injected: check your FXML file 'index.fxml'.";
        assert fullFoundBox != null : "fx:id=\"fullFoundBox\" was not injected: check your FXML file 'index.fxml'.";
        assert trackResultsTab != null : "fx:id=\"trackResultsTab\" was not injected: check your FXML file 'index.fxml'.";
        assert foundTracksLbl != null : "fx:id=\"foundTracksLbl\" was not injected: check your FXML file 'index.fxml'.";
        assert foundTracksTV != null : "fx:id=\"foundTracksTV\" was not injected: check your FXML file 'index.fxml'.";
        assert albumResultsTab != null : "fx:id=\"albumResultsTab\" was not injected: check your FXML file 'index.fxml'.";
        assert foundAlbumsLbl != null : "fx:id=\"foundAlbumsLbl\" was not injected: check your FXML file 'index.fxml'.";
        assert foundAlbumsFP != null : "fx:id=\"foundAlbumsFP\" was not injected: check your FXML file 'index.fxml'.";
        assert artistResultsTab != null : "fx:id=\"artistResultsTab\" was not injected: check your FXML file 'index.fxml'.";
        assert foundArtistsLbl != null : "fx:id=\"foundArtistsLbl\" was not injected: check your FXML file 'index.fxml'.";
        assert foundArtistsFP != null : "fx:id=\"foundArtistsFP\" was not injected: check your FXML file 'index.fxml'.";
        assert playlistResultsTab != null : "fx:id=\"playlistResultsTab\" was not injected: check your FXML file 'index.fxml'.";
        assert foundPlaylistsLabel != null : "fx:id=\"foundPlaylistsLabel\" was not injected: check your FXML file 'index.fxml'.";
        assert foundPlaylistsFP != null : "fx:id=\"foundPlaylistsFP\" was not injected: check your FXML file 'index.fxml'.";
        assert mixResultsTab != null : "fx:id=\"mixResultsTab\" was not injected: check your FXML file 'index.fxml'.";
        assert foundMixesLbl != null : "fx:id=\"foundMixesLbl\" was not injected: check your FXML file 'index.fxml'.";
        assert foundMixesFP != null : "fx:id=\"foundMixesFP\" was not injected: check your FXML file 'index.fxml'.";
        assert profileResultsTab != null : "fx:id=\"profileResultsTab\" was not injected: check your FXML file 'index.fxml'.";
        assert foundProfilesLbl != null : "fx:id=\"foundProfilesLbl\" was not injected: check your FXML file 'index.fxml'.";
        assert foundProfilesFP != null : "fx:id=\"foundProfilesFP\" was not injected: check your FXML file 'index.fxml'.";
        assert homeTab != null : "fx:id=\"mainTab\" was not injected: check your FXML file 'index.fxml'.";
        assert exploreTab != null : "fx:id=\"exploreTab\" was not injected: check your FXML file 'index.fxml'.";
        assert exploreChannelsFP != null : "fx:id=\"exploreChannelsFP\" was not injected: check your FXML file 'index.fxml'.";
        assert myMusicTab != null : "fx:id=\"myMusicTab\" was not injected: check your FXML file 'index.fxml'.";
        assert myMusicBox != null : "fx:id=\"myMusicBox\" was not injected: check your FXML file 'index.fxml'.";
        assert shuffleMusicBtn != null : "fx:id=\"shuffleMusicBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert myMusicTabPane != null : "fx:id=\"myMusicTabPane\" was not injected: check your FXML file 'index.fxml'.";
        assert recentFP != null : "fx:id=\"recentFP\" was not injected: check your FXML file 'index.fxml'.";
        assert highlightsPlaylistFP != null : "fx:id=\"highlightsPlaylistFP\" was not injected: check your FXML file 'index.fxml'.";
        assert highlightsAlbumFP != null : "fx:id=\"highlightsAlbumFP\" was not injected: check your FXML file 'index.fxml'.";
        assert highlightsArtistFP != null : "fx:id=\"highlightsArtistFP\" was not injected: check your FXML file 'index.fxml'.";
        assert favTracksLbl != null : "fx:id=\"favTracksLbl\" was not injected: check your FXML file 'index.fxml'.";
        assert favTracksTV != null : "fx:id=\"favTracksTV\" was not injected: check your FXML file 'index.fxml'.";
        assert favPlaylistsLabel != null : "fx:id=\"favPlaylistsLabel\" was not injected: check your FXML file 'index.fxml'.";
        assert favPlaylistsFP != null : "fx:id=\"favPlaylistsFP\" was not injected: check your FXML file 'index.fxml'.";
        assert favAlbumsLbl != null : "fx:id=\"favAlbumsLbl\" was not injected: check your FXML file 'index.fxml'.";
        assert favAlbumsFP != null : "fx:id=\"favAlbumsFP\" was not injected: check your FXML file 'index.fxml'.";
        assert favArtistsLbl != null : "fx:id=\"favArtistsLbl\" was not injected: check your FXML file 'index.fxml'.";
        assert favArtistsFP != null : "fx:id=\"favArtistsFP\" was not injected: check your FXML file 'index.fxml'.";
        assert albumTab != null : "fx:id=\"albumTab\" was not injected: check your FXML file 'index.fxml'.";
        assert artistTab != null : "fx:id=\"artistTab\" was not injected: check your FXML file 'index.fxml'.";
        assert playlistTab != null : "fx:id=\"playlistTab\" was not injected: check your FXML file 'index.fxml'.";
        assert settingsTab != null : "fx:id=\"settingsTab\" was not injected: check your FXML file 'index.fxml'.";
        assert playerBox != null : "fx:id=\"playerBox\" was not injected: check your FXML file 'index.fxml'.";
        assert trackInfoBox != null : "fx:id=\"trackInfoBox\" was not injected: check your FXML file 'index.fxml'.";
        assert trackLink != null : "fx:id=\"trackLink\" was not injected: check your FXML file 'index.fxml'.";
        assert artistLink != null : "fx:id=\"artistLink\" was not injected: check your FXML file 'index.fxml'.";
        assert textBtn != null : "fx:id=\"textBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert addToPlaylistBtn != null : "fx:id=\"addToPlaylistBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert likeBtn != null : "fx:id=\"likeBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert trackSlider != null : "fx:id=\"trackSlider\" was not injected: check your FXML file 'index.fxml'.";
        assert durationLabel != null : "fx:id=\"durationLabel\" was not injected: check your FXML file 'index.fxml'.";
        assert repeatBtn != null : "fx:id=\"repeatBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert shuffleBtn != null : "fx:id=\"shuffleBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert soundBtn != null : "fx:id=\"soundBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert soundImg != null : "fx:id=\"soundImg\" was not injected: check your FXML file 'index.fxml'.";
        assert audioSettingsBtn != null : "fx:id=\"audioSettingsBtn\" was not injected: check your FXML file 'index.fxml'.";
        assert playingImg != null : "fx:id=\"playingImg\" was not injected: check your FXML file 'index.fxml'.";

        foundTracksTV.prefWidthProperty().bind(Bindings.add(0, foundTracksScroll.widthProperty()));
        foundTracksTV.prefHeightProperty().bind(Bindings.add(0, foundTracksScroll.heightProperty()));

        foundAlbumsFP.prefWidthProperty().bind(Bindings.add(0, foundAlbumsScroll.widthProperty()));
        foundAlbumsFP.prefHeightProperty().bind(Bindings.add(0, foundAlbumsScroll.heightProperty()));

        foundArtistsFP.prefWidthProperty().bind(Bindings.add(-5, foundArtistsScroll.widthProperty()));
        foundArtistsFP.prefHeightProperty().bind(Bindings.add(-5, foundArtistsScroll.heightProperty()));

        foundPlaylistsFP.prefWidthProperty().bind(Bindings.add(-5, foundPlaylistsScroll.widthProperty()));
        foundPlaylistsFP.prefHeightProperty().bind(Bindings.add(-5, foundPlaylistsScroll.heightProperty()));

        foundMixesFP.prefWidthProperty().bind(Bindings.add(-5, foundMixesScroll.widthProperty()));
        foundMixesFP.prefHeightProperty().bind(Bindings.add(-5, foundMixesScroll.heightProperty()));

        foundProfilesFP.prefWidthProperty().bind(Bindings.add(-5, foundProfilesScroll.widthProperty()));
        foundProfilesFP.prefHeightProperty().bind(Bindings.add(-5, foundProfilesScroll.heightProperty()));

        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                cancelSearchBtn.setVisible(newValue != null && newValue.length() > 0);
            }
        });

        setupFoundTracksTable();

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
