package api.objects.playables;

import api.objects.DeezerEntity;

import java.net.URL;

public class Artist extends Playable {
    private String name;
    private URL link;
    private URL share;
    private URL picture;
    private URL picture_small;
    private URL picture_medium;
    private URL picture_big;
    private URL picture_xl;
    private int nb_album;
    private int nb_fan;
    private boolean radio;
    private URL tracklist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getNb_album() {
        return nb_album;
    }

    public void setNb_album(int nb_album) {
        this.nb_album = nb_album;
    }

    public int getNb_fan() {
        return nb_fan;
    }

    public void setNb_fan(int nb_fan) {
        this.nb_fan = nb_fan;
    }

    public boolean isRadio() {
        return radio;
    }

    public void setRadio(boolean radio) {
        this.radio = radio;
    }

    public URL getTracklist() {
        return tracklist;
    }

    public void setTracklist(URL tracklist) {
        this.tracklist = tracklist;
    }
}
