package com.singularitycoder.boundbydata;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.singularitycoder.boundbydata.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    // XML BINDING PROBLEMS
    // 1. Long click doesn't work.
    // 2. Method reference in View Binding doesn't work.
    // 3. POJO fields have to be public which is just nuts.
    // 4. Importing helper function doesn't work.
    // 5. Base Adapters Library import works occasionally.

    private static final String TAG = "MainActivity";

    private MoviesAdapter moviesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<MovieItem> moviesList;
    private ActivityMainBinding mainBinding;
    private String[] postersArray = {
            "https://cdn.pixabay.com/photo/2016/11/15/07/09/photo-manipulation-1825450_960_720.jpg",
            "https://cdn.pixabay.com/photo/2020/03/28/15/20/cat-4977436_960_720.jpg",
            "https://cdn.pixabay.com/photo/2020/03/27/15/14/monastery-4973851_960_720.jpg",
            "https://cdn.pixabay.com/photo/2020/03/28/13/13/asterix-4976983_960_720.jpg",
            "https://cdn.pixabay.com/photo/2020/03/29/21/44/petra-4982346_960_720.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(this, R.color.colorPrimaryDark);
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initializeData();
        setUpRecyclerView();
        setClickListeners();
    }

    private void setStatusBarColor(Activity activity, int statusBarColor) {
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
    }

    private void setUpRecyclerView() {
        moviesAdapter = new MoviesAdapter(moviesList, this);
        mainBinding.recyclerMovies.setLayoutManager(linearLayoutManager);
        mainBinding.recyclerMovies.setAdapter(moviesAdapter);
        mainBinding.recyclerMovies.setHasFixedSize(true);
    }

    private void setClickListeners() {
        moviesAdapter.setOnMovieItemClicked((view, position, movieBinding) -> dialogActionMessage(MainActivity.this, "Movie Name is " + movieBinding.getMovieItem().getTitle(), "Movie Director is " + movieBinding.getMovieItem().getDirector() + " at position " + position, "OK", "", null, null, true));
        mainBinding.setAddMovie(this);
    }

    public void addMovieItem() {
        if (hasValidInput(mainBinding.etMovieTitle, mainBinding.etMovieDirector)) {
            String title = String.valueOf(mainBinding.etMovieTitle.getText());
            String director = String.valueOf(mainBinding.etMovieDirector.getText());
            String moviePoster = postersArray[new Random().nextInt(5)];

            MovieItem movieItem = new MovieItem();
            movieItem.setTitle(title);
            movieItem.setDirector(director);
            movieItem.setMoviePoster(moviePoster);
            movieItem.rating.set("Rating: " + new Random().nextInt(11));
            mainBinding.setMainMovieItem(movieItem); // Demo purpose. U can obviously set the values directly in the constructor below.

            title = mainBinding.getMainMovieItem().getTitle();
            director = mainBinding.getMainMovieItem().getDirector();
            moviePoster = postersArray[new Random().nextInt(5)];
            movieItem = new MovieItem(title, director, moviePoster);
            movieItem.rating.set("Rating: " + new Random().nextInt(11));
            moviesList.add(movieItem);

            moviesAdapter.notifyDataSetChanged();
            mainBinding.etMovieTitle.setText("");
            mainBinding.etMovieDirector.setText("");
            mainBinding.etMovieTitle.requestFocus();
            hideKeyboard(mainBinding.rootLayout);
        }
    }

    public void dialogActionMessage(Context context, String title, String message, String positiveActionWord, String negativeActionWord, Callable<Void> positiveAction, Callable<Void> negativeAction, boolean cancelableDialog) {
        new AlertDialog.Builder(context)
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

    private void hideKeyboard(View view) {
        view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
}
