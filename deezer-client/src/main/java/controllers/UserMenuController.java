package controllers;

import api.Deezer;
import api.events.handlers.DeezerListener;
import api.objects.utils.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static api.LoginStatus.NOT_AUTHORIZED;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class UserMenuController {
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

    public void setupDeezer(Deezer deezerClient) {
        deezerClient.getAuthenticationEventHandler().addListener(new DeezerListener<>(event -> {
            loginBtn.setDisable(false);
            loginBtn.setPrefWidth(event.isLoggedIn() ? 0 : USE_COMPUTED_SIZE);
            userMenuBar.setPrefWidth(event.isLoggedIn() ? USE_COMPUTED_SIZE : 0);
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

    @FXML
    private void accountSettingsItem_OnAction(ActionEvent event) {
        //navigator.getValue().accept(Pages.SETTINGS);
    }

    @FXML
    private void loginBtn_OnAction(ActionEvent event) {
        loginBtn.setDisable(true);
        //deezerClient.login();
    }

    @FXML
    private void logoutItem_OnAction(ActionEvent event) {
        //deezerClient.logout();
    }

    @FXML
    private void userAccountItem_OnAction(ActionEvent event) {
        //navigator.getValue().accept(Pages.USER);
    }
}
