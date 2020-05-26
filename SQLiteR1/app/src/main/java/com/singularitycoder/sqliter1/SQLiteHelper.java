package com.singularitycoder.sqliter1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.singularitycoder.sqliter1.SQLiteContract.MovieColumns.COLUMN_DIRECTOR;
import static com.singularitycoder.sqliter1.SQLiteContract.MovieColumns.COLUMN_ID;
import static com.singularitycoder.sqliter1.SQLiteContract.MovieColumns.COLUMN_TITLE;
import static com.singularitycoder.sqliter1.SQLiteContract.MovieColumns.TABLE_MOVIES;

public class SQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MoviesDb";

    SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL statement to create movie table
        String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + TABLE_MOVIES
                        + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TITLE + " TEXT, "
                        + COLUMN_DIRECTOR + " TEXT)";

        // Create Movies table
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // SQL statement to delete movie table
        String SQL_DELETE_MOVIES_TABLE = "DROP TABLE IF EXISTS " + TABLE_MOVIES;

        // Drop older movies table if existed
        db.execSQL(SQL_DELETE_MOVIES_TABLE);

        // create fresh movies table
        this.onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }
}