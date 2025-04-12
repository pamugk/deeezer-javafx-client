package controllers;

import api.events.authentication.AuthenticationEvent;
import api.objects.utils.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import navigation.Pages;

import java.util.function.Consumer;

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

    private Runnable loginAction = () -> {};
    private Runnable logoutAction = () -> {};
    private Consumer<Pages> navigator = page -> {};

    public void processAuthenticationEvent(AuthenticationEvent event, User currentUser) {
        loginBtn.setDisable(false);
        loginBtn.setPrefWidth(event.isLoggedIn() ? 0 : USE_COMPUTED_SIZE);
        userMenuBar.setPrefWidth(event.isLoggedIn() ? USE_COMPUTED_SIZE : 0);
        if (!event.isLoggedIn()) {
            return;
        }

        Image avatar = new Image(currentUser.pictureSmall().toString(), true);
        userAvatar.setImage(avatar);
        userAccounttem.setText(currentUser.name());
        userMenuAvatar.setImage(avatar);
    }

    public void setLoginAction(Runnable loginAction) {
        this.loginAction = loginAction;
    }

    public void setLogoutAction(Runnable logoutAction) {
        this.logoutAction = logoutAction;
    }

    public void setNavigator() {
        this.navigator = navigator;
    }

    @FXML
    private void accountSettingsItem_OnAction(ActionEvent event) {
        navigator.accept(Pages.SETTINGS);
    }

    @FXML
    private void loginBtn_OnAction(ActionEvent event) {
        loginBtn.setDisable(true);
        loginAction.run();
    }

    @FXML
    private void logoutItem_OnAction(ActionEvent event) {
        logoutAction.run();
    }

    @FXML
    private void userAccountItem_OnAction(ActionEvent event) {
        navigator.accept(Pages.USER);
    }
}
