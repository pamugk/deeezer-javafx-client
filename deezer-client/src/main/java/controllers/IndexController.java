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
import javafx.scene.layout.HBox;
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
    private Deezer deezerClient;
    private Alert standbyAlert;

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

    private void changeInterfaceState(boolean logout) {
        drawerController.onLogout(logout);
        if (logout) {
            navigate(Pages.HOME);
        }
    }

    private void onLoginResponse (AuthenticationEvent event) {
        Platform.runLater(() -> changeInterfaceState(!event.isLoggedIn()));
    }

    private void showUser(User user, boolean loggedIn, UserPageController.Destinations destination){
        userPageController.setUser(user, loggedIn, deezerClient);
        mainTabPane.getSelectionModel().select(userTab);
        userPageController.navigateTo(destination);
    }

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
    private HBox musicPlayerContainer;
    @FXML
    private MusicPlayerController musicPlayerController;

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

        //searchBarController.setSearchEngine(this::search);
        //drawerController.setNavigator(this::navigate);
        //homePageController.setupDeezer(deezerClient);
    }

    private void navigate(Pages page){
        switch (page){
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

    private void redirectToArtistProperty(Long artistId) {
        Artist artist = deezerClient.getArtist(artistId);
        artistPageController.setArtist(artist, deezerClient);
        mainTabPane.getSelectionModel().select(artistTab);
    }

    private void redirectToAlbum(Long albumId) {
        Album album = deezerClient.getAlbum(albumId);
        albumPageController.setAlbum(album, deezerClient);
        mainTabPane.getSelectionModel().select(albumTab);
    }

    private void redirectToPlaylist(Long playlistId) {
        Playlist playlist = deezerClient.getPlaylist(playlistId);
        playlistPageController.setPlaylist(playlist, deezerClient);
        mainTabPane.getSelectionModel().select(playlistTab);
    }

    private void redirectToUserProperty(Long userId) {
        showUser(deezerClient.getUser(userId), userId == deezerClient.getLoggedInUser().getId(),
                UserPageController.Destinations.HIGHLIGHTS);
    }

    private void removeTrackFromFavourite() {
        if (deezerClient.removeTrackFromFavourites(musicPlayerController.getSelectedTrack())){
            new Alert(Alert.AlertType.INFORMATION, "Удаление успешно");
            navigate(Pages.PLAYLISTS);
        }
        else new Alert(Alert.AlertType.INFORMATION, "Удаление отклонено сервером");
    }

    private void removeTrackFromPlaylist(Playlist playlist) {
        if (deezerClient.removeTracksFromPlaylist(playlist, Collections.singletonList(musicPlayerController.getSelectedTrack()))){
            new Alert(Alert.AlertType.INFORMATION, "Удаление успешно");
        }
        else new Alert(Alert.AlertType.INFORMATION, "Удаление отклонено сервером");
    }

    private void search(String query) {
        FullSearchSet searchSet = deezerClient.search(query, null);
        searchPageController.setSearchResults(searchSet);
        mainTabPane.getSelectionModel().select(searchTab);
    }
}
