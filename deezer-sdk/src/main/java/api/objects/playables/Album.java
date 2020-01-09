package api.objects.playables;

import api.objects.utils.Contributor;
import api.objects.utils.Genre;
import api.objects.utils.search.SearchResponse;

import java.net.URL;
import java.util.Date;
import java.util.List;

public class Album extends Playable {
    private String title;
    private String upc;
    private URL cover;
    private URL cover_small;
    private URL cover_medium;
    private URL cover_big;
    private URL cover_xl;
    private long genre_id;
    private SearchResponse<Genre> genres;
    private String label;
    private int nb_tracks;
    private int duration;
    private int fans;
    private int rating;
    private Date release_date;
    private String record_type;
    private boolean available;
    private Album alternative;
    private URL tracklist;
    private boolean explicit_lyrics;
    private int explicit_content_lyrics;
    private int explicit_content_cover;
    private List<Contributor> contributors;
    private Artist artist;
    private SearchResponse<Track> tracks;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) { this.title = title; }
    public String getUpc() {
        return upc;
    }
    public void setUpc(String upc) {
        this.upc = upc;
    }
    public URL getCover() {
        return cover;
    }
    public void setCover(URL cover) {
        this.cover = cover;
    }
    public URL getCover_small() {
        return cover_small;
    }
    public void setCover_small(URL cover_small) {
        this.cover_small = cover_small;
    }
    public URL getCover_medium() {
        return cover_medium;
    }
    public void setCover_medium(URL cover_medium) {
        this.cover_medium = cover_medium;
    }
    public URL getCover_big() {
        return cover_big;
    }
    public void setCover_big(URL cover_big) {
        this.cover_big = cover_big;
    }
    public URL getCover_xl() {
        return cover_xl;
    }
    public void setCover_xl(URL cover_xl) {
        this.cover_xl = cover_xl;
    }
    public long getGenre_id() {
        return genre_id;
    }
    public void setGenre_id(long genre_id) {
        this.genre_id = genre_id;
    }
    public SearchResponse<Genre> getGenres() {
        return genres;
    }
    public void setGenres(SearchResponse<Genre> genres) {
        this.genres = genres;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public int getNb_tracks() {
        return nb_tracks;
    }
    public void setNb_tracks(int nb_tracks) {
        this.nb_tracks = nb_tracks;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getFans() {
        return fans;
    }
    public void setFans(int fans) {
        this.fans = fans;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public Date getRelease_date() {
        return release_date;
    }
    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }
    public String getRecord_type() {
        return record_type;
    }
    public void setRecord_type(String record_type) {
        this.record_type = record_type;
    }
    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public Album getAlternative() {
        return alternative;
    }
    public void setAlternative(Album alternative) {
        this.alternative = alternative;
    }
    public URL getTracklist() {
        return tracklist;
    }
    public void setTracklist(URL tracklist) {
        this.tracklist = tracklist;
    }
    public boolean isExplicit_lyrics() {
        return explicit_lyrics;
    }
    public void setExplicit_lyrics(boolean explicit_lyrics) {
        this.explicit_lyrics = explicit_lyrics;
    }
    public int getExplicit_content_lyrics() {
        return explicit_content_lyrics;
    }
    public void setExplicit_content_lyrics(int explicit_content_lyrics) {
        this.explicit_content_lyrics = explicit_content_lyrics;
    }
    public int getExplicit_content_cover() {
        return explicit_content_cover;
    }
    public void setExplicit_content_cover(int explicit_content_cover) {
        this.explicit_content_cover = explicit_content_cover;
    }
    public Artist getArtist() {
        return artist;
    }
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    public SearchResponse<Track> getTracks() {
        return tracks;
    }
    public void setTracks(SearchResponse<Track> tracks) {
        this.tracks = tracks;
    }
    public List<Contributor> getContributors() {
        return contributors;
    }
    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    @Override
    public String toString() {
        return title;
    }
}
