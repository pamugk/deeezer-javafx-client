package api.objects.playables;

import api.objects.utils.Contributor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Track extends TrackSearch {
    private boolean unseen;
    private String isrc;
    private int track_position;
    private int disk_number;
    private Date release_date;
    private int explicit_content_lyrics;
    private int explicit_content_cover;
    private float bpm;
    private float gain;
    private List<String> available_countries;
    private Track alternative;
    private List<Contributor> contributors;
}
