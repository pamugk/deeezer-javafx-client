package api.objects.comments;

import api.objects.DeezerEntity;
import api.objects.utils.User;

public class Comment extends DeezerEntity {
    private String text;
    private long date;
    private Object object;
    private User author;

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public long getDate() { return date; }
    public void setDate(long date) {
        this.date = date;
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
