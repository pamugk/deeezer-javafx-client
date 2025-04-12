package api.objects.utils;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public record Genre(
        long id,
        String type,
        String name,
        URL picture,
        @SerializedName("picture_small")
        URL pictureSmall,
        @SerializedName("picture_medium")
        URL pictureMedium,
        @SerializedName("picture_big")
        URL pictureBig,
        @SerializedName("picture_xl")
        URL pictureXl
) {
}
