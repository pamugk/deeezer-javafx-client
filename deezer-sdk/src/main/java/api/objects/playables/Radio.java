package api.objects.playables;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
public class Radio extends Playable {
    private String title;
    private String description;
    private URL picture;
    private URL picture_small;
    private URL picture_medium;
    private URL picture_big;
    private URL picture_xl;
    private URL tracklist;
}
