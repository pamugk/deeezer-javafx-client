package api.objects.playables;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public record Artist(
        long id,
        String type,
        URL link,
        URL share,
        String name,
        URL picture,
        @JsonProperty("picture_small")
        URL pictureSmall,
        @JsonProperty("picture_medium")
        URL pictureMedium,
        @JsonProperty("picture_big")
        URL pictureBig,
        @JsonProperty("picture_xl")
        URL pictureXl,
        @JsonProperty("nb_album")
        int albumCount,
        @JsonProperty("nb_fan")
        int fanCount,
        boolean radio,
        URL trackList
) {
    @Override
    public String toString() {
        return name;
    }
}
