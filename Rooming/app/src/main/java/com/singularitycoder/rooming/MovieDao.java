package com.singularitycoder.rooming;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Like SQLite Operations

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovie(MovieItem movieItem);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateMovie(MovieItem movieItem);

    @Delete
    void deleteMovie(MovieItem movieItem);

    @Query("SELECT * FROM movie_table WHERE id=:id")
    MovieItem getMovie(int id);

    @Query("SELECT * FROM movie_table ORDER BY title DESC")
    List<MovieItem> getAllMovies();      // Custom Query to Get All Movies.

    @Query("DELETE FROM movie_table")
    void deleteAllMovies();             // Custom Query to Delete All Movies
}
