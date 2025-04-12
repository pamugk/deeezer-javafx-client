package api.objects.playables;

import api.objects.utils.Contributor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;
import java.util.List;

public record Track(
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
        Album album,
        boolean unseen,
        String isrc,
        @JsonProperty("track_position")
        int trackPosition,
        @JsonProperty("disk_number")
        int diskNumber,
        @JsonProperty("explicit_content_lyrics")
        int explicitContentLyrics,
        @JsonProperty("explicit_content_cover")
        int explicitContentCover,
        float bpm,
        float gain,
        @JsonProperty("available_countries")
        List<String> availableCountries,
        Track alternative,
        List<Contributor> contributors
) {
    @Override
    public String toString() {
        return title;
    }
}
