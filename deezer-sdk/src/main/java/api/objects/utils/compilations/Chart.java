package api.objects.utils.compilations;

import api.objects.DeezerEntity;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.Playlist;
import api.objects.playables.Track;
import api.objects.utils.search.SearchResponse;

public class Chart extends DeezerEntity {
    private SearchResponse<Track> tracks;
    private SearchResponse<Album> albums;
    private SearchResponse<Artist> artists;
    private SearchResponse<Playlist> playlists;

    public SearchResponse<Track> getTracks() {
        return tracks;
    }
    public void setTracks(SearchResponse<Track> tracks) {
        this.tracks = tracks;
    }
    public SearchResponse<Album> getAlbums() {
        return albums;
    }
    public void setAlbums(SearchResponse<Album> albums) {
        this.albums = albums;
    }
    public SearchResponse<Artist> getArtists() {
        return artists;
    }
    public void setArtists(SearchResponse<Artist> artists) {
        this.artists = artists;
    }
    public SearchResponse<Playlist> getPlaylists() {
        return playlists;
    }
    public void setPlaylists(SearchResponse<Playlist> playlists) {
        this.playlists = playlists;
    }
}
