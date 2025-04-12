package api.objects.playables;

import api.objects.utils.Contributor;
import api.objects.utils.Genre;
import api.objects.utils.search.SearchResponse;
import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.Date;
import java.util.List;

public record Album(
        long id,
        String type,
        URL link,
        URL share,
        String title,
        String upc,
        URL cover,
        @SerializedName("cover_small")
        URL coverSmall,
        @SerializedName("cover_medium")
        URL coverMedium,
        @SerializedName("cover_big")
        URL coverBig,
        @SerializedName("cover_xl")
        URL coverXl,
        @SerializedName("genre_id")
        long genreId,
        SearchResponse<Genre> genres,
        String label,
        @SerializedName("nb_tracks")
        int trackCount,
        int duration,
        int fans,
        int rating,
        @SerializedName("release_date")
        Date releaseDate,
        @SerializedName("record_type")
        String recordType,
        boolean available,
        Album alternative,
        URL trackList,
        @SerializedName("explicit_lyrics")
        boolean explicitLyrics,
        @SerializedName("explicit_content_lyrics")
        int explicitContentLyrics,
        @SerializedName("explicit_content_cover")
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
