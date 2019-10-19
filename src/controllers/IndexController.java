package controllers;

import java.io.IOException;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    private Tab allResultsTab;
    @FXML
    private VBox fullFoundBox;
    @FXML
    private Tab trackResultsTab;
    @FXML
    private Label foundTracksLbl;
    @FXML
    private TableView<?> foundTracksTV;
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
    private Tab mainTab;
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
    private FlowPane recentFP;
    @FXML
    private FlowPane highlightsPlaylistFP;
    @FXML
    private FlowPane highlightsAlbumFP;
    @FXML
    private FlowPane highlightsArtistFP;
    @FXML
    private Label favTracksLbl;
    @FXML
    private TableView<?> favTracksTV;
    @FXML
    private Label favPlaylistsLabel;
    @FXML
    private FlowPane favPlaylistsFP;
    @FXML
    private Label favAlbumsLbl;
    @FXML
    private FlowPane favAlbumsFP;
    @FXML
    private Label favArtistsLbl;
    @FXML
    private FlowPane favArtistsFP;
    @FXML
    private Tab albumTab;
    @FXML
    private Tab artistTab;
    @FXML
    private Tab playlistTab;
    @FXML
    private Tab settingsTab;
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
    void albumsBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void artistsBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void cancelSearchBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void exploreBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void favouriteTracksBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void likeBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void loginBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void logoutItem_OnAction(ActionEvent event) {

    }

    @FXML
    void myMusicBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void playlistsBtn_OnAction(ActionEvent event) {

    }

    @FXML
    void repeatBtn_OnAction(ActionEvent event) {

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
    void userAccounttem_OnAction(ActionEvent event) {

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
        assert mainTab != null : "fx:id=\"mainTab\" was not injected: check your FXML file 'index.fxml'.";
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
