package com.singularitycoder.rxbindingviews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.jakewharton.rxbinding3.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerMovies;
    private Button btnAddMovie;
    private EditText etMovieTitle, etMoviesSearch;
    private MoviesAdapter moviesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<String> moviesList;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeData();
        setUpRecyclerView();
        swipeToDelete();
        allObservables();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setStatusBarColor(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        window.requestFeature(window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void initializeViews() {
        recyclerMovies = findViewById(R.id.recycler_movies);
        btnAddMovie = findViewById(R.id.btn_add_movie);
        etMovieTitle = findViewById(R.id.et_movie_title);
        etMoviesSearch = findViewById(R.id.et_movie_search);
    }

    private void initializeData() {
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        moviesList = new ArrayList<>();
    }

    private void setUpRecyclerView() {
        moviesAdapter = new MoviesAdapter(moviesList, this);
        recyclerMovies.setLayoutManager(linearLayoutManager);
        recyclerMovies.setAdapter(moviesAdapter);
        recyclerMovies.setHasFixedSize(true);
    }

    private void allObservables() {
        observableSearchDirectly();
//        observableSearchWithDelay();  // Alternate way
        observableAddMovieDefault();
//        observableAddMovieMappedToButton();
//        observableAddMovieWithDelay();  // Alternate way
//        observableAddMovieAvoidMultiClicks();  // Alternate way
    }

    private void addMovieItem() {
        if (hasValidInput(etMovieTitle)) {
            moviesList.add(String.valueOf(etMovieTitle.getText()));
            moviesAdapter.notifyDataSetChanged();
            etMovieTitle.setText("");
        }
    }

    private Void deleteMovieItem(int position, String actionType) {
        if (("POSITIVE").equals(actionType)) {
            moviesList.remove(position);
            moviesAdapter.notifyItemRemoved(position);
        }

        if (("NEGATIVE").equals(actionType)) {
            moviesAdapter.notifyDataSetChanged();
        }
        return null;
    }

    private void swipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                dialogActionMessage(MainActivity.this, "", "Delete this item?", "DELETE", "CANCEL", () -> deleteMovieItem(viewHolder.getAdapterPosition(), "POSITIVE"), () -> deleteMovieItem(viewHolder.getAdapterPosition(), "NEGATIVE"), true);
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

    private boolean hasValidInput(EditText etMovieTitle) {
        String movieTitle = String.valueOf(etMovieTitle.getText());

        if (("").equals(movieTitle)) {
            etMovieTitle.setError("Title Required!");
            etMovieTitle.requestFocus();
            return false;
        }

        return true;
    }

    private void searchMovies(String filteredText) {
        ArrayList<String> movieSearchList = new ArrayList<>();
        for (String movieTitle : moviesList) {
            if (movieTitle.toLowerCase().trim().contains(filteredText.toLowerCase())) {
                movieSearchList.add(movieTitle);
            }
        }
        moviesAdapter.searchFilter(movieSearchList);
    }

    private void observableSearchDirectly() {
        compositeDisposable.add(
                RxTextView
                        .textChangeEvents(etMoviesSearch)
                        .skipInitialValue()
                        .distinctUntilChanged()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(observerSearchDirectly())
        );
    }

    private DisposableObserver<TextViewTextChangeEvent> observerSearchDirectly() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                searchMovies(String.valueOf(textViewTextChangeEvent.getText()).toLowerCase());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    @SuppressLint("CheckResult")
    private void observableSearchWithDelay() {
        compositeDisposable.add(
                RxTextView
                        .textChanges(etMoviesSearch)
                        .map(charSequence -> charSequence.toString())
//                      .map(charSequence -> charSequence)  // Default
                        .filter(s -> s.toString().length() > 1)
                        .debounce(2, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
//                      .subscribe(charSequence -> label1.setText(charSequence))   // Default
                        .subscribe(observerSearchWithDelay(), observerError())
        );
    }

    private Consumer<String> observerSearchWithDelay() {
        return s -> MainActivity.this.searchMovies(s);
    }

    private Consumer<Throwable> observerError() {
        return throwable -> Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void observableAddMovieDefault() {
        compositeDisposable.add(
                RxView
                        .clicks(btnAddMovie)
                        .subscribe(observerAddMovieDefault(), observerError())
        );
    }

    private void observableAddMovieMappedToButton() {
        compositeDisposable.add(
                RxView
                        .clicks(btnAddMovie)
                        .map(o -> btnAddMovie)
                        .subscribe(observerAddMovieMappedToButton(), observerError())
        );
    }

    private Consumer<? super Unit> observerAddMovieDefault() {
        return (Consumer<Unit>) unit -> {
            MainActivity.this.addMovieItem();
            Toast.makeText(MainActivity.this, "Add Button clicked", Toast.LENGTH_SHORT).show();
        };
    }

    private Consumer<Button> observerAddMovieMappedToButton() {
        return button -> {
            MainActivity.this.addMovieItem();
            Toast.makeText(MainActivity.this, "Add Button clicked", Toast.LENGTH_SHORT).show();
        };
    }

    private void observableAddMovieWithDelay() {
        compositeDisposable.add(
                RxView
                        .clicks(btnAddMovie)
                        .map(o -> btnAddMovie.getText().toString())
                        .debounce(5, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observerAddMovieWithDelay(), observerError())
        );
    }

    private void observableAddMovieAvoidMultiClicks() {
        compositeDisposable.add(
                RxView
                        .clicks(btnAddMovie)
                        .map(o -> btnAddMovie.getText().toString())
                        .throttleFirst(2, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observerAddMovieWithDelay(), observerError())
        );
    }

    private Consumer<String> observerAddMovieWithDelay() {
        return s -> {
            MainActivity.this.addMovieItem();
            Toast.makeText(MainActivity.this, s + " clicked", Toast.LENGTH_SHORT).show();
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}