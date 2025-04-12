package api.objects.playables;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public record Artist(
        long id,
        String type,
        URL link,
        URL share,
        String name,
        URL picture,
        @SerializedName("picture_small")
        URL pictureSmall,
        @SerializedName("picture_medium")
        URL pictureMedium,
        @SerializedName("picture_big")
        URL pictureBig,
        @SerializedName("picture_xl")
        URL pictureXl,
        @SerializedName("nb_album")
        int albumCount,
        @SerializedName("nb_fan")
        int fanCount,
        boolean radio,
        URL trackList
) {
    @Override
    public String toString() {
        return name;
    }
}
