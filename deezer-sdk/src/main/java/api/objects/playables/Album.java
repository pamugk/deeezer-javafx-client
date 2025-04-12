package api.objects.playables;

import api.objects.utils.Contributor;
import api.objects.utils.Genre;
import api.objects.utils.search.SearchResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public record Album(
        long id,
        String type,
        URL link,
        URL share,
        String title,
        String upc,
        URL cover,
        @JsonProperty("cover_small")
        URL coverSmall,
        @JsonProperty("cover_medium")
        URL coverMedium,
        @JsonProperty("cover_big")
        URL coverBig,
        @JsonProperty("cover_xl")
        URL coverXl,
        @JsonProperty("genre_id")
        long genreId,
        SearchResponse<Genre> genres,
        String label,
        @JsonProperty("nb_tracks")
        int trackCount,
        int duration,
        int fans,
        int rating,
        @JsonProperty("release_date")
        LocalDate releaseDate,
        @JsonProperty("record_type")
        String recordType,
        boolean available,
        Album alternative,
        URL trackList,
        @JsonProperty("explicit_lyrics")
        boolean explicitLyrics,
        @JsonProperty("explicit_content_lyrics")
        int explicitContentLyrics,
        @JsonProperty("explicit_content_cover")
        int explicitContentCover,
        List<Contributor> contributors,
        Artist artist,
        SearchResponse<Track> tracks
) {
    @Override
    public String toString() {
        return title;
    }
}
