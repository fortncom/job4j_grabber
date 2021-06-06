package ru.job4j.grabber.model;

import java.sql.Timestamp;

public class Post {

    private int id;
    private String name;
    private String link;
    private String text;
    private Timestamp created;

    public Post() {
    }

    public Post(int id, String name, String link, String text, Timestamp created) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.text = text;
        this.created = created;
    }

    public Post(int id, String name, String link) {
        this.id = id;
        this.name = name;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", link=" + link
                + ", text='" + text + '\''
                + ", created=" + created
                + '}';
    }
}
