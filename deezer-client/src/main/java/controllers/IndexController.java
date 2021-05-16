package controllers;

import java.io.IOException;
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
import components.MusicPlayer;
import components.navigation.Drawer;
import components.navigation.Pages;
import components.navigation.SearchBar;
import components.navigation.UserMenu;
import components.views.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class IndexController {
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
        drawer.onLogout(logout);
        musicPlayer.setDisable(logout);
        if (logout)
            navigate(Pages.HOME);
    }

    private void onLoginResponse (AuthenticationEvent event) {
        Platform.runLater(() -> changeInterfaceState(!event.isLoggedIn()));
    }

    private void showUser(User user, boolean loggedIn, UserView.Destinations destination){
        userView.setUser(user, loggedIn, deezerClient);
        mainTabPane.getSelectionModel().select(userTab);
        userView.navigateTo(destination);
    }

    @FXML
    private SearchBar searchBar;
    @FXML
    private UserMenu userMenu;
    @FXML
    private Drawer drawer;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab searchTab;
    @FXML
    private SearchView searchView;
    @FXML
    private Tab homeTab;
    @FXML
    private HomeView homeView;
    @FXML
    private Tab exploreTab;
    @FXML
    private ExploreView exploreView;
    @FXML
    private Tab userTab;
    @FXML
    private UserView userView;
    @FXML
    private Tab albumTab;
    @FXML
    private AlbumView albumView;
    @FXML
    private Tab artistTab;
    @FXML
    private ArtistView artistView;
    @FXML
    private Tab playlistTab;
    @FXML
    private PlaylistView playlistView;
    @FXML
    private Tab radioTab;
    @FXML
    private Tab settingsTab;
    @FXML
    private SettingsView settingsView;
    @FXML
    private MusicPlayer musicPlayer;

    @FXML
    private void createPlaylist() {
        try {
            Playlist playlist = PlaylistDialog.showAndWait(null);
            if (playlist != null) {
                deezerClient.createPlaylist(playlist);
                navigate(Pages.PLAYLISTS);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editPlaylistProperty(Playlist playlist) {
        try {
            Playlist updatedPlaylist = PlaylistDialog.showAndWait(playlist);
            if (updatedPlaylist == null) {
                if (deezerClient.removePlaylist(playlist))
                    navigate(Pages.HOME);
            }
            else
                if (deezerClient.updatePlaylist(updatedPlaylist))
                    redirectToPlaylist(updatedPlaylist.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        drawer.setNavigator(this::navigate);
        userMenu.setNavigator(this::navigate);
        searchBar.setSearchEngine(this::search);
        try {
            deezerClient = new Deezer(
                    new Configuration("/callback", "API_KEY", "API_SECRET")
            );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(this::onLoginResponse));
        homeView.setupDeezer(deezerClient);
        userMenu.setupDeezer(deezerClient);
    }

    @FXML
    private void navigate(Pages page){
        switch (page){
            case HOME:
                mainTabPane.getSelectionModel().select(homeTab);
                break;
            case EXPLORE:
                mainTabPane.getSelectionModel().select(exploreTab);
                break;
            case USER:
                showUser(deezerClient.getLoggedInUser(), true, UserView.Destinations.HIGHLIGHTS);
                break;
            case TRACKS:
                showUser(deezerClient.getLoggedInUser(), true, UserView.Destinations.TRACKS);
                break;
            case PLAYLISTS:
                showUser(deezerClient.getLoggedInUser(), true, UserView.Destinations.PLAYLISTS);
                break;
            case ALBUMS:
                showUser(deezerClient.getLoggedInUser(), true, UserView.Destinations.ALBUMS);
                break;
            case ARTISTS:
                showUser(deezerClient.getLoggedInUser(), true, UserView.Destinations.ARTISTS);
                break;
            case SETTINGS: {
                mainTabPane.getSelectionModel().select(settingsTab);
                break;
            }
        }
    }

    @FXML
    private void redirectToArtistProperty(Long artistId) {
        Artist artist = deezerClient.getArtist(artistId);
        artistView.setArtist(artist, deezerClient);
        mainTabPane.getSelectionModel().select(artistTab);
    }

    @FXML
    private void redirectToAlbum(Long albumId) {
        Album album = deezerClient.getAlbum(albumId);
        albumView.setAlbum(album, deezerClient);
        mainTabPane.getSelectionModel().select(albumTab);
    }

    @FXML
    private void redirectToPlaylist(Long playlistId) {
        Playlist playlist = deezerClient.getPlaylist(playlistId);
        playlistView.setPlaylist(playlist, deezerClient);
        mainTabPane.getSelectionModel().select(playlistTab);
    }

    @FXML
    private void redirectToUserProperty(Long userId) {
        showUser(deezerClient.getUser(userId), userId == deezerClient.getLoggedInUser().getId(),
                UserView.Destinations.HIGHLIGHTS);
    }

    @FXML
    private void removeTrackFromFavourite() {
        if (deezerClient.removeTrackFromFavourites(musicPlayer.getSelectedTrack())){
            new Alert(Alert.AlertType.INFORMATION, "Удаление успешно");
            navigate(Pages.PLAYLISTS);
        }
        else new Alert(Alert.AlertType.INFORMATION, "Удаление отклонено сервером");
    }

    @FXML
    private void removeTrackFromPlaylist(Playlist playlist) {
        if (deezerClient.removeTracksFromPlaylist(playlist, Collections.singletonList(musicPlayer.getSelectedTrack()))){
            new Alert(Alert.AlertType.INFORMATION, "Удаление успешно");
        }
        else new Alert(Alert.AlertType.INFORMATION, "Удаление отклонено сервером");
    }

    @FXML
    private void search(String query) {
        FullSearchSet searchSet = deezerClient.search(query, null);
        searchView.setSearchResults(searchSet);
        mainTabPane.getSelectionModel().select(searchTab);
    }
}
