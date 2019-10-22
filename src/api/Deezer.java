package api;

import api.events.authentication.AuthenticationEvent;
import api.events.base.DeezerEventHandler;
import api.net.Permissions;
import api.net.DeezerRequestExecutor;
import api.objects.DeezerEntity;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.User;
import api.objects.utils.search.FullSearchSet;
import api.objects.utils.search.SearchOrder;
import api.objects.utils.search.SearchResponse;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Deezer {
    private DeezerRequestExecutor requestExecutor;
    private DeezerEventHandler<AuthenticationEvent> authenticationEventHandler;
    private Long currentUserId;

    private static final String API_URL_PREFIX = "https://api.deezer.com";

    private static final String ALBUM_SECTION = "album";
    private static final String ALBUMS_SECTION = "albums";
    private static final String ARTIST_SECTION = "artist";
    private static final String ARTISTS_SECTION = "artists";
    private static final String COMMENT_SECTION = "comment";
    private static final String COMMENTS_SECTION = "comments";
    private static final String PLAYLIST_SECTION = "playlist";
    private static final String PLAYLISTS_SECTION = "playlists";
    private static final String RADIO_SECTION = "radio";
    private static final String RADIOS_SECTION = "radios";
    private static final String TRACK_SECTION = "track";
    private static final String TRACKS_SECTION = "tracks";
    private static final String USER_SECTION = "user";
    private static final String USERS_SECTION = "users";

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

    public void stop() { requestExecutor.stop(); }

    //<editor-fold defaultstate="collapsed" desc="Album management">
    public boolean addAlbumToUserLibrary(Album album) {
        boolean success = true;
        try {
            changePlayableFavour(album, Verb.POST, ALBUMS_SECTION);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public void addCommentToAlbum(Album album, Comment comment) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/%s/%d/%s",
                API_URL_PREFIX, ALBUMS_SECTION, album.getId(), COMMENTS_SECTION));
        request.addParameter("comment", comment.getText());
        try {
            Response response = requestExecutor.execute(request, true);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void rateAlbum(Album album, int note) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/%s/%d",
                API_URL_PREFIX, ALBUM_SECTION, album.getId()));
        request.addParameter("note", String.valueOf(note));
        try {
            Response response = requestExecutor.execute(request, true);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void removeAlbumFromLibrary(Album album) {
        try {
            Response response = changePlayableFavour(album, Verb.DELETE, ALBUMS_SECTION);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Artist management">
    public void addArtistToFavourites(Artist artist) {
        try {
            Response response = changePlayableFavour(artist, Verb.POST, ARTISTS_SECTION);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addCommentToArtist(Artist artist, Comment comment) {
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/%s/%d/%s",
                API_URL_PREFIX, ARTIST_SECTION, artist.getId(), COMMENTS_SECTION));
        request.addParameter("comment", comment.getText());
        try {
            Response response = requestExecutor.execute(request, true);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void removeArtistFromFavorites(Artist artist) {
        try {
            Response response = changePlayableFavour(artist, Verb.DELETE, ARTISTS_SECTION);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Base library management">
    private Response changePlayableFavour(Playable playable, Verb action, String section)
            throws InterruptedException, ExecutionException, IOException {
        OAuthRequest request = new OAuthRequest(action,
                String.format("%s/%s/%d/%s", API_URL_PREFIX, USER_SECTION, currentUserId, section));
        request.addParameter(String.format("%s_id", playable.getType()), String.valueOf(playable.getId()));
        return requestExecutor.execute(request, true);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Comment management">
    public boolean removeComment(Comment comment) {
        boolean success = true;
        OAuthRequest request = new OAuthRequest(Verb.DELETE,
                String.format("%s/%s/%d", API_URL_PREFIX, COMMENT_SECTION, comment.getId()));
        try {
            Response response = requestExecutor.execute(request, true);
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
                String.format("%s/%s/%d/notifications", API_URL_PREFIX, USER_SECTION, user.getId()));
        request.addParameter("message", message);
        try {
            Response response = requestExecutor.execute(request, true);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private Response changeFollowings(long user_id, Verb action) throws InterruptedException, ExecutionException, IOException {
        OAuthRequest request = new OAuthRequest(action,
                String.format("%s/%s/%d/followings", API_URL_PREFIX, USER_SECTION, currentUserId));
        request.addParameter("user_id", String.valueOf(user_id));
        return requestExecutor.execute(request, true);
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
        currentUserId = null;
    }

    public User getLoggedInUser(){
        User loggedInUser = null;
        OAuthRequest request = new OAuthRequest(Verb.GET, String.format("%s/%s/me", API_URL_PREFIX, USER_SECTION));
        try {
            Response response = requestExecutor.execute(request, true);
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
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/%s/%d/%s",
                API_URL_PREFIX, PLAYLIST_SECTION, playlist.getId(), COMMENTS_SECTION));
        request.addParameter("comment", comment);
        try {
            Response response = requestExecutor.execute(request, true);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addPlaylistToFavourites(Playlist playlist) {
        boolean success = true;
        try {
            Response response = changePlayableFavour(playlist, Verb.POST, PLAYLISTS_SECTION);
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
                String.format("%s/%s/%d/%s", API_URL_PREFIX, USER_SECTION, currentUserId, PLAYLISTS_SECTION));
        request.addParameter("title", title);
        try {
            Response response = requestExecutor.execute(request, true);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
        return createdPlaylist;
    }

    public boolean markPlaylistAsSeen(Playlist playlist) {
        boolean success = true;
        try {
            OAuthRequest request = new OAuthRequest(Verb.POST,
                    String.format("%s/playlist/%d/seen", API_URL_PREFIX,playlist.getId()));
            Response response = requestExecutor.execute(request, true);
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
        OAuthRequest request = new OAuthRequest(Verb.POST, String.format("%s/playlist/%d", API_URL_PREFIX, playlist.getId()));
        request.addParameter("note", String.valueOf(note));
        try {
            Response response = requestExecutor.execute(request, true);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean removePlaylist(Playlist playlist) {
        boolean success = true;
        OAuthRequest request = new OAuthRequest(Verb.DELETE,
                String.format("%s/playlist/%d", API_URL_PREFIX,playlist.getId()));
        try {
            Response response = requestExecutor.execute(request, true);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public boolean removePlaylistFromFavourites(Playlist playlist) {
        boolean success = true;
        try {
            changePlayableFavour(playlist, Verb.DELETE, PLAYLISTS_SECTION);
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
                String.format("%s/%s/%d/%s", API_URL_PREFIX, PLAYLIST_SECTION, playlist.getId(), TRACKS_SECTION));
        request.addParameter(parameter, songs.stream().map(
                song -> String.valueOf(song.getId())).collect(Collectors.joining(",")));
        return requestExecutor.execute(request, true);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Radio management">
    public boolean addRadioToFavourites(Radio radio) {
        boolean success = true;
        try {
            Response response = changePlayableFavour(radio, Verb.POST, RADIOS_SECTION);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public boolean removeRadioFromFavourites(Radio radio) {
        boolean success = true;
        try {
            changePlayableFavour(radio, Verb.DELETE, RADIOS_SECTION);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Search">
    private <T extends DeezerEntity> SearchResponse<T> abstractSearch(
            Type responseType, String query, Boolean strict, SearchOrder order, String searchPlace) {
        OAuthRequest request = new OAuthRequest(Verb.GET,
                String.format("%s/search/%s", API_URL_PREFIX, searchPlace));
        request.addParameter("q", query);
        if (strict != null)
            request.addParameter("strict", String.valueOf(strict));
        if (order != null)
            request.addParameter("order", order.name());
        SearchResponse<T> searchResult = null;
        try {
            Response response = requestExecutor.execute(request, false);
            String body = response.getBody();
            searchResult = new Gson().fromJson(body, responseType);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    public FullSearchSet search(String query, Boolean strict) {
        FullSearchSet searchSet = new FullSearchSet();
        searchSet.setTrackResponse(searchForTracks(query, strict, null));
        searchSet.setAlbumResponse(searchForAlbums(query, strict, null));
        searchSet.setArtistResponse(searchForArtists(query, strict, null));
        searchSet.setPlaylistResponse(searchForPlaylists(query, strict, null));
        searchSet.setRadioResponse(searchForRadios(query, strict, null));
        searchSet.setUserResponse(searchForUsers(query, strict, null));
        return searchSet;
    }

    public SearchResponse<Album> searchForAlbums(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(new TypeToken<SearchResponse<Album>>(){}.getType(),
                query, strict, order, ALBUM_SECTION);
    }

    public SearchResponse<Artist> searchForArtists(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(new TypeToken<SearchResponse<Artist>>(){}.getType(),
                query, strict, order, ARTIST_SECTION);
    }

    public SearchResponse<Playlist> searchForPlaylists(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(new TypeToken<SearchResponse<Playlist>>(){}.getType(),
                query, strict, order, PLAYLIST_SECTION);
    }

    public SearchResponse<Radio> searchForRadios(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(new TypeToken<SearchResponse<Radio>>(){}.getType(),
                query, strict, order, RADIO_SECTION);
    }

    public SearchResponse<TrackSearch> searchForTracks(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(new TypeToken<SearchResponse<TrackSearch>>(){}.getType(),
                query, strict, order, TRACK_SECTION);
    }

    public SearchResponse<User> searchForUsers(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(new TypeToken<SearchResponse<User>>(){}.getType(),
                query, strict, order, USER_SECTION);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Track management">
    public boolean addTrackToFavourites(Track track) {
        boolean success = true;
        try {
            Response response = changePlayableFavour(track, Verb.POST, TRACKS_SECTION);
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
            Response response = changePlayableFavour(track, Verb.DELETE, TRACKS_SECTION);
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
