package api.objects.playables;

import api.objects.utils.Contributor;
import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.Date;
import java.util.List;

public record Track(
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
        Album album,
        boolean unseen,
        String isrc,
        @SerializedName("track_position")
        int trackPosition,
        @SerializedName("disk_number")
        int diskNumber,
        @SerializedName("release_date")
        Date releaseDate,
        @SerializedName("explicit_content_lyrics")
        int explicitContentLyrics,
        @SerializedName("explicit_content_cover")
        int explicitContentCover,
        float bpm,
        float gain,
        @SerializedName("available_countries")
        List<String> availableCountries,
        Track alternative,
        List<Contributor> contributors
) {
    @Override
    public String toString() {
        return title;
    }
}
