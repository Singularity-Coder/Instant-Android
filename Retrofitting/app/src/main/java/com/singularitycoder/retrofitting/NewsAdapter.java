package com.singularitycoder.retrofitting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NewsAdapter";

    ArrayList<NewsItem> newsList;
    Context context;

    NewsAdapter(ArrayList<NewsItem> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);
        if (null != holder) {
            NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.tvAuthor.setText("Author: " + newsItem.getStrAuthor());
            newsViewHolder.tvTitle.setText(newsItem.getStrTitle());
            newsViewHolder.tvDescription.setText(newsItem.getStrDescription());
            newsViewHolder.tvPublishedAt.setText("Published at: " + newsItem.getStrPublishedAt());
            newsViewHolder.tvSource.setText("Source: " + newsItem.getStrSource());
            glideImage(context, newsItem.getStrUrlToImage(), newsViewHolder.ivHeaderImage);
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

    private static void glideImage(Context context, String imgUrl, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.color.colorAccent)
                .error(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(context)
                .load(imgUrl)
                .apply(requestOptions)
                .into(imageView);
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView tvAuthor, tvTitle, tvDescription, tvPublishedAt, tvSource;
        ImageView ivHeaderImage;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPublishedAt = itemView.findViewById(R.id.tv_published_at);
            ivHeaderImage = itemView.findViewById(R.id.iv_header_image);
            tvSource = itemView.findViewById(R.id.tv_source);
        }
    }
}
