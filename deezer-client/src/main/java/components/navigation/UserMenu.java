package components.navigation;

import api.Deezer;
import api.events.handlers.DeezerListener;
import api.objects.utils.User;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static api.LoginStatus.NOT_AUTHORIZED;

public class UserMenu extends HBox {
    private Deezer deezerClient;

    public UserMenu() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userMenu.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private Button loginBtn;
    @FXML
    private MenuBar userMenuBar;
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
    private void accountSettingsItem_OnAction(ActionEvent event) {
        navigator.getValue().accept(Pages.SETTINGS);
    }

    @FXML
    private void loginBtn_OnAction(ActionEvent event) {
        loginBtn.setDisable(true);
        deezerClient.login();
    }

    @FXML
    private void logoutItem_OnAction(ActionEvent event) { deezerClient.logout(); }

    @FXML
    private void userAccountItem_OnAction(ActionEvent event) {
        navigator.getValue().accept(Pages.USER);
    }

    public void setupDeezer(Deezer deezerClient) {
        this.deezerClient = deezerClient;
        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(event -> {
            loginBtn.setDisable(false);
            loginBtn.setVisible(!event.isLoggedIn());
            userMenuBar.setVisible(event.isLoggedIn());
            if (!event.isLoggedIn())
                return;
            User currentUser = deezerClient.getLoggedInUser();
            Image avatar = new Image(currentUser.getPicture_small().toString(), true);
            userAvatar.setImage(avatar);
            userAccounttem.setText(currentUser.getName());
            userMenuAvatar.setImage(avatar);
        }));
        if (deezerClient.getLoginStatus() != NOT_AUTHORIZED)
            loginBtn.getOnAction().handle(new ActionEvent());
    }

    public final ObjectProperty<Consumer<Pages>> navigatorProperty() { return navigator; }
    public final void setNavigator(Consumer<Pages> value) { navigatorProperty().set(value); }
    public final Consumer<Pages> getNavigator() { return navigatorProperty().get(); }
    private ObjectProperty<Consumer<Pages>> navigator = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return UserMenu.this;
        }

        @Override
        public String getName() {
            return "navigator";
        }
    };
}
