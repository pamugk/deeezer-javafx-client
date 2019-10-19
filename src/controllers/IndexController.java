package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import api.Deezer;
import api.events.authentication.AuthenticationEvent;
import api.events.base.DeezerListener;
import api.objects.utils.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class IndexController {
    private Deezer deezerClient;

    //<editor-fold defaultstate="collapsed" desc="Auxiliary methods">
    private void onLoginResponse (AuthenticationEvent event) {
        if (!event.isAuthenticationSuccessful())
            return;

        loginBtn.setPrefWidth(0);

        userMenuBar.setPrefWidth(Control.USE_COMPUTED_SIZE);

        User currentUser = deezerClient.getLoggedInUser();

        Image avatar = new Image(currentUser.getPicture_small().toString());
        userAvatar.setImage(avatar);
        userAccounttem.setText(currentUser.getName());
        userMenuAvatar.setImage(avatar);
    }

    private void login() {
        deezerClient.login();
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
    private Tab mainTab;
    @FXML
    private Tab searchPane;
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
    void loginBtn_OnAction(ActionEvent event) {
        login();
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
    //<editor-fold defaultstate="collapsed" desc="Controller initialisation">
    @FXML
    void initialize() {
        assert homeBtn != null : "fx:id=\"homeBtn\" was not injected: check your FXML file 'index.fxml'.";
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
        assert mainTab != null : "fx:id=\"mainTab\" was not injected: check your FXML file 'index.fxml'.";
        assert searchPane != null : "fx:id=\"searchPane\" was not injected: check your FXML file 'index.fxml'.";
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
