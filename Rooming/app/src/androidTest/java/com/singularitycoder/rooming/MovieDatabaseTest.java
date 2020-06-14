package com.singularitycoder.rooming;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*;

public abstract class MovieDatabaseTest {

    // System under test
    private MovieDatabase movieDatabase;


    public MovieDao getMovieDao(){
        return movieDatabase.movieDao();
    }

    @Before
    public void openRoom(){
        movieDatabase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                MovieDatabase.class
        ).build();
    }

    @After
    public void closeRoom(){
        movieDatabase.close();
    }
}