package com.singularitycoder.sqliter1;

import java.io.Serializable;

public class MovieItem implements Serializable {

    private int id;
    private String title;
    private String director;

    MovieItem() {
    }

    MovieItem(String title, String director) {
        super();
        this.title = title;
        this.director = director;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getDirector() {
        return director;
    }

    void setDirector(String director) {
        this.director = director;
    }

    @Override
    public String toString() {
        return "Movie [id=" + id + ", title=" + title + ", director=" + director + "]";
    }
}