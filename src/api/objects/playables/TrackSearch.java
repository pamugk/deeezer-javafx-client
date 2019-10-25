package api.objects.playables;

import java.net.URL;

public class TrackSearch extends Playable {
    private boolean readable;
    private String title;
    private String title_short;
    private String title_version;
    private int duration;
    private int rank;
    private boolean explicit_lyrics;
    private String preview;
    private Artist artist;
    private Album album;

    public boolean isReadable() {
        return readable;
    }
    public void setReadable(boolean readable) {
        this.readable = readable;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle_short() {
        return title_short;
    }
    public void setTitle_short(String title_short) {
        this.title_short = title_short;
    }
    public String getTitle_version() {
        return title_version;
    }
    public void setTitle_version(String title_version) {
        this.title_version = title_version;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public boolean isExplicit_lyrics() {
        return explicit_lyrics;
    }
    public void setExplicit_lyrics(boolean explicit_lyrics) {
        this.explicit_lyrics = explicit_lyrics;
    }
    public String getPreview() {
        return preview;
    }
    public void setPreview(String preview) {
        this.preview = preview;
    }
    public Artist getArtist() {
        return artist;
    }
    public void setArtist(Artist artist) {
        this.artist = artist;
    }
    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return title;
    }
}