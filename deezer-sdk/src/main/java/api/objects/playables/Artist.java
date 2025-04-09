package api.objects.playables;

import java.net.URL;

public record Artist(
        long id, String type,
        URL link, URL share,
        String name,
        URL picture, URL picture_small, URL picture_medium, URL picture_big, URL picture_xl,
        int nb_album,
        int nb_fan,
        boolean radio,
        URL tracklist
) {
    @Override
    public String toString() {
        return name;
    }
}
