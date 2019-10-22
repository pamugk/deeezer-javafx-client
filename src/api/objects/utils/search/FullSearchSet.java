package api.objects.utils.search;

import api.objects.playables.*;
import api.objects.utils.User;

public class FullSearchSet {
    private SearchResponse<Album> albumResponse;
    private SearchResponse<Artist> artistResponse;
    private SearchResponse<Playlist> playlistResponse;
    private SearchResponse<Radio> radioResponse;
    private SearchResponse<TrackSearch> trackResponse;
    private SearchResponse<User> userResponse;

    public SearchResponse<Album> getAlbumResponse() { return albumResponse; }
    public void setAlbumResponse(SearchResponse<Album> albumResponse) { this.albumResponse = albumResponse; }
    public SearchResponse<Artist> getArtistResponse() { return artistResponse; }
    public void setArtistResponse(SearchResponse<Artist> artistResponse) { this.artistResponse = artistResponse; }
    public SearchResponse<Playlist> getPlaylistResponse() { return playlistResponse; }
    public void setPlaylistResponse(SearchResponse<Playlist> playlistResponse) {
        this.playlistResponse = playlistResponse;
    }
    public SearchResponse<Radio> getRadioResponse() { return radioResponse; }
    public void setRadioResponse(SearchResponse<Radio> radioResponse) { this.radioResponse = radioResponse; }
    public SearchResponse<TrackSearch> getTrackResponse() { return trackResponse; }
    public void setTrackResponse(SearchResponse<TrackSearch> trackResponse) { this.trackResponse = trackResponse; }
    public SearchResponse<User> getUserResponse() { return userResponse; }
    public void setUserResponse(SearchResponse<User> userResponse) { this.userResponse = userResponse; }
}
