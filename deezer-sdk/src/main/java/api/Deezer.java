package api;

import api.events.authentication.AuthenticationEvent;
import api.events.handlers.DeezerEventHandler;
import api.net.Permissions;
import api.net.DeezerRequestExecutor;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.User;
import api.objects.utils.search.FullSearchSet;
import api.objects.utils.search.PartialSearchResponse;
import api.objects.utils.search.SearchOrder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class Deezer {
    private final DeezerRequestExecutor requestExecutor;
    private final DeezerEventHandler<AuthenticationEvent> authenticationEventHandler;
    private Long currentUserId;

    private final ObjectMapper serializer;
    private final JavaType ALBUM_RESPONSE_TYPE;
    private final JavaType ARTIST_RESPONSE_TYPE;
    private final JavaType COMMENT_RESPONSE_TYPE;
    private final JavaType PLAYLIST_RESPONSE_TYPE;
    private final JavaType TRACK_RESPONSE_TYPE;
    private final JavaType TRACKSEARCH_RESPONSE_TYPE;
    private final JavaType USER_RESPONSE_TYPE;

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

    public Deezer(Configuration configuration) throws IOException, NoSuchPaddingException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        authenticationEventHandler = new DeezerEventHandler<>();
        serializer = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        ALBUM_RESPONSE_TYPE = serializer.getTypeFactory().constructParametricType(PartialSearchResponse.class, Album.class);
        ARTIST_RESPONSE_TYPE = serializer.getTypeFactory().constructParametricType(PartialSearchResponse.class, Artist.class);
        COMMENT_RESPONSE_TYPE = serializer.getTypeFactory().constructParametricType(PartialSearchResponse.class, Comment.class);
        PLAYLIST_RESPONSE_TYPE = serializer.getTypeFactory().constructParametricType(PartialSearchResponse.class, Playlist.class);
        TRACK_RESPONSE_TYPE = serializer.getTypeFactory().constructParametricType(PartialSearchResponse.class, Track.class);
        TRACKSEARCH_RESPONSE_TYPE = serializer.getTypeFactory().constructParametricType(PartialSearchResponse.class, TrackSearch.class);
        USER_RESPONSE_TYPE = serializer.getTypeFactory().constructParametricType(PartialSearchResponse.class, User.class);

        requestExecutor = new DeezerRequestExecutor(configuration.callbackContext(), configuration.apiKey(),
                configuration.apiSecret(), Arrays.asList(Permissions.values()));
        requestExecutor.setAuthenticationEventHandler(authenticationEventHandler);
    }

    public void stop() { requestExecutor.stop(); }

    public Album getAlbum(long albumId) {
        return abstractGet(Album.class, String.format("%s/%d", ALBUM_SECTION, albumId));
    }

    public PartialSearchResponse<Track> getAlbumTracks(Album album) {
        return abstractSearch(TRACK_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", ALBUM_SECTION, album.id(), TRACKS_SECTION), 25, false);
    }

    public Artist getArtist(long artistId) {
        return abstractGet(Artist.class, String.format("%s/%d", ARTIST_SECTION, artistId));
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
            throw new RuntimeException(e);
        }
    }

    public void logout() {
        currentUserId = null;
        requestExecutor.tearStorageDown();
        authenticationEventHandler.invoke(new AuthenticationEvent(false));
    }

    public User getLoggedInUser(){
        User loggedInUser;
        OAuthRequest request = new OAuthRequest(Verb.GET, String.format("%s/%s/me", API_URL_PREFIX, USER_SECTION));
        try(Response response = requestExecutor.execute(request)) {
            loggedInUser = serializer.readValue(response.getBody(), User.class);
            if (currentUserId == null)
                currentUserId = loggedInUser.id();
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        return loggedInUser;
    }

    public LoginStatus getLoginStatus() {
        if (!requestExecutor.isAuthorised())
            return LoginStatus.NOT_AUTHORIZED;
        return LoginStatus.UNKNOWN;
    }

    public Playlist getPlaylist(long playlistId) {
        return abstractGet(Playlist.class, String.format("%s/%d", PLAYLIST_SECTION, playlistId));
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
        return abstractGet(User.class, String.format("%s/%d", USER_SECTION, userId));
    }

    private <T> T abstractGet(Class<T> responseType, String searchPlace) {
        OAuthRequest request = new OAuthRequest(Verb.GET,
                String.format("%s/%s", API_URL_PREFIX, searchPlace));
        try (Response response = requestExecutor.execute(request)) {
            return serializer.readValue(response.getBody(), responseType);
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T abstractSearch(
            JavaType responseType, Boolean strict, SearchOrder order, String searchPlace) {
        OAuthRequest request = new OAuthRequest(Verb.GET,
                String.format("%s/%s", API_URL_PREFIX, searchPlace));
        if (strict != null) {
            request.addParameter("strict", String.valueOf(strict));
        }
        if (order != null) {
            request.addParameter("order", order.name());
        }
        try (Response response = requestExecutor.execute(request)) {
            return serializer.readValue(response.getBody(), responseType);
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> PartialSearchResponse<T> abstractSearch(
            JavaType responseType, String query, Boolean strict, SearchOrder order, String searchPlace, int top, boolean getTop) {
        OAuthRequest request = new OAuthRequest(Verb.GET,
                String.format("%s/%s", API_URL_PREFIX, searchPlace));
        if (query != null)
            request.addParameter("q", query);
        if (strict != null) {
            request.addParameter("strict", String.valueOf(strict));
        }
        if (order != null) {
            request.addParameter("order", order.name());
        }
        if (getTop && top != -1) {
            request.addParameter("top", String.valueOf(top));
        }
        try (Response response = requestExecutor.execute(request)) {
            return serializer.readValue(response.getBody(), responseType);
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
 }
