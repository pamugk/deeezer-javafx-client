package api.objects.playables;

import api.objects.utils.Contributor;
import api.objects.utils.Genre;
import api.objects.utils.search.SearchResponse;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Album extends Playable {
    private String title;
    private String upc;
    private URL cover;
    private URL cover_small;
    private URL cover_medium;
    private URL cover_big;
    private URL cover_xl;
    private long genre_id;
    private SearchResponse<Genre> genres;
    private String label;
    private int nb_tracks;
    private int duration;
    private int fans;
    private int rating;
    private Date release_date;
    private String record_type;
    private boolean available;
    private Album alternative;
    private URL tracklist;
    private boolean explicit_lyrics;
    private int explicit_content_lyrics;
    private int explicit_content_cover;
    private List<Contributor> contributors;
    private Artist artist;
    private SearchResponse<Track> tracks;

    @Override
    public String toString() {
        return title;
    }
}
