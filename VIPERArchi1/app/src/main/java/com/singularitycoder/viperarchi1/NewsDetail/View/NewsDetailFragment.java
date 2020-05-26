package com.singularitycoder.viperarchi1.NewsDetail.View;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.singularitycoder.viperarchi1.NewsHome.Entity.NewsSubItemArticle;
import com.singularitycoder.viperarchi1.R;

public class NewsDetailFragment extends Fragment {

    private static final String TAG = "NewsDetailFragment";

    private TextView tvAuthor, tvTitle, tvDescription, tvContent, tvPublishedAt, tvSource;
    private ImageView ivHeaderImage;
    private NewsSubItemArticle newsSubItemArticle;

    public NewsDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);
        getBundleData();
        initializeViews(view);
        setData();
        return view;
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            newsSubItemArticle = (NewsSubItemArticle) bundle.getSerializable("ARTICLE");
        }
    }

    private void initializeViews(View view) {
        tvAuthor = view.findViewById(R.id.tv_author);
        tvTitle = view.findViewById(R.id.tv_title);
        tvDescription = view.findViewById(R.id.tv_description);
        tvContent = view.findViewById(R.id.tv_content);
        tvPublishedAt = view.findViewById(R.id.tv_published_at);
        ivHeaderImage = view.findViewById(R.id.iv_header_image);
        tvSource = view.findViewById(R.id.tv_source);
    }

    private void setData() {
        glideImage(getContext(), newsSubItemArticle.getUrlToImage(), ivHeaderImage);
        tvAuthor.setText("Author: " + newsSubItemArticle.getAuthor());
        tvTitle.setText(newsSubItemArticle.getTitle());
        tvDescription.setText(newsSubItemArticle.getDescription());
        tvContent.setText(newsSubItemArticle.getContent());
        tvPublishedAt.setText("Published at: " + newsSubItemArticle.getPublishedAt());
        tvSource.setText("Source: " + newsSubItemArticle.getSource().getName());
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
}
