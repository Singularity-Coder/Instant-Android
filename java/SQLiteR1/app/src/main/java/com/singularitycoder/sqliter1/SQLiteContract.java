package com.singularitycoder.sqliter1;

import android.provider.BaseColumns;

final class SQLiteContract {

    // To prevent someone from accidentally instantiating the contract class, make the constructor private.
    private SQLiteContract() {
    }

    // Inner class that defines the table contents. Create a class for every table
    static class MovieColumns implements BaseColumns {
        // Table Name
        static final String TABLE_MOVIES = "movies";
        // Table Column Names
        static final String COLUMN_ID = "id";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_DIRECTOR = "director";
    }
}
