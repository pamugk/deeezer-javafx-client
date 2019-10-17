package com.psu.deezerunofficialapp.api.objects.comments;

import com.psu.deezerunofficialapp.api.objects.utils.User;

import java.security.Timestamp;

public class Comment {
    private int id;
    private String text;
    private Timestamp timestamp;
    private Object object;
    private User author;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
