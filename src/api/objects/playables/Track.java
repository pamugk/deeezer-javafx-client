package api.objects.playables;

import api.objects.utils.Contributor;

import java.net.URL;
import java.util.Date;
import java.util.List;

public class Track extends Playable {
    private boolean readable;
    private String title;
    private String title_short;
    private String title_version;
    private boolean unseen;
    private String isrc;
    private int duration;
    private int track_position;
    private int disk_number;
    private int rank;
    private Date release_date;
    private boolean explicit_lyrics;
    private int explicit_content_lyrics;
    private int explicit_content_cover;
    private URL preview;
    private float bpm;
    private float gain;
    private List<String> available_countries;
    private Track alternative;
    private List<Contributor> contributors;
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
    public boolean isUnseen() {
        return unseen;
    }
    public void setUnseen(boolean unseen) {
        this.unseen = unseen;
    }
    public String getIsrc() {
        return isrc;
    }
    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getTrack_position() {
        return track_position;
    }
    public void setTrack_position(int track_position) {
        this.track_position = track_position;
    }
    public int getDisk_number() {
        return disk_number;
    }
    public void setDisk_number(int disk_number) {
        this.disk_number = disk_number;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public Date getRelease_date() {
        return release_date;
    }
    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
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

    public URL getPreview() {
        return preview;
    }
    public void setPreview(URL preview) {
        this.preview = preview;
    }
    public float getBpm() {
        return bpm;
    }
    public void setBpm(float bpm) {
        this.bpm = bpm;
    }
    public float getGain() {
        return gain;
    }
    public void setGain(float gain) {
        this.gain = gain;
    }
    public List<String> getAvailable_countries() {
        return available_countries;
    }
    public void setAvailable_countries(List<String> available_countries) {
        this.available_countries = available_countries;
    }

    public Track getAlternative() {
        return alternative;
    }
    public void setAlternative(Track alternative) {
        this.alternative = alternative;
    }
    public List<Contributor> getContributors() {
        return contributors;
    }
    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
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
}
