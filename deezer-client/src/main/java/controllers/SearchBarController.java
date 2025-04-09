package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class SearchBarController implements Initializable {
    @FXML
    private TextField searchTextField;
    @FXML
    private Button cancelSearchBtn;

    private Consumer<String> searchEngine = searchQuery -> {};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) ->
                cancelSearchBtn.setVisible(newValue != null && !newValue.isEmpty()));
    }

    @FXML
    private void cancelSearchBtn_OnAction(ActionEvent event) {
        searchTextField.setText("");
    }

    @FXML
    private void searchTextField_OnKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && searchTextField.getText() != null) {
            if (searchTextField.getText().trim().isEmpty()) {
                searchTextField.setText(null);
            }
            else {
                searchEngine.accept(searchTextField.getText());
            }
        }
    }

    public void setSearchEngine(Consumer<String> searchEngine) {
        this.searchEngine = searchEngine;
    }
}
