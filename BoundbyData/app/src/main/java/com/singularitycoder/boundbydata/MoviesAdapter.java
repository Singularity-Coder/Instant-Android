package com.singularitycoder.boundbydata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.boundbydata.databinding.MovieBinding;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MoviesAdapter";

    private List<MovieItem> moviesList;
    private Context context;
    private LayoutInflater layoutInflater;
    private MovieItemClicked movieItemClicked;

    MoviesAdapter(List<MovieItem> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        final MovieBinding movieBinding = MovieBinding.inflate(layoutInflater, parent, false);
        return new MoviesViewHolder(movieBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (null != holder) {
            MoviesViewHolder moviesViewHolder = (MoviesViewHolder) holder;
            MovieItem movieItem = moviesList.get(position);
            moviesViewHolder.bind(movieItem);
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface BoundRootItemClicked {
        void onRootItemClick();
    }

    public interface MovieItemClicked {
        void onMovieItemClick(View view, int position, MovieBinding movieBinding);
    }

    public interface BoundMovieItemDeleteClicked {
        void onMovieItemDeleteBtnClick();
    }

    public interface BoundMoviePosterLongClicked {
        void onMoviePosterLongClick(MovieItem movieItem);
    }

    public void setOnMovieItemClicked(MovieItemClicked movieItemClicked) {
        this.movieItemClicked = movieItemClicked;
    }

    public void moviePosterLongClicked(MovieItem movieItem) {
        String posterName = "";
        int nameSlice = movieItem.getMoviePoster().lastIndexOf('/');
        if (nameSlice != -1) {
            posterName = movieItem.getMoviePoster().substring(nameSlice + 1);
        }
        new MainActivity().dialogActionMessage(context, "Movie Poster name is ", posterName, "OK", "", null, null, true);
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {
        MovieBinding movieBinding;

        MoviesViewHolder(MovieBinding movieBinding) {
            super(movieBinding.getRoot());
            this.movieBinding = movieBinding;

            movieBinding.setPosterClicked((movieItem) -> moviePosterLongClicked(movieItem));
            movieBinding.setRootItemClicked(() -> movieItemClicked.onMovieItemClick(movieBinding.getRoot(), MoviesViewHolder.this.getAdapterPosition(), movieBinding));
            movieBinding.setItemDeleteClicked(() -> {
                moviesList.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            });
        }

        public void bind(MovieItem movieItem) {
            this.movieBinding.setMovieItem(movieItem);
        }
    }
}
