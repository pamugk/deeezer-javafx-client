package api.objects.playables;

import api.objects.DeezerEntity;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
public abstract class Playable extends DeezerEntity {
    private URL link;
    private URL share;
}
