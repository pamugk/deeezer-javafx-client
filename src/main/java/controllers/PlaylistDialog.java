package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import api.objects.playables.Playlist;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PlaylistDialog {
    private Playlist playlist;

    private void close() {
        ((Stage) saveBtn.getScene().getWindow()).close();
    }

    private void setup(Playlist playlist) {
        if (playlist != null) {
            this.playlist = playlist;
            playlistActionLbl.setText(resources.getString("editPlaylist"));
            playlistNameTextField.setText(playlist.getTitle());
            privatePlaylistChckBox.setSelected(!playlist.is_public());
            publicPlaylistChckBox.setSelected(playlist.isCollaborative());
            playlistDescriptionTextField.setText(playlist.getDescription());
            removeBtn.setText(resources.getString("delete"));
            saveBtn.setText(resources.getString("change"));
        }
        else {
            this.playlist = new Playlist();
            playlistActionLbl.setText(resources.getString("createPlaylist"));
            removeBtn.setText(resources.getString("cancel"));
            saveBtn.setText(resources.getString("create"));
        }
    }

    static Playlist showAndWait(Playlist playlist) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/playlistdialoglocalisation");
        FXMLLoader loader = new FXMLLoader(PlaylistDialog.class.getResource("/fxml/playlistdialog.fxml"), bundle);
        Parent dialogRoot = loader.load();
        PlaylistDialog dialog = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setResizable(false);
        dialogStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                dialogStage.setMaximized(false);
        });
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(dialogRoot));
        dialog.setup(playlist);
        dialogStage.showAndWait();
        return dialog.playlist;
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label playlistActionLbl;

    @FXML
    private ImageView playlistPicture;

    @FXML
    private TextField playlistNameTextField;

    @FXML
    private CheckBox privatePlaylistChckBox;

    @FXML
    private CheckBox publicPlaylistChckBox;

    @FXML
    private TextField playlistDescriptionTextField;

    @FXML
    private Button removeBtn;

    @FXML
    private Button saveBtn;

    @FXML
    void removeBtn_OnAction(ActionEvent event) {
        playlist = null;
        close();
    }

    @FXML
    void saveBtn_OnAction(ActionEvent event) {
        playlist.setTitle(playlistNameTextField.getText());
        playlist.setDescription(playlistDescriptionTextField.getText());
        playlist.set_public(!privatePlaylistChckBox.isSelected());
        playlist.setCollaborative(publicPlaylistChckBox.isSelected());
        close();
    }

    @FXML
    void initialize() {
        assert playlistActionLbl != null : "fx:id=\"playlistActionLbl\" was not injected: check your FXML file 'playlistdialog.fxml'.";
        assert playlistPicture != null : "fx:id=\"playlistPicture\" was not injected: check your FXML file 'playlistdialog.fxml'.";
        assert playlistNameTextField != null : "fx:id=\"playlistNameTextField\" was not injected: check your FXML file 'playlistdialog.fxml'.";
        assert privatePlaylistChckBox != null : "fx:id=\"privatePlaylistRadioBtn\" was not injected: check your FXML file 'playlistdialog.fxml'.";
        assert publicPlaylistChckBox != null : "fx:id=\"publicPlaylistRadioBtn\" was not injected: check your FXML file 'playlistdialog.fxml'.";
        assert playlistDescriptionTextField != null : "fx:id=\"playlistDescriptionTextField\" was not injected: check your FXML file 'playlistdialog.fxml'.";
        assert removeBtn != null : "fx:id=\"removeBtn\" was not injected: check your FXML file 'playlistdialog.fxml'.";
        assert saveBtn != null : "fx:id=\"saveBtn\" was not injected: check your FXML file 'playlistdialog.fxml'.";

        privatePlaylistChckBox.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal && publicPlaylistChckBox.isSelected())
                publicPlaylistChckBox.setSelected(false);
        });

        publicPlaylistChckBox.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal && privatePlaylistChckBox.isSelected())
                privatePlaylistChckBox.setSelected(false);
        });

        playlistNameTextField.textProperty().addListener((observableValue, oldVal, newVal)
                -> saveBtn.setDisable(newVal == null || newVal.trim().equals("")));
    }
}
