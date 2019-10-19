package api.objects.playables;

import api.objects.DeezerEntity;

import java.net.URL;

public class Radio extends Playable {
    private String title;
    private String description;
    private URL share;
    private URL picture;
    private URL picture_small;
    private URL picture_medium;
    private URL picture_big;
    private URL picture_xl;
    private URL tracklist;

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

    public URL getTracklist() {
        return tracklist;
    }

    public void setTracklist(URL tracklist) {
        this.tracklist = tracklist;
    }
}
