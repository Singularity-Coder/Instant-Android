package com.singularitycoder.boundbydata;

import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.bumptech.glide.Glide;

public class MovieItem extends BaseObservable {

    public String title;
    public String director;
    public String moviePoster;
    public ObservableField<String> rating = new ObservableField<>();

    public MovieItem() {
    }

    public MovieItem(String title, String director, String moviePoster) {
        this.title = title;
        this.director = director;
        this.moviePoster = moviePoster;
    }

    public ObservableField<String> getRating() {
        return rating;
    }

    @Bindable
    String getTitle() {
        return title;
    }

    @Bindable
    String getDirector() {
        return director;
    }

    @Bindable
    public String getMoviePoster() {
        return moviePoster;
    }

    void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(R.id.tv_movie_title);
    }

    void setDirector(String director) {
        this.director = director;
        notifyPropertyChanged(R.id.tv_movie_director);
    }

    void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
        notifyPropertyChanged(BR.moviePoster);  // Another way of notifying change of a specific property
    }

    @BindingAdapter({"android:poster"})
    public static void loadMoviePoster(ImageView view, String imageUrl) {
        Glide.with(view.getContext()).load(imageUrl).into(view);
    }

    @Override
    public String toString() {
        return "Movie [title=" + title + ", director=" + director + "]";
    }
}