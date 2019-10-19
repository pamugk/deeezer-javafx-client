package api;

import api.events.authentication.AuthenticationEvent;
import api.events.base.DeezerEventHandler;
import api.net.Permissions;
import api.net.DeezerRequestExecutor;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.User;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Deezer {
    private final DeezerRequestExecutor requestExecutor;
    private static final String apiUrlPrefix = "https://api.deezer.com";
    private final DeezerEventHandler<AuthenticationEvent> authenticationEventHandler;

    public Deezer() throws IOException {
        Properties apiClientProps = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/clientConfig.properties")){
            apiClientProps.load(inputStream);
        }

        authenticationEventHandler = new DeezerEventHandler<>();

        requestExecutor = new DeezerRequestExecutor(
                apiClientProps.getProperty("callbackContext"), apiClientProps.getProperty("apiKey"),
                apiClientProps.getProperty("apiSecret"), Arrays.asList(Permissions.values()));
    }

    //<editor-fold defaultstate="collapsed" desc="Album management">
    public void addAlbumToUserLibrary(User user, Album album) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/user/%d/albums", apiUrlPrefix, user.getId()));
        request.addParameter("album_id", String.valueOf(album.getId()));
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addCommentToAlbum(Album album, Comment comment) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/album/%d/comments", apiUrlPrefix, album.getId()));
        request.addParameter("comment", comment.getText());
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void rateAlbum(Album album, int note) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/album/%d", apiUrlPrefix, album.getId()));
        request.addParameter("note", String.valueOf(note));
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void removeAlbumFromLibrary(User user, Album album) {
        OAuthRequest request = new OAuthRequest(Verb.DELETE, String.format("%s/user/%d/albums", apiUrlPrefix, user.getId()));
        request.addParameter("album_id", String.valueOf(album.getId()));
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Artist management">
    public void addArtistToFavourites(User user, Artist artist) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/user/%d/artists", apiUrlPrefix, user.getId()));
        request.addParameter("artist_id", String.valueOf(artist.getId()));
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addCommentToArtist(Artist artist, Comment comment) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/artist/%d/comments", apiUrlPrefix, artist.getId()));
        request.addParameter("comment", comment.getText());
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void removeArtistFromFavorites(User user, Artist artist) {
        OAuthRequest request = new OAuthRequest(Verb.DELETE, String.format("%s/user/%d/artists", apiUrlPrefix, user.getId()));
        request.addParameter("album_id", String.valueOf(artist.getId()));
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Comment management">
    public void removeComment(Comment comment) {

    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Community management">
    public void addNotification(User user) {

    }

    public void followUser(User user) {

    }

    public void unfollowUser(User user) {

    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Event handling">
    public DeezerEventHandler<AuthenticationEvent> getAuthenticationEventHandler() {
        return authenticationEventHandler;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Login/logout">
    public void login() throws IOException, URISyntaxException {
        requestExecutor.authenticate();
    }

    public void logout() {

    }

    public User getLoggedInUser(){
        OAuthRequest request = new OAuthRequest(Verb.GET, apiUrlPrefix + "/user/me");
        try {
            Response response = requestExecutor.execute(request);
            return new Gson().fromJson(response.getBody(), User.class);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getLoginStatus() {

    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="User's library management">
    private void addPlayableToLibrary(User user, Playable playable) {

    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Playlist management">
    public void addCommentToPlaylist(Playlist playlist, Comment comment) {

    }

    public void addPlaylistToFavourites(Playlist playlist) {

    }

    public void addTracksToPlaylist(Playlist playlist, List<Track> songs) {

    }

    public void createPlaylist(String title) {

    }

    public void markPlaylistAsSeen(Playlist playlist) {

    }

    public void orderTracksInPlaylist(Playlist playlist, List<Track> order) {

    }

    public void ratePlaylist(Playlist playlist, int note) {

    }

    public void removePlaylist(Playlist playlist) {

    }

    public void removePlaylistFromFavourites(Playlist playlist) {

    }

    public void removeTracksFromPlaylist(Playlist playlist, List<Track> songs) {

    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Radio management">
    public void addRadioToFavourites(Radio radio) {

    }

    public void removeRadioFromFavourites(Radio radio) {

    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Track management">
    public void addTrackToFavourites(Track track) {

    }

    public void removePersonalTrack(Track track) {

    }

    public void removeTrackFromFavourites(Track track) {

    }

    public void updatePersonalTrack(Track track) {

    }
    //</editor-fold>
 }
