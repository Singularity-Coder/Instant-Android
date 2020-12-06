package com.singularitycoder.viperarchi1.NewsHome;

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
import com.singularitycoder.viperarchi1.NewsHome.Entity.NewsSubItemArticle;
import com.singularitycoder.viperarchi1.R;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NewsAdapter";

    private ArrayList<NewsSubItemArticle> newsList;
    private Context context;
    private OnNewsItemClicked onNewsItemClicked;

    public NewsAdapter(ArrayList<NewsSubItemArticle> newsList, Context context) {
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
        NewsSubItemArticle newsSubItemArticle = newsList.get(position);
        if (null != holder) {
            NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.tvTitle.setText(newsSubItemArticle.getTitle());
            glideImage(context, newsSubItemArticle.getUrlToImage(), newsViewHolder.ivHeaderImage);
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

    public interface OnNewsItemClicked {
        void onClicked(int position);
    }

    public void setOnNewsItemClicked(OnNewsItemClicked onNewsItemClicked) {
        this.onNewsItemClicked = onNewsItemClicked;
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private ImageView ivHeaderImage;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHeaderImage = itemView.findViewById(R.id.iv_header_image);
            tvTitle = itemView.findViewById(R.id.tv_title);

            itemView.setOnClickListener(view -> onNewsItemClicked.onClicked(getAdapterPosition()));
        }
    }
}