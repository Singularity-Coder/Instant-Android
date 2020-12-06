package com.singularitycoder.sqliter1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import static com.singularitycoder.sqliter1.SQLiteContract.MovieColumns.COLUMN_DIRECTOR;
import static com.singularitycoder.sqliter1.SQLiteContract.MovieColumns.COLUMN_ID;
import static com.singularitycoder.sqliter1.SQLiteContract.MovieColumns.COLUMN_TITLE;
import static com.singularitycoder.sqliter1.SQLiteContract.MovieColumns.TABLE_MOVIES;

public class SQLiteOperations {

    SQLiteHelper sqliteHelper;

    public SQLiteOperations(Context context) {
        this.sqliteHelper = new SQLiteHelper(context);
    }

    // CRUD operations (create "add", read "get", update, delete) movie + get all movies + delete all movies

    void addMovie(MovieItem movieItem) {
        Log.d("addMovie", movieItem.toString());

        // 1. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, movieItem.getTitle()); // get title
        values.put(COLUMN_DIRECTOR, movieItem.getDirector()); // get director

        // 2. get reference to writable DB
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        // 3. insert
        db.insert(
                TABLE_MOVIES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    MovieItem getMovie(int id) {
        MovieItem movieItem = new MovieItem();

        // Define a projection that specifies which columns from the database you will actually use after this query.
        String[] projection = {
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DIRECTOR
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        // Optional - How you want the results sorted in the resulting Cursor
        String sortOrder = COLUMN_ID + " DESC";

        // 1. get reference to readable DB
        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.query(
                TABLE_MOVIES, // The table to query
                projection, // The array of columns to return (pass null to get all)
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null, // Sort order
                null); // Limit

        // 3. if we got results get the first one
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                // 4. build movie object
                movieItem.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                movieItem.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                movieItem.setDirector(cursor.getString(cursor.getColumnIndex(COLUMN_DIRECTOR)));

                Log.d("getMovie(" + id + ")", movieItem.toString());
            }
            cursor.close();
        }

        // 5. return movie
        return movieItem;
    }

    // Get All Movies
    List<MovieItem> getAllMovies() {
        List<MovieItem> movies = new LinkedList<MovieItem>();

        // 1. build the query
        String query = "SELECT * FROM " + TABLE_MOVIES;

        // 2. get reference to writable DB
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            // 3. go over each row, build movie and add it to list
            MovieItem movieItem = null;
            if (cursor.moveToFirst()) {
                do {
                    movieItem = new MovieItem();
                    movieItem.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                    movieItem.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                    movieItem.setDirector(cursor.getString(cursor.getColumnIndex(COLUMN_DIRECTOR)));

                    // Add movie to movies
                    movies.add(movieItem);
                } while (cursor.moveToNext());
            }
            Log.d("getAllMovies()", movies.toString());
            cursor.close();
        }

        // return movies
        return movies;
    }

    // Updating single movie
    void updateMovie(MovieItem movieItem, String movieItemId) {

        // 1. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, movieItem.getTitle()); // get title
        values.put(COLUMN_DIRECTOR, movieItem.getDirector()); // get director

        // 2. get reference to writable DB
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        // 3. updating row
        db.update(
                TABLE_MOVIES, //table
                values, // column/value
                COLUMN_ID + " = ?", // selections
                new String[]{movieItemId}); //selection args
//                new String[]{String.valueOf(position)}); //selection args

        // 4. close
        db.close();
    }

    // Deleting single movie
    void deleteMovie(MovieItem movieItem) {

        // 1. get reference to writable DB
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        // 2. delete
        db.delete(
                TABLE_MOVIES,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(movieItem.getId())});

        // 3. close
        db.close();
    }

    // Delete all movies
    void deleteAllMovies() {
        // 1. get reference to writable DB
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        db.delete(
                TABLE_MOVIES,
                null,
                null);
    }

}
