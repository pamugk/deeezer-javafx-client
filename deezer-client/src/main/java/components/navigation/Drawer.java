package components.navigation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class Drawer extends VBox {
    public Drawer() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("drawer.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void onLogout(boolean logout) {
        exploreBtn.setDisable(logout);
        myMusicBtn.setDisable(logout);
        tracksBtn.setDisable(logout);
        playlistsBtn.setDisable(logout);
        albumsBtn.setDisable(logout);
        artistsBtn.setDisable(logout);
    }

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

    @FXML
    private void homeBtn_OnAction(ActionEvent event) {
        getNavigator().accept(Pages.HOME);
    }

    @FXML
    private void exploreBtn_OnAction(ActionEvent event) {
        getNavigator().accept(Pages.EXPLORE);
    }

    @FXML
    private void myMusicBtn_OnAction(ActionEvent event) {
        getNavigator().accept(Pages.USER);
    }

    @FXML
    private void favouriteTracksBtn_OnAction(ActionEvent event) {
        getNavigator().accept(Pages.TRACKS);
    }

    @FXML
    private void playlistsBtn_OnAction(ActionEvent event) {
        getNavigator().accept(Pages.PLAYLISTS);
    }

    @FXML
    private void albumsBtn_OnAction(ActionEvent event) {
        getNavigator().accept(Pages.ALBUMS);
    }

    @FXML
    private void artistsBtn_OnAction(ActionEvent event) {
        getNavigator().accept(Pages.ARTISTS);
    }

    public final ObjectProperty<Consumer<Pages>> navigatorProperty() { return navigator; }
    public final void setNavigator(Consumer<Pages> value) { navigatorProperty().set(value); }
    public final Consumer<Pages> getNavigator() { return navigatorProperty().get(); }
    private final ObjectProperty<Consumer<Pages>> navigator = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return Drawer.this;
        }

        @Override
        public String getName() {
            return "navigator";
        }
    };
}
