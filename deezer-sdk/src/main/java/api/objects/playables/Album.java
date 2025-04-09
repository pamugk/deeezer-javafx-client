package api.objects.playables;

import api.objects.utils.Contributor;
import api.objects.utils.Genre;
import api.objects.utils.search.SearchResponse;

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
        URL cover_small,
        URL cover_medium,
        URL cover_big,
        URL cover_xl,
        long genre_id,
        SearchResponse<Genre> genres,
        String label,
        int nb_tracks,
        int duration,
        int fans,
        int rating,
        Date release_date,
        String record_type,
        boolean available,
        Album alternative,
        URL tracklist,
        boolean explicit_lyrics,
        int explicit_content_lyrics,
        int explicit_content_cover,
        List<Contributor> contributors,
        Artist artist,
        SearchResponse<Track> tracks
) {
    @Override
    public String toString() {
        return title;
    }
}
