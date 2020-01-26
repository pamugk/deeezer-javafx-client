package api.objects.playables;

import api.objects.utils.User;
import api.objects.utils.search.SearchResponse;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
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
    private URL picture;
    private URL picture_small;
    private URL picture_medium;
    private URL picture_big;
    private URL picture_xl;
    private String checksum;
    private User creator;
    private SearchResponse<Track> tracks;

    @Override
    public String toString(){
        return title;
    }
}
