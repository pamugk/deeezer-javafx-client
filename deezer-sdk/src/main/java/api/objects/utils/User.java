package api.objects.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public record User(
        long id,
        String type,
        String name,
        @JsonProperty("lastname")
        String lastName,
        @JsonProperty("firstname")
        String firstName,
        String email,
        int status,
        String gender,
        URL link,
        URL picture,
        @JsonProperty("picture_small")
        URL pictureSmall,
        @JsonProperty("picture_medium")
        URL pictureMedium,
        @JsonProperty("picture_big")
        URL pictureBig,
        @JsonProperty("picture_xl")
        URL pictureXl,
        String country,
        String lang,
        @JsonProperty("is_kid")
        boolean kid,
        @JsonProperty("explicit_content_level")
        String explicitContentLevel,
        @JsonProperty("explicit_content_levels_available")
        String[] explicitContentLevelsAvailable,
        @JsonProperty("trackList")
        URL trackList
) {
}
