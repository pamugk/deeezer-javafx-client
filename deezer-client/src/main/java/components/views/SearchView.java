package components.views;

import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.TrackSearch;
import api.objects.utils.search.FullSearchSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import utils.UiUtils;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static utils.UiUtils.*;
import static utils.UiUtils.fillFlowPaneWithUsers;

public class SearchView extends VBox {
    public SearchView() {
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("searchView.fxml"), bundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private TabPane searchTabPane;
    @FXML
    private Tab allResultsTab;
    @FXML
    private VBox fullFoundBox;
    @FXML
    private Button tracksResultBtn;
    @FXML
    private TableView<TrackSearch> tracksResultTV;
    @FXML
    private TableColumn<TrackSearch, Album> trackResultImgCol;
    @FXML
    private TableColumn<TrackSearch, String> trackResultTitleCol;
    @FXML
    private TableColumn<TrackSearch, Artist> trackResultArtistCol;
    @FXML
    private TableColumn<TrackSearch, Album> trackResultAlbumCol;
    @FXML
    private TableColumn<TrackSearch, Integer> trackResultLengthCol;
    @FXML
    private TableColumn<TrackSearch, Integer> trackResultPopCol;
    @FXML
    private Button albumsResultBtn;
    @FXML
    private FlowPane artistsResultsFP;
    @FXML
    private FlowPane albumsResultsFP;
    @FXML
    private Button artistsResultBtn;
    @FXML
    private FlowPane tracksResultsFP;
    @FXML
    private Button playlistsResultsBtn;
    @FXML
    private FlowPane playlistsResultsFP;
    @FXML
    private Button profilesResultsBtn;
    @FXML
    private FlowPane profilesResultsFP;
    @FXML
    private Tab trackResultsTab;
    @FXML
    private Label foundTracksLbl;
    @FXML
    private TableView<TrackSearch> foundTracksTV;
    @FXML
    private TableColumn<TrackSearch, Album> foundTrAlbImgCol;
    @FXML
    private TableColumn<TrackSearch, String> foundTrNameCol;
    @FXML
    private TableColumn<TrackSearch, Artist> foundTrArtistCol;
    @FXML
    private TableColumn<TrackSearch, Album> foundTrAlbumCol;
    @FXML
    private TableColumn<TrackSearch, Integer> foundTrLengthCol;
    @FXML
    private TableColumn<TrackSearch, Integer> foundTrPopCol;
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
    void albumsResultBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(albumResultsTab);
    }

    @FXML
    void artistsResultBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(artistResultsTab);
    }

    @FXML
    void playlistsResultsBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(playlistResultsTab);
    }

    @FXML
    void profilesResultsBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(profileResultsTab);
    }

    @FXML
    void tracksResultBtn_OnAction(ActionEvent actionEvent) {
        searchTabPane.getSelectionModel().select(trackResultsTab);
    }

    public void setSearchResults(FullSearchSet searchSet) {
        searchTabPane.getTabs().clear();
        searchTabPane.getTabs().add(allResultsTab);

        foundTracksTV.getItems().clear();
        boolean found = searchSet.getTrackResponse().getData().size() > 0;
        if (found) {
            tracksResultTV.getItems().addAll(
                    searchSet.getTrackResponse().getData().stream().limit(6).collect(Collectors.toList()));
            searchTabPane.getTabs().add(trackResultsTab);
        }
        UiUtils.fillTableWithTracks(foundTracksTV, searchSet.getTrackResponse(), foundTracksLbl);
        tracksResultBtn.setVisible(found);
        tracksResultTV.setVisible(found);

        found = searchSet.getAlbumResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(albumResultsTab);
        albumsResultBtn.setVisible(found);
        albumsResultsFP.setVisible(found);
        fillFlowPaneWithAlbums(albumsResultsFP, new PartialSearchResponse<>(searchSet.getAlbumResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null, true, false);
        fillFlowPaneWithAlbums(foundAlbumsFP, searchSet.getAlbumResponse(), foundAlbumsLbl, true, true);

        found=searchSet.getArtistResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(artistResultsTab);
        artistsResultBtn.setVisible(found);
        artistsResultsFP.setVisible(found);
        fillFlowPaneWithArtists(artistsResultsFP, new PartialSearchResponse<>(searchSet.getArtistResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null, true, false);
        fillFlowPaneWithArtists(foundArtistsFP, searchSet.getArtistResponse(), foundArtistsLbl, true, true);

        found = searchSet.getPlaylistResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(playlistResultsTab);
        playlistsResultsBtn.setVisible(found);
        playlistsResultsFP.setVisible(found);
        fillFlowPaneWithPlaylists(playlistsResultsFP, new PartialSearchResponse<>(searchSet.getPlaylistResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null, true, false);
        fillFlowPaneWithPlaylists(foundPlaylistsFP, searchSet.getPlaylistResponse(), foundPlaylistsLabel, true, true);

        //if (searchSet.getRadioResponse().getData().size() > 0)
        //   searchTabPane.getTabs().add(mixResultsTab);
        //fillFlowPaneWithRadios(foundMixesFP, searchSet.getRadioResponse(), foundMixesLbl);

        found = searchSet.getUserResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(profileResultsTab);
        profilesResultsBtn.setVisible(found);
        profilesResultsFP.setVisible(found);
        fillFlowPaneWithUsers(profilesResultsFP, new PartialSearchResponse<>(searchSet.getUserResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null, true, false);
        fillFlowPaneWithUsers(foundProfilesFP, searchSet.getUserResponse(), foundProfilesLbl, true, true);
    }
}
