package com.singularitycoder.sqliter1;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class AddUpdateMovieActivity extends AppCompatActivity {

    private static final String TAG = "AddMovieActivity";

    private EditText etMovieTitle;
    private EditText etMovieDirector;
    private Button btnAddUpdateMovie;

    public String dbAction;
    private SQLiteOperations sqliteOperations;
    private int movieItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatuBarColor(this, R.color.colorPrimaryDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        initializeViews();
        initializeData();
        getIntentData();
        clickListeners();
    }

    private void setStatuBarColor(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        window.requestFeature(window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void initializeViews() {
        etMovieTitle = findViewById(R.id.et_movie_title);
        etMovieDirector = findViewById(R.id.et_movie_director);
        btnAddUpdateMovie = findViewById(R.id.btn_add_movie);
    }

    private void initializeData() {
        sqliteOperations = new SQLiteOperations(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        dbAction = intent.getStringExtra("DB_ACTION");
        if (("add").equals(dbAction)) {
            btnAddUpdateMovie.setText("ADD MOVIE");
        }

        if (("update").equals(dbAction)) {
            btnAddUpdateMovie.setText("UPDATE MOVIE");
            movieItemId = intent.getIntExtra("MOVIE_ITEM_ID", 0);
            Log.d(TAG, "getIntentData: item id: " + movieItemId);
            MovieItem movieItem = sqliteOperations.getMovie(movieItemId);
            etMovieTitle.setText(movieItem.getTitle());
            etMovieDirector.setText(movieItem.getDirector());
        }
    }

    private void clickListeners() {
        btnAddUpdateMovie.setOnClickListener(view -> {
            if (("add").equals(dbAction)) {
                addMovie();
            }

            if (("update").equals(dbAction)) {
                updateMovie();
            }
        });
    }

    private boolean hasValidInput(EditText etMovieTitle, EditText etMovieDirector) {
        String movieTitle = String.valueOf(etMovieTitle.getText());
        String movieDirector = String.valueOf(etMovieDirector.getText());

        if (("").equals(movieTitle)) {
            etMovieTitle.setError("Title Required!");
            etMovieTitle.requestFocus();
            return false;
        }

        if (("").equals(movieDirector)) {
            etMovieDirector.setError("Director Required!");
            etMovieDirector.requestFocus();
            return false;
        }

        return true;
    }

    private void addMovie() {
        if (hasValidInput(etMovieTitle, etMovieDirector)) {
            String title = String.valueOf(etMovieTitle.getText());
            String director = String.valueOf(etMovieDirector.getText());
            MovieItem movieItem = new MovieItem(title, director);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("MOVIE_ITEM", movieItem);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void updateMovie() {
        if (hasValidInput(etMovieTitle, etMovieDirector)) {
            String title = String.valueOf(etMovieTitle.getText());
            String director = String.valueOf(etMovieDirector.getText());
            MovieItem movieItem = new MovieItem(title, director);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("MOVIE_ITEM", movieItem);
            intent.putExtra("MOVIE_ITEM_ID", String.valueOf(movieItemId));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
