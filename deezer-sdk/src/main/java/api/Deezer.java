package api;

import api.events.authentication.AuthenticationEvent;
import api.events.handlers.DeezerEventHandler;
import api.net.Permissions;
import api.net.DeezerRequestExecutor;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.User;
import api.objects.utils.search.FullSearchSet;
import api.objects.utils.search.SearchOrder;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Deezer {
    private DeezerRequestExecutor requestExecutor;
    private final DeezerEventHandler<AuthenticationEvent> authenticationEventHandler;
    private Long currentUserId;

    private final Gson serializer;

    private static final Type ALBUM_TYPE = new TypeToken<Album>(){}.getType();
    private static final Type ARTIST_TYPE = new TypeToken<Artist>(){}.getType();
    private static final Type ALBUM_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Album>>(){}.getType();
    private static final Type ARTIST_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Artist>>(){}.getType();
    private static final Type COMMENT_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Comment>>(){}.getType();
    private static final Type PLAYLIST_TYPE = new TypeToken<Playlist>(){}.getType();
    private static final Type PLAYLIST_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Playlist>>(){}.getType();
    private static final Type TRACK_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Track>>(){}.getType();
    private static final Type TRACKSEARCH_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<TrackSearch>>(){}.getType();
    private static final Type USER_TYPE = new TypeToken<User>(){}.getType();
    private static final Type USER_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<User>>(){}.getType();

    private static final String API_URL_PREFIX = "https://api.deezer.com";

    private static final String ALBUM_SECTION = "album";
    private static final String ALBUMS_SECTION = "albums";
    private static final String ARTIST_SECTION = "artist";
    private static final String ARTISTS_SECTION = "artists";
    private static final String COMMENTS_SECTION = "comments";
    private static final String PLAYLIST_SECTION = "playlist";
    private static final String PLAYLISTS_SECTION = "playlists";
    private static final String RECOMMENDATIONS_SECTION = "recommendations";
    private static final String SEARCH_SECTION = "search";
    private static final String TRACK_SECTION = "track";
    private static final String TRACKS_SECTION = "tracks";
    private static final String USER_SECTION = "user";

    public Deezer(Configuration configuration) throws IOException {
        authenticationEventHandler = new DeezerEventHandler<>();
        serializer = new GsonBuilder().create();

        try {
            requestExecutor = new DeezerRequestExecutor(configuration.callbackContext(), configuration.apiKey(),
                    configuration.apiSecret(), Arrays.asList(Permissions.values()));
            requestExecutor.setAuthenticationEventHandler(authenticationEventHandler);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | CertificateException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public void stop() { requestExecutor.stop(); }

    public Album getAlbum(long albumId) {
        return abstractSearch(ALBUM_TYPE, null, null,
                String.format("%s/%d", ALBUM_SECTION, albumId));
    }

    public PartialSearchResponse<Track> getAlbumTracks(Album album) {
        return abstractSearch(TRACK_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", ALBUM_SECTION, album.id(), TRACKS_SECTION), 25, false);
    }

    public Artist getArtist(long artistId) {
        return abstractSearch(ARTIST_TYPE, null, null,
                String.format("%s/%d", ARTIST_SECTION, artistId));
    }

    public PartialSearchResponse<Comment> getArtistComments(Artist artist) {
        return abstractSearch(COMMENT_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.id(), COMMENTS_SECTION), 25, false);
    }

    public PartialSearchResponse<Album> getArtistDiscography(Artist artist) {
        return abstractSearch(ALBUM_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.id(), ALBUMS_SECTION), 25, false);
    }

    public PartialSearchResponse<Playlist> getArtistPlaylists(Artist artist, int limit) {
        PartialSearchResponse<Playlist> artistPlaylists = abstractSearch(PLAYLIST_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.id(), PLAYLISTS_SECTION), limit, true);
        artistPlaylists.data().replaceAll(playlist -> getPlaylist(playlist.id()));
        return artistPlaylists;
    }

    public PartialSearchResponse<Artist> getArtistRelated(Artist artist, int limit) {
        return abstractSearch(ARTIST_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.id(), "related"), limit, true);
    }

    public PartialSearchResponse<TrackSearch> getArtistTop(Artist artist, int limit) {
        return abstractSearch(TRACKSEARCH_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/top", ARTIST_SECTION, artist.id()), limit, true);
    }

    public DeezerEventHandler<AuthenticationEvent> getAuthenticationEventHandler() {
        return authenticationEventHandler;
    }

    public void login() {
        try {
            requestExecutor.authenticate();
        } catch (IOException | URISyntaxException | UnrecoverableKeyException |
                NoSuchAlgorithmException | KeyStoreException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        currentUserId = null;
        requestExecutor.tearStorageDown();
        authenticationEventHandler.invoke(new AuthenticationEvent(false));
    }

    public User getLoggedInUser(){
        User loggedInUser = null;
        OAuthRequest request = new OAuthRequest(Verb.GET, String.format("%s/%s/me", API_URL_PREFIX, USER_SECTION));
        try(Response response = requestExecutor.execute(request)) {
            loggedInUser = serializer.fromJson(response.getBody(), User.class);
            if (currentUserId == null)
                currentUserId = loggedInUser.id();
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return loggedInUser;
    }

    public LoginStatus getLoginStatus() {
        if (!requestExecutor.isAuthorised())
            return LoginStatus.NOT_AUTHORIZED;
        return LoginStatus.UNKNOWN;
    }

    public Playlist getPlaylist(long playlistId) {
        return abstractSearch(PLAYLIST_TYPE, null, null,
                String.format("%s/%d", PLAYLIST_SECTION, playlistId));
    }

    public PartialSearchResponse<Comment> getPlaylistComments(Playlist playlist) {
        return abstractSearch(COMMENT_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", PLAYLIST_SECTION, playlist.id(), COMMENTS_SECTION), -1, false);
    }

    public PartialSearchResponse<Album> getRecommendedAlbums(int count) {
        return abstractSearch(ALBUM_RESPONSE_TYPE, null, null, null,
                String.format("%s/me/%s/%s", USER_SECTION, RECOMMENDATIONS_SECTION, ALBUMS_SECTION), count, true);
    }

    public PartialSearchResponse<Artist> getRecommendedArtists(int count) {
        return abstractSearch(ARTIST_RESPONSE_TYPE, null, null, null,
                String.format("%s/me/%s/%s", USER_SECTION, RECOMMENDATIONS_SECTION, ARTISTS_SECTION),
                count, true);
    }

    public PartialSearchResponse<Playlist> getRecommendedPlaylists(int count) {
        return abstractSearch(PLAYLIST_RESPONSE_TYPE, null, null, null,
                String.format("%s/me/%s/%s", USER_SECTION, RECOMMENDATIONS_SECTION, PLAYLISTS_SECTION),
                count, true);
    }

    private <T> T abstractSearch(
            Type responseType, Boolean strict, SearchOrder order, String searchPlace) {
        OAuthRequest request = new OAuthRequest(Verb.GET,
                String.format("%s/%s", API_URL_PREFIX, searchPlace));
        if (strict != null)
            request.addParameter("strict", String.valueOf(strict));
        if (order != null)
            request.addParameter("order", order.name());
        T searchResult = null;
        try(Response response = requestExecutor.execute(request)) {
            String body = response.getBody();
            searchResult = serializer.fromJson(body, responseType);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    private <T> PartialSearchResponse<T> abstractSearch(
            Type responseType, String query, Boolean strict, SearchOrder order, String searchPlace, int top, boolean getTop) {
        OAuthRequest request = new OAuthRequest(Verb.GET,
                String.format("%s/%s", API_URL_PREFIX, searchPlace));
        if (query != null)
            request.addParameter("q", query);
        if (strict != null)
            request.addParameter("strict", String.valueOf(strict));
        if (order != null)
            request.addParameter("order", order.name());
        if (getTop && top != -1)
            request.addParameter("top", String.valueOf(top));
        try(Response response = requestExecutor.execute(request)) {
            String body = response.getBody().replace("\"\"", "null");
            return serializer.fromJson(body, responseType);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return new PartialSearchResponse<>(responseType);
    }

    public FullSearchSet search(String query, Boolean strict) {
        return new FullSearchSet(
                searchForAlbums(query, strict, null),
                searchForArtists(query, strict, null),
                searchForPlaylists(query, strict, null),
                searchForTracks(query, strict, null),
                searchForUsers(query, strict, null)
        );
    }

    public PartialSearchResponse<Album> searchForAlbums(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(ALBUM_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, ALBUM_SECTION), -1, false);
    }

    public PartialSearchResponse<Artist> searchForArtists(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(ARTIST_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, ARTIST_SECTION), -1, false);
    }

    public PartialSearchResponse<Playlist> searchForPlaylists(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(PLAYLIST_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, PLAYLIST_SECTION), -1, false);
    }

    public PartialSearchResponse<TrackSearch> searchForTracks(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(TRACKSEARCH_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, TRACK_SECTION), -1, false);
    }

    public PartialSearchResponse<User> searchForUsers(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(USER_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, USER_SECTION), -1, false);
    }

    public PartialSearchResponse<Album> getFavoredAlbums(User user, SearchOrder order) {
        return abstractSearch(ALBUM_RESPONSE_TYPE, null, null, order,
                String.format("%s/%d/%s", USER_SECTION, user.id(), ALBUMS_SECTION), -1, false);
    }

    public PartialSearchResponse<Artist> getFavoredArtists(User user, SearchOrder order) {
        return abstractSearch(ARTIST_RESPONSE_TYPE, null, null, order,
                String.format("%s/%d/%s", USER_SECTION, user.id(), ARTISTS_SECTION), -1, false);
    }

    public PartialSearchResponse<Playlist> getFavouredPlaylists(User user, SearchOrder order) {
        return abstractSearch(PLAYLIST_RESPONSE_TYPE, null, order,
                String.format("%s/%d/%s", USER_SECTION, user.id(), PLAYLISTS_SECTION));
    }

    public PartialSearchResponse<Track> getFavouredTracks(User user, SearchOrder order) {
        return abstractSearch(TRACK_RESPONSE_TYPE, null, null, order,
                String.format("%s/%d/%s", USER_SECTION, user.id(), TRACKS_SECTION), -1, false);
    }

    public User getUser(long userId) {
        return abstractSearch(USER_TYPE, null, null, String.format("%s/%d", USER_SECTION, userId));
    }
 }
