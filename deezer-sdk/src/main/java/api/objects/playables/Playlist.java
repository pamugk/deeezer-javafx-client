package api.objects.playables;

import api.objects.utils.User;
import api.objects.utils.search.SearchResponse;
import com.google.gson.annotations.SerializedName;

import java.net.URL;

public record Playlist(
        long id,
        String type,
        URL link,
        URL share,
        String title,
        String description,
        int duration,
        @SerializedName("public")
        boolean _public,
        boolean is_loved_track,
        boolean collaborative,
        int rating,
        int nb_tracks,
        int unseen_track_count,
        int fans,
        URL picture,
        URL picture_small,
        URL picture_medium,
        URL picture_big,
        URL picture_xl,
        String checksum,
        User creator,
        SearchResponse<Track> tracks
) {
    @Override
    public String toString(){
        return title;
    }
}
