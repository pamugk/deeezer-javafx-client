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
        @SerializedName("is_loved_track")
        boolean lovedTrack,
        boolean collaborative,
        int rating,
        @SerializedName("nb_tracks")
        int trackCount,
        @SerializedName("unseen_track_count")
        int unseenTrackCount,
        int fans,
        URL picture,
        @SerializedName("picture_small")
        URL pictureSmall,
        @SerializedName("picture_medium")
        URL pictureMedium,
        @SerializedName("picture_big")
        URL pictureBig,
        @SerializedName("picture_xl")
        URL pictureXl,
        String checksum,
        User creator,
        SearchResponse<Track> tracks
) {
    @Override
    public String toString(){
        return title;
    }
}
