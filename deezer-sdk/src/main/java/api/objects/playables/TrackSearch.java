package api.objects.playables;

import java.net.URL;

public record TrackSearch(
        long id,
        String type,
        URL link,
        URL share,
        boolean readable,
        String title,
        String title_short,
        String title_version,
        int duration,
        int rank,
        boolean explicit_lyrics,
        String preview,
        Artist artist,
        Album album
) {
    @Override
    public String toString() {
        return title;
    }
}
