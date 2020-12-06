package com.singularitycoder.rooming;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// Like SQLite Contract that contains column names

@Entity(tableName = "movie_table")
public class MovieItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Director")
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