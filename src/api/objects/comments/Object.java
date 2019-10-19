package api.objects.comments;

import api.objects.DeezerEntity;

public class Object extends DeezerEntity {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
