package components.navigation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class SearchBar extends HBox {
    public SearchBar() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchBar.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private TextField searchTextField;
    @FXML
    private Button cancelSearchBtn;

    @FXML
    void cancelSearchBtn_OnAction(ActionEvent event) {
        searchTextField.setText("");
    }

    @FXML
    void searchTextField_OnKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && searchTextField.getText() != null) {
            if (searchTextField.getText().trim().isEmpty())
                searchTextField.setText(null);
            else getSearchEngine().accept(searchTextField.getText());
        }
    }

    @FXML
    void initialize(){
        searchTextField.textProperty().addListener((observable, oldValue, newValue) ->
                cancelSearchBtn.setVisible(newValue != null && !newValue.isEmpty()));
    }

    public final ObjectProperty<Consumer<String>> searchEngineProperty() { return searchEngine; }
    public final void setSearchEngine(Consumer<String> value) { searchEngineProperty().set(value); }
    public final Consumer<String> getSearchEngine() { return searchEngineProperty().get(); }
    private final ObjectProperty<Consumer<String>> searchEngine = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return SearchBar.this;
        }

        @Override
        public String getName() {
            return "searchEngine";
        }
    };
}
