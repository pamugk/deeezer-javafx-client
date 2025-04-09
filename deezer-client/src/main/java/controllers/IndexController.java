package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import api.Configuration;
import api.Deezer;
import api.events.authentication.AuthenticationEvent;
import api.events.handlers.DeezerListener;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.utils.User;
import api.objects.utils.search.FullSearchSet;
import navigation.Pages;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class IndexController implements Initializable {

    @FXML
    private SearchBarController searchBarController;
    @FXML
    private UserMenuController userMenuController;
    @FXML
    private DrawerController drawerController;

    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab searchTab;
    @FXML
    private SearchPageController searchPageController;
    @FXML
    private Tab homeTab;
    @FXML
    private HomePageController homePageController;
    @FXML
    private Tab exploreTab;
    @FXML
    private Tab userTab;
    @FXML
    private UserPageController userPageController;
    @FXML
    private Tab albumTab;
    @FXML
    private AlbumPageController albumPageController;
    @FXML
    private Tab artistTab;
    @FXML
    private ArtistPageController artistPageController;
    @FXML
    private Tab playlistTab;
    @FXML
    private PlaylistPageController playlistPageController;
    @FXML
    private Tab settingsTab;

    @FXML
    private MusicPlayerController musicPlayerController;

    private Deezer deezerClient;
    private Alert standbyAlert;

    public static void show(Stage primaryStage) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader loader = new FXMLLoader(IndexController.class.getResource("/fxml/index.fxml"), bundle);
        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.getIcons().add(
                new Image(Objects.requireNonNull(IndexController.class.getResourceAsStream("/img/deezer-icon.jpg"))));
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

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            deezerClient = new Deezer(
                    new Configuration("/callback", "API_KEY", "API_SECRET")
            );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(this::onLoginResponse));
        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>((event) -> {
            userMenuController.processAuthenticationEvent(event, this.deezerClient.getLoggedInUser());
        }));

        searchBarController.setSearchEngine(this::search);
        userMenuController.setLoginAction(deezerClient::login);
        userMenuController.setLogoutAction(deezerClient::logout);
        drawerController.setNavigator(this::navigate);

        albumPageController.setAlbumRedirectioner((album) -> redirectToAlbum(album.id()));
        albumPageController.setArtistRedirectioner((artist) -> redirectToArtist(artist.id()));
        artistPageController.setAlbumRedirectioner((album) -> redirectToAlbum(album.id()));
        artistPageController.setArtistRedirectioner((artist) -> redirectToArtist(artist.id()));
        artistPageController.setPlaylistRedirectioner((playlist) -> redirectToPlaylist(playlist.id()));
        artistPageController.setUserRedirectioner((user) -> redirectToUser(user.id()));
        homePageController.setupDeezer(deezerClient);
        homePageController.setAlbumRedirectioner((album) -> redirectToAlbum(album.id()));
        homePageController.setArtistRedirectioner((artist) -> redirectToArtist(artist.id()));
        homePageController.setPlaylistRedirectioner((playlist) -> redirectToPlaylist(playlist.id()));
        playlistPageController.setUserRedirectioner((user) -> redirectToUser(user.id()));
        searchPageController.setAlbumRedirectioner((album) -> redirectToAlbum(album.id()));
        searchPageController.setArtistRedirectioner((artist) -> redirectToArtist(artist.id()));
        searchPageController.setPlaylistRedirectioner((playlist) -> redirectToPlaylist(playlist.id()));
        searchPageController.setUserRedirectioner((user) -> redirectToUser(user.id()));
        userPageController.setAlbumRedirectioner((album) -> redirectToAlbum(album.id()));
        userPageController.setArtistRedirectioner((artist) -> redirectToArtist(artist.id()));
        userPageController.setPlaylistRedirectioner((playlist) -> redirectToPlaylist(playlist.id()));
    }

    private void changeInterfaceState(boolean logout) {
        drawerController.onLogout(logout);
        if (logout) {
            navigate(Pages.HOME);
        }
    }

    private void navigate(Pages page) {
        switch (page) {
            case HOME:
                mainTabPane.getSelectionModel().select(homeTab);
                break;
            case EXPLORE:
                mainTabPane.getSelectionModel().select(exploreTab);
                break;
            case USER:
                showUser(deezerClient.getLoggedInUser(), true, UserPageController.Destinations.HIGHLIGHTS);
                break;
            case TRACKS:
                showUser(deezerClient.getLoggedInUser(), true, UserPageController.Destinations.TRACKS);
                break;
            case PLAYLISTS:
                showUser(deezerClient.getLoggedInUser(), true, UserPageController.Destinations.PLAYLISTS);
                break;
            case ALBUMS:
                showUser(deezerClient.getLoggedInUser(), true, UserPageController.Destinations.ALBUMS);
                break;
            case ARTISTS:
                showUser(deezerClient.getLoggedInUser(), true, UserPageController.Destinations.ARTISTS);
                break;
            case SETTINGS: {
                mainTabPane.getSelectionModel().select(settingsTab);
                break;
            }
        }
    }

    private void onLoginResponse (AuthenticationEvent event) {
        Platform.runLater(() -> changeInterfaceState(!event.isLoggedIn()));
    }

    private void redirectToArtist(long artistId) {
        Artist artist = deezerClient.getArtist(artistId);
        artistPageController.fillData(artist, deezerClient);
        mainTabPane.getSelectionModel().select(artistTab);
    }

    private void redirectToAlbum(long albumId) {
        Album album = deezerClient.getAlbum(albumId);
        albumPageController.fillData(album, deezerClient);
        mainTabPane.getSelectionModel().select(albumTab);
    }

    private void redirectToPlaylist(long playlistId) {
        Playlist playlist = deezerClient.getPlaylist(playlistId);
        playlistPageController.fillData(playlist, deezerClient);
        mainTabPane.getSelectionModel().select(playlistTab);
    }

    private void redirectToUser(long userId) {
        showUser(deezerClient.getUser(userId), userId == deezerClient.getLoggedInUser().id(),
                UserPageController.Destinations.HIGHLIGHTS);
    }

    private void search(String query) {
        FullSearchSet searchSet = deezerClient.search(query, null);
        searchPageController.fillData(searchSet);
        mainTabPane.getSelectionModel().select(searchTab);
    }

    private void showUser(User user, boolean loggedIn, UserPageController.Destinations destination) {
        userPageController.fillData(user, loggedIn, deezerClient);
        mainTabPane.getSelectionModel().select(userTab);
        userPageController.navigateTo(destination);
    }
}
