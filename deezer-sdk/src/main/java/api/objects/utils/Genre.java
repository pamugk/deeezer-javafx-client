package api.objects.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public record Genre(
        long id,
        String type,
        String name,
        URL picture,
        @JsonProperty("picture_small")
        URL pictureSmall,
        @JsonProperty("picture_medium")
        URL pictureMedium,
        @JsonProperty("picture_big")
        URL pictureBig,
        @JsonProperty("picture_xl")
        URL pictureXl
) {
}
