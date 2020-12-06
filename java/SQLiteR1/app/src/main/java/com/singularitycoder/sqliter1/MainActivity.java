package com.singularitycoder.sqliter1;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int REQUEST_CODE_ADD_MOVIE = 201;
    public static final int REQUEST_CODE_UPDATE_MOVIE = 202;

    private RecyclerView recyclerMovies;
    private FloatingActionButton btnAddMovie, btnDeleteAll;

    private MoviesAdapter moviesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<MovieItem> moviesList;
    private SQLiteOperations sqliteOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatuBarColor(this, R.color.colorPrimaryDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeData();
        setUpRecyclerView();
        setClickListeners();
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

    private void initializeData() {
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        moviesList = new ArrayList<>();
        sqliteOperations = new SQLiteOperations(this);
    }

    private void initializeViews() {
        recyclerMovies = findViewById(R.id.recycler_movies);
        btnAddMovie = findViewById(R.id.btn_add_movie);
        btnDeleteAll = findViewById(R.id.btn_delete_all_movies);
    }

    private void setUpRecyclerView() {
        if (null != sqliteOperations.getAllMovies()) {
            moviesList = sqliteOperations.getAllMovies();   // get all movies from db
        }
        moviesAdapter = new MoviesAdapter(moviesList, this);
        recyclerMovies.setLayoutManager(linearLayoutManager);
        recyclerMovies.setAdapter(moviesAdapter);
        recyclerMovies.setHasFixedSize(true);
    }

    private void setClickListeners() {
        btnAddMovie.setOnClickListener(view -> addMovieItem());
        btnDeleteAll.setOnClickListener(view -> dialogActionMessage(this, "", "Delete all items?", "YES", "CANCEL", () -> deleteAllMovies(), null, false));
        moviesAdapter.setMovieItemClickListener((view, position) -> updateMovieItem(position));
        swipeToDelete();
    }

    private void addMovieItem() {
        Intent intent = new Intent(MainActivity.this, AddUpdateMovieActivity.class);
        intent.putExtra("DB_ACTION", "add");
        startActivityForResult(intent, REQUEST_CODE_ADD_MOVIE);
    }

    private Void deleteAllMovies() {
        sqliteOperations.deleteAllMovies();
        moviesList.clear();
        moviesAdapter.notifyDataSetChanged();
        return null;
    }

    private void updateMovieItem(int position) {
        MovieItem movieItem = moviesList.get(position);
        Intent intent = new Intent(MainActivity.this, AddUpdateMovieActivity.class);
        intent.putExtra("DB_ACTION", "update");
        intent.putExtra("MOVIE_ITEM_ID", movieItem.getId());
        startActivityForResult(intent, REQUEST_CODE_UPDATE_MOVIE);
    }

    private void swipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                sqliteOperations.deleteMovie(moviesList.get(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Movie at position " + viewHolder.getAdapterPosition() + " deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerMovies);
    }

    public void dialogActionMessage(Activity activity, String title, String message, String positiveActionWord, String negativeActionWord, Callable<Void> positiveAction, Callable<Void> negativeAction, boolean cancelableDialog) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveActionWord, (dialog, which) -> {
                    try {
                        positiveAction.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(negativeActionWord, (dialog, which) -> {
                    try {
                        negativeAction.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setCancelable(cancelableDialog)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        moviesList.clear();
        moviesList.addAll(sqliteOperations.getAllMovies());
        moviesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // On Back Press
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        // Add Movie
        if (data != null && requestCode == REQUEST_CODE_ADD_MOVIE && resultCode == Activity.RESULT_OK) {
            MovieItem movieItem = (MovieItem) data.getSerializableExtra("MOVIE_ITEM");
            sqliteOperations.addMovie(movieItem);   // add Movie to DB
            moviesList.add(movieItem);
            moviesAdapter.notifyDataSetChanged();
        }

        // Update Movie
        if (data != null && requestCode == REQUEST_CODE_UPDATE_MOVIE && resultCode == Activity.RESULT_OK) {
            MovieItem movieItem = (MovieItem) data.getSerializableExtra("MOVIE_ITEM");
            String movieItemId = data.getStringExtra("MOVIE_ITEM_ID");
            Log.d(TAG, "onActivityResult: " + movieItem.getTitle() + movieItem.getDirector());
            sqliteOperations.updateMovie(movieItem, movieItemId);
            if (null != sqliteOperations.getAllMovies()) {
                moviesList.clear();
                moviesList.addAll(sqliteOperations.getAllMovies());
                moviesAdapter.notifyDataSetChanged();
            }
        }
    }
}
