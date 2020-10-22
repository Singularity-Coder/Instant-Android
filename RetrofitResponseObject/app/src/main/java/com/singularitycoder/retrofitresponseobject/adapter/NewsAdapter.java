package com.singularitycoder.retrofitresponseobject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.retrofitresponseobject.model.NewsItem;
import com.singularitycoder.retrofitresponseobject.R;
import com.singularitycoder.retrofitresponseobject.databinding.ListItemNewsBinding;
import com.singularitycoder.retrofitresponseobject.helper.AppUtils;

import java.util.Collections;
import java.util.List;

public final class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private final String TAG = "NewsAdapter";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private List<NewsItem.NewsArticle> newsList = Collections.emptyList();

    @Nullable
    private Context context;

    public NewsAdapter(List<NewsItem.NewsArticle> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NewsItem.NewsArticle newsArticle = newsList.get(position);
        if (null != holder && holder instanceof NewsViewHolder) {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.binding.tvAuthor.setText("Author: " + newsArticle.getAuthor());
            newsViewHolder.binding.tvTitle.setText(newsArticle.getTitle());
            newsViewHolder.binding.tvDescription.setText(newsArticle.getDescription());
            newsViewHolder.binding.tvPublishedAt.setText("Published at: " + newsArticle.getPublishedAt());
            newsViewHolder.binding.tvSource.setText("Source: " + newsArticle.getSource().getName());
            appUtils.glideImage(context, newsArticle.getUrlToImage(), newsViewHolder.binding.ivHeaderImage);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    final class NewsViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private ListItemNewsBinding binding;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListItemNewsBinding.bind(itemView);
        }
    }
}