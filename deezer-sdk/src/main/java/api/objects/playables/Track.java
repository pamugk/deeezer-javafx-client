package api.objects.playables;

import api.objects.utils.Contributor;

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
        String title_short,
        String title_version,
        int duration,
        int rank,
        boolean explicit_lyrics,
        String preview,
        Artist artist,
        Album album,
        boolean unseen,
        String isrc,
        int track_position,
        int disk_number,
        Date release_date,
        int explicit_content_lyrics,
        int explicit_content_cover,
        float bpm,
        float gain,
        List<String> available_countries,
        Track alternative,
        List<Contributor> contributors
) {
}
