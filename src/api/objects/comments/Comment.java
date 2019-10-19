package api.objects.comments;

import api.objects.DeezerEntity;
import api.objects.utils.User;

import java.security.Timestamp;

public class Comment extends DeezerEntity {
    private String text;
    private Timestamp timestamp;
    private Object object;
    private User author;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
