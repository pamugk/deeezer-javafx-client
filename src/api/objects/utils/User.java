package api.objects.utils;

import java.net.URL;
import java.util.Date;

public class User {
    private int id;
    private String name;
    private String lastname;
    private String firstname;
    private String email;
    private int status;
    private Date birthday;
    private Date inscription_date;
    private String gender;
    private URL link;
    private URL picture;
    private URL picture_small;
    private URL picture_medium;
    private URL picture_big;
    private URL picture_xl;
    private String country;
    private String lang;
    private boolean is_kid;
    private String explicit_content_level;
    private String[] explicit_content_levels_available;
    private URL tracklist;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getInscription_date() {
        return inscription_date;
    }

    public void setInscription_date(Date inscription_date) {
        this.inscription_date = inscription_date;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isIs_kid() {
        return is_kid;
    }

    public void setIs_kid(boolean is_kid) {
        this.is_kid = is_kid;
    }

    public String getExplicit_content_level() {
        return explicit_content_level;
    }

    public void setExplicit_content_level(String explicit_content_level) {
        this.explicit_content_level = explicit_content_level;
    }

    public String[] getExplicit_content_levels_available() {
        return explicit_content_levels_available;
    }

    public void setExplicit_content_levels_available(String[] explicit_content_levels_available) {
        this.explicit_content_levels_available = explicit_content_levels_available;
    }

    public URL getTracklist() {
        return tracklist;
    }

    public void setTracklist(URL tracklist) {
        this.tracklist = tracklist;
    }
}
