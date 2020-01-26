package api.objects.playables;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackSearch extends Playable {
    private boolean readable;
    private String title;
    private String title_short;
    private String title_version;
    private int duration;
    private int rank;
    private boolean explicit_lyrics;
    private String preview;
    private Artist artist;
    private Album album;

    @Override
    public String toString() {
        return title;
    }
}
