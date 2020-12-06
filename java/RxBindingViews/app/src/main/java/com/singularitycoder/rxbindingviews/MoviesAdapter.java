package com.singularitycoder.rxbindingviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MoviesAdapter";

    private List<String> moviesList;
    private Context context;
    private MovieItemClicked movieItemClicked;

    MoviesAdapter(List<String> moviesList, Context context) {
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
        String title = moviesList.get(position);
        if (null != holder) {
            MoviesViewHolder moviesViewHolder = (MoviesViewHolder) holder;
            moviesViewHolder.tvMovieTitle.setText(title);
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

    void searchFilter(ArrayList<String> filteredList) {
        moviesList = filteredList;
        notifyDataSetChanged();
    }

    interface MovieItemClicked {
        void onMovieItemClicked(View view, int position);
    }

    void setMovieItemClickListener(MovieItemClicked movieItemClicked) {
        this.movieItemClicked = movieItemClicked;
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMovieTitle;

        MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);

            itemView.setOnClickListener(view -> movieItemClicked.onMovieItemClicked(itemView, getAdapterPosition()));
        }
    }
}
