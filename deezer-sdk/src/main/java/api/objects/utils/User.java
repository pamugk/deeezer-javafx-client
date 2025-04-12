package api.objects.utils;

import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.Date;

public record User(
        long id,
        String type,
        String name,
        @SerializedName("lastname")
        String lastName,
        @SerializedName("firstname")
        String firstName,
        String email,
        int status,
        Date birthday,
        @SerializedName("inscription_date")
        Date inscriptionDate,
        String gender,
        URL link,
        URL picture,
        @SerializedName("picture_small")
        URL pictureSmall,
        @SerializedName("picture_medium")
        URL pictureMedium,
        @SerializedName("picture_big")
        URL pictureBig,
        @SerializedName("picture_xl")
        URL pictureXl,
        String country,
        String lang,
        @SerializedName("is_kid")
        boolean kid,
        @SerializedName("explicit_content_level")
        String explicitContentLevel,
        @SerializedName("explicit_content_levels_available")
        String[] explicitContentLevelsAvailable,
        @SerializedName("trackList")
        URL trackList
) {
}
