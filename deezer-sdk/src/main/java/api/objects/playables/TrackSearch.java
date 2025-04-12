package api.objects.playables;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public record TrackSearch(
        long id,
        String type,
        URL link,
        URL share,
        boolean readable,
        String title,
        @SerializedName("title_short")
        String titleShort,
        @SerializedName("title_version")
        String titleVersion,
        int duration,
        int rank,
        @SerializedName("explicit_lyrics")
        boolean explicitLyrics,
        String preview,
        Artist artist,
        Album album
) {
    @Override
    public String toString() {
        return title;
    }
}
