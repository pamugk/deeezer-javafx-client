package api.objects.playables;

import api.objects.utils.User;
import api.objects.utils.search.SearchResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public record Playlist(
        long id,
        String type,
        URL link,
        URL share,
        String title,
        String description,
        int duration,
        @JsonProperty("public")
        boolean _public,
        @JsonProperty("is_loved_track")
        boolean lovedTrack,
        boolean collaborative,
        int rating,
        @JsonProperty("nb_tracks")
        int trackCount,
        @JsonProperty("unseen_track_count")
        int unseenTrackCount,
        int fans,
        URL picture,
        @JsonProperty("picture_small")
        URL pictureSmall,
        @JsonProperty("picture_medium")
        URL pictureMedium,
        @JsonProperty("picture_big")
        URL pictureBig,
        @JsonProperty("picture_xl")
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
