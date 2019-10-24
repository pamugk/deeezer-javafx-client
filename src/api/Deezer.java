package api;

import api.events.authentication.AuthenticationEvent;
import api.events.base.DeezerEventHandler;
import api.net.Permissions;
import api.net.DeezerRequestExecutor;
import api.objects.comments.Comment;
import api.objects.playables.*;
import api.objects.utils.Genre;
import api.objects.utils.User;
import api.objects.utils.compilations.Chart;
import api.objects.utils.compilations.Editorial;
import api.objects.utils.search.FullSearchSet;
import api.objects.utils.search.SearchOrder;
import api.objects.utils.search.SearchResponse;
import api.objects.utils.service.Infos;
import api.objects.utils.service.Options;
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

    private static final Type ALBUM_TYPE = new TypeToken<Album>(){}.getType();
    private static final Type ARTIST_TYPE = new TypeToken<Artist>(){}.getType();
    private static final Type ALBUM_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Album>>(){}.getType();
    private static final Type ARTIST_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Artist>>(){}.getType();
    private static final Type CHART_TYPE = new TypeToken<Chart>(){}.getType();
    private static final Type COMMENT_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Comment>>(){}.getType();
    private static final Type EDITORIAL_TYPE = new TypeToken<Editorial>(){}.getType();
    private static final Type EDITORIAL_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Editorial>>(){}.getType();
    private static final Type GENRE_TYPE = new TypeToken<Genre>(){}.getType();
    private static final Type GENRE_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Genre>>(){}.getType();
    private static final Type INFOS_TYPE = new TypeToken<Infos>(){}.getType();
    private static final Type OPTIONS_TYPE = new TypeToken<Options>(){}.getType();
    private static final Type PLAYLIST_TYPE = new TypeToken<Playlist>(){}.getType();
    private static final Type PLAYLIST_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Playlist>>(){}.getType();
    private static final Type RADIO_TYPE = new TypeToken<Radio>(){}.getType();
    private static final Type RADIO_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Radio>>(){}.getType();
    private static final Type TRACK_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<Track>>(){}.getType();
    private static final Type TRACKSEARCH_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<TrackSearch>>(){}.getType();
    private static final Type USER_TYPE = new TypeToken<User>(){}.getType();
    private static final Type USER_RESPONSE_TYPE = new TypeToken<PartialSearchResponse<User>>(){}.getType();

    private static final String API_URL_PREFIX = "https://api.deezer.com";

    private static final String ALBUM_SECTION = "album";
    private static final String ALBUMS_SECTION = "albums";
    private static final String ARTIST_SECTION = "artist";
    private static final String ARTISTS_SECTION = "artists";
    private static final String CHART_SECTION = "chart";
    private static final String CHARTS_SECTION = "charts";
    private static final String COMMENT_SECTION = "comment";
    private static final String COMMENTS_SECTION = "comments";
    private static final String EDITORIAL_SECTION = "editorial";
    private static final String GENRE_SECTION = "genre";
    private static final String FANS_SECTION = "fans";
    private static final String INFOS_SECTION = "infos";
    private static final String OPTIONS_SECTION = "options";
    private static final String PLAYLIST_SECTION = "playlist";
    private static final String PLAYLISTS_SECTION = "playlists";
    private static final String RADIO_SECTION = "radio";
    private static final String RADIOS_SECTION = "radios";
    private static final String RECOMMENDATIONS_SECTION = "recommendations";
    private static final String SEARCH_SECTION = "search";
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

    public Album getAlbum(long albumId) {
        Album album = abstractSearch(ALBUM_TYPE, null, null,
                String.format("%s/%d", ALBUM_SECTION, albumId));
        album.getTracks().getData().forEach(track -> track.setAlbum(album));
        return album;
    }

    public PartialSearchResponse<Comment> getAlbumComments(Album album) {
        return abstractSearch(COMMENT_RESPONSE_TYPE,  null, null, null,
                String.format("%s/%d/%s", ALBUM_SECTION, album.getId(), COMMENTS_SECTION), -1);
    }

    public PartialSearchResponse<User> getAlbumFans(Album album) {
        return abstractSearch(USER_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", ALBUM_SECTION, album.getId(), FANS_SECTION), -1);
    }

    public PartialSearchResponse<Track> getAlbumTracks(Album album) {
        PartialSearchResponse<Track> response = abstractSearch(TRACK_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", ALBUM_SECTION, album.getId(), TRACKS_SECTION), -1);
        response.getData().forEach(track -> track.setAlbum(album));
        return response;
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

    public Artist getArtist(long artistId) {
        return abstractSearch(ARTIST_TYPE, null, null,
                String.format("%s/%d", ARTIST_SECTION, artistId));
    }

    public PartialSearchResponse<Comment> getArtistComments(Artist artist) {
        return abstractSearch(COMMENT_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.getId(), COMMENTS_SECTION), -1);
    }

    public PartialSearchResponse<Album> getArtistDiscography(Artist artist) {
        PartialSearchResponse<Album> response = abstractSearch(ALBUM_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.getId(), ALBUMS_SECTION), -1);
        response.getData().forEach(album -> album.setArtist(artist));
        return response;
    }

    public PartialSearchResponse<User> getArtistFans(Artist artist) {
        return abstractSearch(USER_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.getId(), FANS_SECTION), -1);
    }

    public PartialSearchResponse<Playlist> getArtistPlaylists(Artist artist, int limit) {
        return abstractSearch(PLAYLIST_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.getId(), PLAYLISTS_SECTION), limit);
    }

    public PartialSearchResponse<Artist> getArtistRelated(Artist artist, int limit) {
        return abstractSearch(ARTIST_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.getId(), "related"), limit);
    }

    public PartialSearchResponse<Radio> getArtistRadio(Artist artist, int limit) {
        return abstractSearch(RADIO_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/%s", ARTIST_SECTION, artist.getId(), RADIO_SECTION), limit);
    }

    public PartialSearchResponse<TrackSearch> getArtistTop(Artist artist, int limit) {
        return abstractSearch(TRACKSEARCH_RESPONSE_TYPE,
                null, null, null,
                String.format("%s/%d/top", ARTIST_SECTION, artist.getId()), limit);
    }

    public void removeArtistFromFavorites(Artist artist) {
        try {
            Response response = changePlayableFavour(artist, Verb.DELETE, ARTISTS_SECTION);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Chart management">
    public Chart getChart(long chartId) {
        return abstractSearch(CHART_TYPE, null, null,
                String.format("%s/%d", CHART_SECTION, chartId));
    }

    public PartialSearchResponse<Album> getChartAlbums(Chart chart) {
        return abstractSearch(ALBUM_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", CHART_SECTION, chart.getId(), ALBUMS_SECTION), -1);
    }

    public PartialSearchResponse<Artist> getChartArtists(Chart chart) {
        return abstractSearch(ARTIST_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", CHART_SECTION, chart.getId(), ARTISTS_SECTION), -1);
    }

    public PartialSearchResponse<Playlist> getChartPlaylists(Chart chart) {
        return abstractSearch(PLAYLIST_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", CHART_SECTION, chart.getId(), PLAYLISTS_SECTION), -1);
    }

    public PartialSearchResponse<Track> getChartTracks(Chart chart) {
        return abstractSearch(TRACK_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", CHART_SECTION, chart.getId(), TRACKS_SECTION), -1);
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
    //<editor-fold defaultstate="collapsed" desc="Editorial management">
    public Editorial getEditorial(long editorialId) {
        return abstractSearch(EDITORIAL_TYPE, null, null,
                String.format("%s/%d", EDITORIAL_SECTION, editorialId));
    }

    public Chart getEditorialCharts(Editorial editorial) {
        return abstractSearch(CHART_TYPE, null, null,
                String.format("%s/%d/%s", EDITORIAL_SECTION, editorial.getId(), CHARTS_SECTION));
    }

    public PartialSearchResponse<Editorial> getEditorials() {
        return abstractSearch(EDITORIAL_RESPONSE_TYPE, null, null, null, EDITORIAL_SECTION, -1);
    }

    public PartialSearchResponse<Album> getEditorialReleases(Editorial editorial) {
        return abstractSearch(CHART_TYPE, null, null, null,
                String.format("%s/%d/releases", EDITORIAL_SECTION, editorial.getId()), -1);
    }

    public PartialSearchResponse<Album> getEditorialSelection(Editorial editorial) {
        return abstractSearch(ALBUM_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/selection", EDITORIAL_SECTION, editorial.getId()), -1);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Event handling">
    public DeezerEventHandler<AuthenticationEvent> getAuthenticationEventHandler() {
        return authenticationEventHandler;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Genres management">
    public Genre getGenre(long genreId) {
        return abstractSearch(GENRE_TYPE, null, null,
                String.format("%s/%d", GENRE_SECTION, genreId));
    }

    public PartialSearchResponse<Artist> getGenreArtists(Genre genre) {
       return abstractSearch(ARTIST_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", GENRE_SECTION, genre.getId(), ARTISTS_SECTION), -1);
    }

    public PartialSearchResponse<Radio> getGenreRadios(Genre genre) {
        return abstractSearch(RADIO_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", GENRE_SECTION, genre.getId(), RADIOS_SECTION), -1);
    }

    public PartialSearchResponse<Genre> getGenres() {
        return abstractSearch(GENRE_RESPONSE_TYPE, null, null, null, GENRE_SECTION, -1);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Infos management">
    public Infos getInfos() {
        return abstractSearch(INFOS_TYPE, null, null, INFOS_SECTION);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Library management">
    private Response changePlayableFavour(Playable playable, Verb action, String section)
            throws InterruptedException, ExecutionException, IOException {
        OAuthRequest request = new OAuthRequest(action,
                String.format("%s/%s/%d/%s", API_URL_PREFIX, USER_SECTION, currentUserId, section));
        request.addParameter(String.format("%s_id", playable.getType()), String.valueOf(playable.getId()));
        return requestExecutor.execute(request, true);
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
        if (currentUserId == null)
            return LoginStatus.NOT_AUTHORIZED;
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

    public void addTracksToPlaylist(Playlist playlist, List<TrackSearch> songs) {
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

    public Playlist getPlaylist(long playlistId) {
        return abstractSearch(PLAYLIST_TYPE, null, null,
                String.format("%s/%d", PLAYLIST_SECTION, playlistId));
    }

    public PartialSearchResponse<Comment> getPlaylistComments(Playlist playlist) {
        return abstractSearch(COMMENT_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", PLAYLIST_SECTION, playlist.getId(), COMMENTS_SECTION), -1);
    }

    public PartialSearchResponse<User> getPlaylistFans(Playlist playlist) {
        return abstractSearch(USER_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", PLAYLIST_SECTION, playlist.getId(), FANS_SECTION), -1);
    }

    public Boolean getPlaylistRadio(Playlist playlist) {
        return abstractSearch(Boolean.TYPE, null, null,
                String.format("%s/%d/%s", PLAYLIST_SECTION, playlist.getId(), RADIO_SECTION));
    }

    public PartialSearchResponse<TrackSearch> getPlaylistTracks(Playlist playlist) {
        return abstractSearch(TRACKSEARCH_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s", PLAYLIST_SECTION, playlist.getId(), TRACKS_SECTION), -1);
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

    public void orderTracksInPlaylist(Playlist playlist, List<TrackSearch> order) {
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

    public void removeTracksFromPlaylist(Playlist playlist, List<TrackSearch> songs) {
        try {
            Response response = updatePlaylist(playlist, songs, "songs", Verb.DELETE);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
    }

    private Response updatePlaylist(Playlist playlist, List<TrackSearch> songs, String parameter, Verb action)
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

    public Radio getRadio(long radioId) {
        return abstractSearch(RADIO_TYPE, null, null,
                String.format("%s/%d", RADIO_SECTION, radioId));
    }

    public SearchResponse<Radio> getRadios() {
        return abstractSearch(RADIO_RESPONSE_TYPE, null, null, null,
                String.format("%s/%s", RADIO_SECTION, "lists"), -1);
    }

    public SearchResponse<Radio> getTopRadios(int top) {
        return abstractSearch(COMMENT_RESPONSE_TYPE, null, null, null,
                String.format("%s/%s", RADIO_SECTION, "top"), top);
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
    //<editor-fold defaultstate="collapsed" desc="Recommendations management">
    public PartialSearchResponse<Album> getRecommendedAlbums(int count) {
        return abstractSearch(ALBUM_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s/%s", USER_SECTION, currentUserId, RECOMMENDATIONS_SECTION, ALBUMS_SECTION), count);
    }

    public PartialSearchResponse<Album> getRecommendedArtists(int count) {
        return abstractSearch(ARTIST_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s/%s", USER_SECTION, currentUserId, RECOMMENDATIONS_SECTION, ARTISTS_SECTION),
                count);
    }

    public PartialSearchResponse<Playlist> getRecommendedPlaylists(int count) {
        return abstractSearch(PLAYLIST_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s/%s", USER_SECTION, currentUserId, RECOMMENDATIONS_SECTION, PLAYLISTS_SECTION),
                count);
    }

    public PartialSearchResponse<Radio> getRecommendedRadios(int count) {
        return abstractSearch(RADIO_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s/%s", USER_SECTION, currentUserId, RECOMMENDATIONS_SECTION, RADIOS_SECTION),
                count);
    }

    public PartialSearchResponse<TrackSearch> getRecommendedTracks(int count) {
        return abstractSearch(TRACKSEARCH_RESPONSE_TYPE, null, null, null,
                String.format("%s/%d/%s/%s", USER_SECTION, currentUserId, RECOMMENDATIONS_SECTION, TRACKS_SECTION),
                count);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Search">
    private <T> T abstractSearch(
            Type responseType, Boolean strict, SearchOrder order, String searchPlace) {
        OAuthRequest request = new OAuthRequest(Verb.GET,
                String.format("%s/%s", API_URL_PREFIX, searchPlace));
        if (strict != null)
            request.addParameter("strict", String.valueOf(strict));
        if (order != null)
            request.addParameter("order", order.name());
        T searchResult = null;
        try {
            Response response = requestExecutor.execute(request, currentUserId != null);
            String body = response.getBody();
            searchResult = new Gson().fromJson(body, responseType);
        } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    private <T> PartialSearchResponse<T> abstractSearch(
            Type responseType, String query, Boolean strict, SearchOrder order, String searchPlace, int top) {
        OAuthRequest request = new OAuthRequest(Verb.GET,
                String.format("%s/%s", API_URL_PREFIX, searchPlace));
        if (query != null)
            request.addParameter("q", query);
        if (strict != null)
            request.addParameter("strict", String.valueOf(strict));
        if (order != null)
            request.addParameter("order", order.name());
        PartialSearchResponse<T> searchResult = new PartialSearchResponse<>();
        do
            try {
                Response response = requestExecutor.execute(
                        searchResult.getPrev() ==  null ? request :
                                new OAuthRequest(Verb.GET, searchResult.getNext().toString()), currentUserId != null);
                String body = response.getBody();
                PartialSearchResponse<T> nextPart = new Gson().fromJson(body, responseType);
                searchResult.setNext(nextPart.getNext());
                searchResult.getData().addAll(nextPart.getData());
            } catch (ExecutionException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
        while ((top == -1 || searchResult.getData().size() < top) && searchResult.getNext() != null);

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

    public PartialSearchResponse<Album> searchForAlbums(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(ALBUM_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, ALBUM_SECTION), 25);
    }

    public PartialSearchResponse<Artist> searchForArtists(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(ARTIST_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, ARTIST_SECTION), 25);
    }

    public PartialSearchResponse<Playlist> searchForPlaylists(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(PLAYLIST_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, PLAYLIST_SECTION), 25);
    }

    public PartialSearchResponse<Radio> searchForRadios(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(RADIO_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, RADIO_SECTION), 25);
    }

    public PartialSearchResponse<TrackSearch> searchForTracks(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(TRACKSEARCH_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, TRACK_SECTION), 25);
    }

    public PartialSearchResponse<User> searchForUsers(String query, Boolean strict, SearchOrder order) {
        return abstractSearch(USER_RESPONSE_TYPE, query, strict, order,
                String.format("%s/%s", SEARCH_SECTION, USER_SECTION), 25);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Track management">
    public boolean addTrackToFavourites(TrackSearch track) {
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

    public boolean removeTrackFromFavourites(TrackSearch track) {
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
    //<editor-fold defaultstate="collapsed" desc="User management">
    public PartialSearchResponse<Album> getFavoredAlbums(SearchOrder order) {
        return abstractSearch(ALBUM_RESPONSE_TYPE, null, null, order,
                String.format("%s/%d/%s", USER_SECTION, currentUserId, ALBUMS_SECTION), -1);
    }

    public PartialSearchResponse<Artist> getFavoredArtists(SearchOrder order) {
        return abstractSearch(ARTIST_RESPONSE_TYPE, null, null, order,
                String.format("%s/%d/%s", USER_SECTION, currentUserId, ARTISTS_SECTION), -1);
    }

    public PartialSearchResponse<Playlist> getFavouredPlaylists(SearchOrder order) {
        return abstractSearch(PLAYLIST_RESPONSE_TYPE, null, order,
                String.format("%s/%d/%s", USER_SECTION, currentUserId, PLAYLISTS_SECTION));
    }

    public PartialSearchResponse<Radio> getFavouredRadios(SearchOrder order) {
        return abstractSearch(RADIO_RESPONSE_TYPE, null, null, order,
                String.format("%s/%d/%s", USER_SECTION, currentUserId, RADIOS_SECTION), -1);
    }

    public PartialSearchResponse<Track> getFavoredTracks(SearchOrder order) {
        return abstractSearch(TRACK_RESPONSE_TYPE, null, order,
                String.format("%s/%d/%s", USER_SECTION, currentUserId, "personal_songs"));
    }

    public PartialSearchResponse<Track> getFlow() {
        return abstractSearch(TRACK_RESPONSE_TYPE, null, null,
                String.format("%s/%d/%s", USER_SECTION, currentUserId, "flow"));
    }

    public PartialSearchResponse<User> getFollowers(SearchOrder order) {
        return abstractSearch(USER_RESPONSE_TYPE, null, null, order,
                String.format("%s/%d/%s", USER_SECTION, currentUserId, "followers"), -1);
    }

    public PartialSearchResponse<User> getFollowings(SearchOrder order) {
        return abstractSearch(USER_RESPONSE_TYPE, null, null, order,
                String.format("%s/%d/%s", USER_SECTION, currentUserId, "followings"), -1);
    }

    public Options getOptions() {
        return abstractSearch(OPTIONS_TYPE, null, null,
                currentUserId == null ?
                        OPTIONS_SECTION :
                        String.format("%s/%d/%s", USER_SECTION, currentUserId, OPTIONS_SECTION));
    }

    public User getUser(long userId) {
        return abstractSearch(USER_TYPE, null, null, String.format("%s/%d", USER_SECTION, userId));
    }
    //</editor-fold>
 }
