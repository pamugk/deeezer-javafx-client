package components.views;

import api.PartialSearchResponse;
import api.objects.playables.TrackSearch;
import api.objects.utils.search.FullSearchSet;
import components.containers.flows.*;
import components.containers.tables.TrackTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    private TrackTable<TrackSearch> tracksResultTV;
    @FXML
    private Button albumsResultBtn;
    @FXML
    private ArtistFlowPane artistsResultsFP;
    @FXML
    private AlbumFlowPane albumsResultsFP;
    @FXML
    private Button artistsResultBtn;
    @FXML
    private Button playlistsResultsBtn;
    @FXML
    private PlaylistFlowPane playlistsResultsFP;
    @FXML
    private Button profilesResultsBtn;
    @FXML
    private UserFlowPane profilesResultsFP;
    @FXML
    private Tab trackResultsTab;
    @FXML
    private Label foundTracksLbl;
    @FXML
    private TrackTable<TrackSearch> foundTracksTV;
    @FXML
    private Tab albumResultsTab;
    @FXML
    private Label foundAlbumsLbl;
    @FXML
    private AlbumFlowPane foundAlbumsFP;
    @FXML
    private Tab artistResultsTab;
    @FXML
    private Label foundArtistsLbl;
    @FXML
    private ArtistFlowPane foundArtistsFP;
    @FXML
    private Tab playlistResultsTab;
    @FXML
    private Label foundPlaylistsLabel;
    @FXML
    private PlaylistFlowPane foundPlaylistsFP;
    @FXML
    private Tab mixResultsTab;
    @FXML
    private Label foundMixesLbl;
    @FXML
    private RadioFlowPane foundMixesFP;
    @FXML
    private Tab profileResultsTab;
    @FXML
    private Label foundProfilesLbl;
    @FXML
    private UserFlowPane foundProfilesFP;

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

        boolean found = searchSet.getTrackResponse().getData().size() > 0;
        if (found) {
            tracksResultTV.fill(
                    new PartialSearchResponse<>(searchSet.getTrackResponse().getData().stream().limit(6).collect(Collectors.toList())),
                    null, true);
            searchTabPane.getTabs().add(trackResultsTab);
        }
        foundTracksTV.fill(searchSet.getTrackResponse(), foundTracksLbl, true);
        tracksResultBtn.setVisible(found);
        tracksResultTV.setVisible(found);

        found = searchSet.getAlbumResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(albumResultsTab);
        albumsResultBtn.setVisible(found);
        albumsResultsFP.setVisible(found);
        albumsResultsFP.fill(new PartialSearchResponse<>(searchSet.getAlbumResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null, true, false);
        foundAlbumsFP.fill(searchSet.getAlbumResponse(), foundAlbumsLbl, true, true);

        found=searchSet.getArtistResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(artistResultsTab);
        artistsResultBtn.setVisible(found);
        artistsResultsFP.setVisible(found);
        artistsResultsFP.fill(new PartialSearchResponse<>(searchSet.getArtistResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null, true, false);
        foundArtistsFP.fill(searchSet.getArtistResponse(), foundArtistsLbl, true, true);

        found = searchSet.getPlaylistResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(playlistResultsTab);
        playlistsResultsBtn.setVisible(found);
        playlistsResultsFP.setVisible(found);
        playlistsResultsFP.fill(new PartialSearchResponse<>(searchSet.getPlaylistResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null, true, false);
        foundPlaylistsFP.fill(searchSet.getPlaylistResponse(), foundPlaylistsLabel, true, true);

        //if (searchSet.getRadioResponse().getData().size() > 0)
        //   searchTabPane.getTabs().add(mixResultsTab);
        //fillFlowPaneWithRadios(foundMixesFP, searchSet.getRadioResponse(), foundMixesLbl);

        found = searchSet.getUserResponse().getData().size() > 0;
        if (found)
            searchTabPane.getTabs().add(profileResultsTab);
        profilesResultsBtn.setVisible(found);
        profilesResultsFP.setVisible(found);
        profilesResultsFP.fill(new PartialSearchResponse<>(searchSet.getUserResponse()
                .getData().stream().limit(4).collect(Collectors.toList())), null, true, false);
        foundProfilesFP.fill(searchSet.getUserResponse(), foundProfilesLbl, true, true);
    }
}
