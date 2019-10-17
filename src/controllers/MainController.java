package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import api.Deezer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

    private Deezer deezerClient;

    //<editor-fold defaultstate="collapsed" desc="Вспомогательные методы">
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Элементы управления">
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button homeBtn;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button cancelSearchBtn;
    @FXML
    private MenuBar userMenuBar;
    @FXML
    private Menu userMenu;
    @FXML
    private ImageView userAvatar;
    @FXML
    private MenuItem userAccounttem;
    @FXML
    private MenuItem accountSettingsItem;
    @FXML
    private MenuItem logoutItem;
    @FXML
    private VBox mainPane;
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
    //<editor-fold defaultstate="collapsed" desc="Обработчики событий">
    @FXML
    void accountSettingsItem_OnAction(ActionEvent event) {

    }

    @FXML
    void addToPlaylistBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void cancelSearchBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void likeBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void logoutItem_OnAction(ActionEvent event) {

    }

    @FXML
    void repeatBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void shuffleBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void textBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void userAccounttem_OnAction(ActionEvent event) {

    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Инициализация">
    @FXML
    void initialize() {
        assert homeBtn != null : "fx:id=\"homeBtn\" was not injected: check your FXML file 'main.fxml'.";
        assert searchTextField != null : "fx:id=\"searchTextField\" was not injected: check your FXML file 'main.fxml'.";
        assert cancelSearchBtn != null : "fx:id=\"cancelSearchBtn\" was not injected: check your FXML file 'main.fxml'.";
        assert userMenuBar != null : "fx:id=\"userMenuBar\" was not injected: check your FXML file 'main.fxml'.";
        assert userMenu != null : "fx:id=\"userMenu\" was not injected: check your FXML file 'main.fxml'.";
        assert userAvatar != null : "fx:id=\"userAvatar\" was not injected: check your FXML file 'main.fxml'.";
        assert userAccounttem != null : "fx:id=\"userAccounttem\" was not injected: check your FXML file 'main.fxml'.";
        assert accountSettingsItem != null : "fx:id=\"accountSettingsItem\" was not injected: check your FXML file 'main.fxml'.";
        assert logoutItem != null : "fx:id=\"logoutItem\" was not injected: check your FXML file 'main.fxml'.";
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'main.fxml'.";
        assert trackInfoBox != null : "fx:id=\"trackInfoBox\" was not injected: check your FXML file 'main.fxml'.";
        assert trackLink != null : "fx:id=\"trackLink\" was not injected: check your FXML file 'main.fxml'.";
        assert artistLink != null : "fx:id=\"artistLink\" was not injected: check your FXML file 'main.fxml'.";
        assert textBtn != null : "fx:id=\"textBtn\" was not injected: check your FXML file 'main.fxml'.";
        assert addToPlaylistBtn != null : "fx:id=\"addToPlaylistBtn\" was not injected: check your FXML file 'main.fxml'.";
        assert likeBtn != null : "fx:id=\"likeBtn\" was not injected: check your FXML file 'main.fxml'.";
        assert durationLabel != null : "fx:id=\"durationLabel\" was not injected: check your FXML file 'main.fxml'.";
        assert repeatBtn != null : "fx:id=\"repeatBtn\" was not injected: check your FXML file 'main.fxml'.";
        assert shuffleBtn != null : "fx:id=\"shuffleBtn\" was not injected: check your FXML file 'main.fxml'.";
        assert soundBtn != null : "fx:id=\"soundBtn\" was not injected: check your FXML file 'main.fxml'.";
        assert soundImg != null : "fx:id=\"soundImg\" was not injected: check your FXML file 'main.fxml'.";
        assert audioSettingsBtn != null : "fx:id=\"audioSettingsBtn\" was not injected: check your FXML file 'main.fxml'.";
        assert playingImg != null : "fx:id=\"playingImg\" was not injected: check your FXML file 'main.fxml'.";

        try {
            deezerClient = new Deezer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
}
