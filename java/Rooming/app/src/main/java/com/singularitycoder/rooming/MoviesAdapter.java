package com.singularitycoder.rooming;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MoviesAdapter";

    private List<MovieItem> moviesList;
    private Context context;
    private MovieItemClicked movieItemClicked;

    MoviesAdapter(List<MovieItem> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MovieItem movieItem = moviesList.get(position);
        if (null != holder) {
            MoviesViewHolder moviesViewHolder = (MoviesViewHolder) holder;
            moviesViewHolder.tvMovieTitle.setText(movieItem.getTitle());
            moviesViewHolder.tvMovieDirector.setText(movieItem.getDirector());
            Log.d(TAG, "onBindViewHolder: " + movieItem.getDirector());
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

    class MoviesViewHolder extends RecyclerView.ViewHolder {

        TextView tvMovieTitle, tvMovieDirector;

        MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
            tvMovieDirector = itemView.findViewById(R.id.tv_movie_director);

            itemView.setOnClickListener(view -> movieItemClicked.onMovieItemClicked(itemView, getAdapterPosition()));
        }
    }

    interface MovieItemClicked {
        void onMovieItemClicked(View view, int position);
    }

    void setMovieItemClickListener(MovieItemClicked movieItemClicked) {
        this.movieItemClicked = movieItemClicked;
    }
}
