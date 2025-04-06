package controllers;

import api.objects.utils.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SettingsPageController {
    @FXML
    private ImageView userImg;
    @FXML
    private Label mySubscriptionPlanLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Button emailBtn;
    @FXML
    private Button passwordBtn;
    @FXML
    private TextField passwordTextField;
    @FXML
    private RadioButton maleRadioBtn;
    @FXML
    private ToggleGroup genderToggleGroup;
    @FXML
    private RadioButton femaleRadioBtn;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField lastnameTextField;
    @FXML
    private TextField firstnameTextField;
    @FXML
    private TextField birthdayTextField;

    @FXML
    void emailBtn_OnAction(ActionEvent actionEvent) {
    }

    @FXML
    void passwordBtn_OnAction(ActionEvent actionEvent) {
    }

    public void setUserDetails(User loggedInUser) {
        userImg.setImage(new Image(loggedInUser.getPicture_medium().toString(), true));
        emailTextField.setText(loggedInUser.getEmail());
        passwordTextField.setText("****");
        maleRadioBtn.setSelected(loggedInUser.getGender().equals("M"));
        femaleRadioBtn.setSelected(loggedInUser.getGender().equals("F"));
        usernameTextField.setText(loggedInUser.getName());
        firstnameTextField.setText(loggedInUser.getFirstname());
        lastnameTextField.setText(loggedInUser.getLastname());
        birthdayTextField.setText(loggedInUser.getBirthday().toInstant().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
