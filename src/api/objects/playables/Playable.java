package api.objects.playables;

import api.objects.DeezerEntity;

import java.net.URL;

public abstract class Playable extends DeezerEntity {
    private URL link;
    private URL share;
    public URL getLink() { return link; }
    public void setLink(URL link) { this.link = link; }
    public URL getShare() { return share; }
    public void setShare(URL share) { this.share = share; }
}
