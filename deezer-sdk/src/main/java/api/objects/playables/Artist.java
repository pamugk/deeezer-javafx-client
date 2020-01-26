package api.objects.playables;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
public class Artist extends Playable {
    private String name;
    private URL picture;
    private URL picture_small;
    private URL picture_medium;
    private URL picture_big;
    private URL picture_xl;
    private int nb_album;
    private int nb_fan;
    private boolean radio;
    private URL tracklist;

    @Override
    public String toString() {
        return name;
    }
}
