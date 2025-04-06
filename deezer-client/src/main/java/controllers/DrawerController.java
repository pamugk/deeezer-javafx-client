package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import navigation.Pages;

import java.util.function.Consumer;

public class DrawerController {
    @FXML
    private Button exploreBtn;
    @FXML
    private Button myMusicBtn;
    @FXML
    private Button tracksBtn;
    @FXML
    private Button playlistsBtn;
    @FXML
    private Button albumsBtn;
    @FXML
    private Button artistsBtn;
    
    private Consumer<Pages> navigator = page -> {};

    public void onLogout(boolean logout) {
        exploreBtn.setDisable(logout);
        myMusicBtn.setDisable(logout);
        tracksBtn.setDisable(logout);
        playlistsBtn.setDisable(logout);
        albumsBtn.setDisable(logout);
        artistsBtn.setDisable(logout);
    }

    public void setNavigator(Consumer<Pages> navigator) {
        this.navigator = navigator;
    }

    @FXML
    private void homeBtn_OnAction(ActionEvent event) {
        navigator.accept(Pages.HOME);
    }

    @FXML
    private void exploreBtn_OnAction(ActionEvent event) {
        navigator.accept(Pages.EXPLORE);
    }

    @FXML
    private void myMusicBtn_OnAction(ActionEvent event) {
        navigator.accept(Pages.USER);
    }

    @FXML
    private void favouriteTracksBtn_OnAction(ActionEvent event) {
        navigator.accept(Pages.TRACKS);
    }

    @FXML
    private void playlistsBtn_OnAction(ActionEvent event) {
        navigator.accept(Pages.PLAYLISTS);
    }

    @FXML
    private void albumsBtn_OnAction(ActionEvent event) {
        navigator.accept(Pages.ALBUMS);
    }

    @FXML
    private void artistsBtn_OnAction(ActionEvent event) {
        navigator.accept(Pages.ARTISTS);
    }
}
