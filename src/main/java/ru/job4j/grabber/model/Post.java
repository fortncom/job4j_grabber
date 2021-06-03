package ru.job4j.grabber.model;

import java.nio.file.Path;
import java.sql.Timestamp;

public class Post {

    private int id;
    private String name;
    private Path path;
    private String text;
    private Timestamp created;

    public Post() {
    }

    public Post(int id, String name, Path path) {
        this.id = id;
        this.name = name;
        this.path = path;
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

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
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
                + ", path=" + path
                + ", text='" + text + '\''
                + ", created=" + created
                + '}';
    }
}
