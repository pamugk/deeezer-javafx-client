package controllers;

import api.Deezer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController {
    private Deezer deezer;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    void initialize() {
        try {
            deezer = new Deezer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            deezer.login();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
