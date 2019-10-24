package api.objects.utils.search;

import api.PartialSearchResponse;
import api.objects.playables.*;
import api.objects.utils.User;

public class FullSearchSet {
    private PartialSearchResponse<Album> albumResponse;
    private PartialSearchResponse<Artist> artistResponse;
    private PartialSearchResponse<Playlist> playlistResponse;
    private PartialSearchResponse<Radio> radioResponse;
    private PartialSearchResponse<TrackSearch> trackResponse;
    private PartialSearchResponse<User> userResponse;

    public PartialSearchResponse<Album> getAlbumResponse() { return albumResponse; }
    public void setAlbumResponse(PartialSearchResponse<Album> albumResponse) { this.albumResponse = albumResponse; }
    public PartialSearchResponse<Artist> getArtistResponse() { return artistResponse; }
    public void setArtistResponse(PartialSearchResponse<Artist> artistResponse) { this.artistResponse = artistResponse; }
    public PartialSearchResponse<Playlist> getPlaylistResponse() { return playlistResponse; }
    public void setPlaylistResponse(PartialSearchResponse<Playlist> playlistResponse) {
        this.playlistResponse = playlistResponse;
    }
    public PartialSearchResponse<Radio> getRadioResponse() { return radioResponse; }
    public void setRadioResponse(PartialSearchResponse<Radio> radioResponse) { this.radioResponse = radioResponse; }
    public PartialSearchResponse<TrackSearch> getTrackResponse() { return trackResponse; }
    public void setTrackResponse(PartialSearchResponse<TrackSearch> trackResponse) { this.trackResponse = trackResponse; }
    public PartialSearchResponse<User> getUserResponse() { return userResponse; }
    public void setUserResponse(PartialSearchResponse<User> userResponse) { this.userResponse = userResponse; }
}
