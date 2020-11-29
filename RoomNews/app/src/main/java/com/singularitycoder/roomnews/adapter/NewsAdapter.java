package com.singularitycoder.roomnews.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.roomnews.R;
import com.singularitycoder.roomnews.databinding.ListItemNewsBinding;
import com.singularitycoder.roomnews.helper.AppUtils;
import com.singularitycoder.roomnews.model.NewsItem;

import java.util.Collections;
import java.util.List;

public final class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isDark = false;

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private final Context context;

    @NonNull
    private List<NewsItem.NewsArticle> newsList = Collections.emptyList();

    public NewsAdapter(
            @NonNull final List<NewsItem.NewsArticle> newsList,
            @NonNull final Context context,
            final boolean isDark) {
        this.newsList = newsList;
        this.context = context;
        this.isDark = isDark;
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
            setAnimation(newsViewHolder);
        }
    }

    private void setAnimation(NewsViewHolder newsViewHolder) {
        newsViewHolder.binding.ivHeaderImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        newsViewHolder.binding.cardDetails.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale));
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
            itemView.setOnClickListener(v -> {
            });
            if (isDark) setDarkTheme();
        }

        private void setDarkTheme() {
            binding.cardDetails.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.black)));
        }
    }
}