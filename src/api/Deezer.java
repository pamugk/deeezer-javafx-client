package api;

import api.events.authentication.AuthenticationEvent;
import api.events.base.DeezerEventHandler;
import api.net.Permissions;
import api.net.DeezerRequestExecutor;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.User;
import api.objects.utils.search.Search;
import api.objects.utils.search.SearchOrder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.google.gson.Gson;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Deezer {
    private DeezerRequestExecutor requestExecutor;
    private static final String apiUrlPrefix = "https://api.deezer.com";
    private DeezerEventHandler<AuthenticationEvent> authenticationEventHandler;
    private User loggedInUser;
    private boolean connected;

    public Deezer() throws IOException {
        Properties apiClientProps = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/clientConfig.properties")){
            apiClientProps.load(inputStream);
        }

        authenticationEventHandler = new DeezerEventHandler<>();

        try {
            requestExecutor = new DeezerRequestExecutor(
                    apiClientProps.getProperty("callbackContext"), apiClientProps.getProperty("apiKey"),
                    apiClientProps.getProperty("apiSecret"), Arrays.asList(Permissions.values()));
            requestExecutor.setAuthenticationEventHandler(authenticationEventHandler);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Album management">
    public boolean addAlbumToUserLibrary(Album album) {
        boolean success = true;
        try {
            changePlayableFavour(album, Verb.POST);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
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

    public void removeAlbumFromLibrary(Album album) {
        try {
            Response response = changePlayableFavour(album, Verb.DELETE);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Artist management">
    public void addArtistToFavourites(Artist artist) {
        try {
            Response response = changePlayableFavour(artist, Verb.POST);
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

    public void removeArtistFromFavorites(Artist artist) {
        try {
            Response response = changePlayableFavour(artist, Verb.DELETE);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Base library management">
    private Response changePlayableFavour(Playable playable, Verb action)
            throws InterruptedException, ExecutionException, IOException {
        OAuthRequest request = new OAuthRequest(action,
                String.format("%s/user/%d/%ss", apiUrlPrefix, loggedInUser.getId(), playable.getType()));
        request.addParameter(String.format("%s_id", playable.getType()), String.valueOf(playable.getId()));
        return requestExecutor.execute(request);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Comment management">
    public boolean removeComment(Comment comment) {
        boolean success = true;
        OAuthRequest request = new OAuthRequest(Verb.DELETE,
                String.format("%s/comment/%d", apiUrlPrefix, comment.getId()));
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Community management">
    public boolean addNotification(User user, String message) {
        boolean success = true;
        OAuthRequest request = new OAuthRequest(Verb.DELETE,
                String.format("%s/user/%d/notifications", apiUrlPrefix, user.getId()));
        request.addParameter("message", message);
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private Response changeFollowings(long user_id, Verb action) throws InterruptedException, ExecutionException, IOException {
        OAuthRequest request = new OAuthRequest(action,
                String.format("%s/user/%d/followings", apiUrlPrefix, loggedInUser.getId()));
        request.addParameter("user_id", String.valueOf(user_id));
        return requestExecutor.execute(request);
    }

    public boolean followUser(User user) {
        boolean success = true;
        try {
            Response response = changeFollowings(user.getId(), Verb.POST);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public boolean unfollowUser(User user) {
        boolean success = true;
        try {
            Response response = changeFollowings(user.getId(), Verb.DELETE);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Event handling">
    public DeezerEventHandler<AuthenticationEvent> getAuthenticationEventHandler() {
        return authenticationEventHandler;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Login/logout">
    public void login() {
        try {
            requestExecutor.authenticate();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        loggedInUser = null;
    }

    public User getLoggedInUser(){
        if (loggedInUser != null)
            return loggedInUser;

        OAuthRequest request = new OAuthRequest(Verb.GET, apiUrlPrefix + "/user/me");
        try {
            Response response = requestExecutor.execute(request);
            loggedInUser = new Gson().fromJson(response.getBody(), User.class);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return loggedInUser;
    }

    public LoginStatus getLoginStatus() {
        return LoginStatus.UNKNOWN;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Playlist management">
    public void addCommentToPlaylist(Playlist playlist, String comment) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/playlist/%d/comments",
                apiUrlPrefix, playlist.getId()));
        request.addParameter("comment", comment);
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addPlaylistToFavourites(Playlist playlist) {
        boolean success = true;
        try {
            Response response = changePlayableFavour(playlist, Verb.POST);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public void addTracksToPlaylist(Playlist playlist, List<Track> songs) {
        try {
            Response response = updatePlaylist(playlist, songs, "songs", Verb.POST);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
    }

    public Playlist createPlaylist(String title) {
        Playlist createdPlaylist = null;
        OAuthRequest request = new OAuthRequest(Verb.POST,
                String.format("%s/user/%d/playlists", apiUrlPrefix, loggedInUser.getId()));
        request.addParameter("title", title);
        try {
            Response response = requestExecutor.execute(request);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
        return createdPlaylist;
    }

    public boolean markPlaylistAsSeen(Playlist playlist) {
        boolean success = true;
        try {
            OAuthRequest request = new OAuthRequest(Verb.POST,
                    String.format("%s/playlist/%d/seen", apiUrlPrefix,playlist.getId()));
            Response response = requestExecutor.execute(request);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public void orderTracksInPlaylist(Playlist playlist, List<Track> order) {
        try {
            Response response = updatePlaylist(playlist, order, "order", Verb.POST);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
    }

    public void ratePlaylist(Playlist playlist, int note) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/playlist/%d", apiUrlPrefix, playlist.getId()));
        request.addParameter("note", String.valueOf(note));
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean removePlaylist(Playlist playlist) {
        boolean success = true;
        OAuthRequest request = new OAuthRequest(Verb.DELETE,
                String.format("%s/playlist/%d", apiUrlPrefix,playlist.getId()));
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public boolean removePlaylistFromFavourites(Playlist playlist) {
        boolean success = true;
        try {
            changePlayableFavour(playlist, Verb.DELETE);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public void removeTracksFromPlaylist(Playlist playlist, List<Track> songs) {
        try {
            Response response = updatePlaylist(playlist, songs, "songs", Verb.DELETE);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
    }

    private Response updatePlaylist(Playlist playlist, List<Track> songs, String parameter, Verb action)
            throws InterruptedException, ExecutionException, IOException {
        OAuthRequest request = new OAuthRequest(action,
                String.format("%s/playlist/%d/tracks", apiUrlPrefix,playlist.getId()));
        request.addParameter(parameter, songs.stream().map(
                song -> String.valueOf(song.getId())).collect(Collectors.joining(",")));
        return requestExecutor.execute(request);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Radio management">
    public boolean addRadioToFavourites(Radio radio) {
        boolean success = true;
        try {
            Response response = changePlayableFavour(radio, Verb.POST);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public boolean removeRadioFromFavourites(Radio radio) {
        boolean success = true;
        try {
            changePlayableFavour(radio, Verb.DELETE);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Search">
    public Search search(String query, Boolean strict, SearchOrder order) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/search", apiUrlPrefix));
        request.addParameter("q", query);
        request.addParameter("strict", String.valueOf(strict));
        if (order != null)
            request.addParameter("order", order.name());
        Search search = null;
        try {
            Response response = requestExecutor.execute(request);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Track management">
    public boolean addTrackToFavourites(Track track) {
        boolean success = true;
        try {
            Response response = changePlayableFavour(track, Verb.POST);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    /*public void removePersonalTrack(Track track) {

    }*/

    public boolean removeTrackFromFavourites(Track track) {
        boolean success = true;
        try {
            Response response = changePlayableFavour(track, Verb.DELETE);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    /*public void updatePersonalTrack(Track track) {

    }*/
    //</editor-fold>
 }
