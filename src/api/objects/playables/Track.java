package api.objects.playables;

import api.objects.utils.Contributor;

import java.util.Date;
import java.util.List;

public class Track extends TrackSearch {
    private boolean unseen;
    private String isrc;
    private int track_position;
    private int disk_number;
    private Date release_date;
    private int explicit_content_lyrics;
    private int explicit_content_cover;
    private float bpm;
    private float gain;
    private List<String> available_countries;
    private Track alternative;
    private List<Contributor> contributors;

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
    public Date getRelease_date() {
        return release_date;
    }
    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
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
}
