package api.objects.playables;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public record TrackSearch(
        long id,
        String type,
        URL link,
        URL share,
        boolean readable,
        String title,
        @JsonProperty("title_short")
        String titleShort,
        @JsonProperty("title_version")
        String titleVersion,
        int duration,
        int rank,
        @JsonProperty("explicit_lyrics")
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
