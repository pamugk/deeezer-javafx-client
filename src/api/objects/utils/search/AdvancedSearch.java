package api.objects.utils.search;

public class AdvancedSearch {
    private String artist;
    private String album;
    private String track;
    private String label;
    private int dur_min;
    private int dur_max;
    private int bpm_min;
    private int bpm_max;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getDur_min() {
        return dur_min;
    }

    public void setDur_min(int dur_min) {
        this.dur_min = dur_min;
    }

    public int getDur_max() {
        return dur_max;
    }

    public void setDur_max(int dur_max) {
        this.dur_max = dur_max;
    }

    public int getBpm_min() {
        return bpm_min;
    }

    public void setBpm_min(int bpm_min) {
        this.bpm_min = bpm_min;
    }

    public int getBpm_max() {
        return bpm_max;
    }

    public void setBpm_max(int bpm_max) {
        this.bpm_max = bpm_max;
    }
}
