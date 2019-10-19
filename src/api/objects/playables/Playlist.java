package api.objects.playables;

import api.objects.DeezerEntity;
import com.google.gson.annotations.SerializedName;
import api.objects.utils.User;

import java.net.URL;
import java.util.List;

public class Playlist extends Playable {
    private String title;
    private String description;
    private int duration;
    @SerializedName("public")
    private boolean _public;
    private boolean is_loved_track;
    private boolean collaborative;
    private int rating;
    private int nb_tracks;
    private int unseen_track_count;
    private int fans;
    private URL link;
    private URL share;
    private URL picture;
    private URL picture_small;
    private URL picture_medium;
    private URL picture_big;
    private URL picture_xl;
    private String checksum;
    private User creator;
    private List<Track> tracks;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean is_public() {
        return _public;
    }

    public void set_public(boolean _public) {
        this._public = _public;
    }

    public boolean isIs_loved_track() {
        return is_loved_track;
    }

    public void setIs_loved_track(boolean is_loved_track) {
        this.is_loved_track = is_loved_track;
    }

    public boolean isCollaborative() {
        return collaborative;
    }

    public void setCollaborative(boolean collaborative) {
        this.collaborative = collaborative;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getNb_tracks() {
        return nb_tracks;
    }

    public void setNb_tracks(int nb_tracks) {
        this.nb_tracks = nb_tracks;
    }

    public int getUnseen_track_count() {
        return unseen_track_count;
    }

    public void setUnseen_track_count(int unseen_track_count) {
        this.unseen_track_count = unseen_track_count;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public URL getShare() {
        return share;
    }

    public void setShare(URL share) {
        this.share = share;
    }

    public URL getPicture() {
        return picture;
    }

    public void setPicture(URL picture) {
        this.picture = picture;
    }

    public URL getPicture_small() {
        return picture_small;
    }

    public void setPicture_small(URL picture_small) {
        this.picture_small = picture_small;
    }

    public URL getPicture_medium() {
        return picture_medium;
    }

    public void setPicture_medium(URL picture_medium) {
        this.picture_medium = picture_medium;
    }

    public URL getPicture_big() {
        return picture_big;
    }

    public void setPicture_big(URL picture_big) {
        this.picture_big = picture_big;
    }

    public URL getPicture_xl() {
        return picture_xl;
    }

    public void setPicture_xl(URL picture_xl) {
        this.picture_xl = picture_xl;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
