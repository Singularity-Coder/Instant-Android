package com.singularitycoder.viperarchi1.NewsHome.Router;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.singularitycoder.viperarchi1.NewsDetail.View.NewsDetailFragment;
import com.singularitycoder.viperarchi1.NewsHome.Entity.NewsSubItemArticle;
import com.singularitycoder.viperarchi1.NewsHome.NewsContract;
import com.singularitycoder.viperarchi1.R;

public class NewsRouter {

    private NewsContract.Router newsRouter;

    public NewsRouter() {
        implementContractRouter();
    }

    public NewsContract.Router implementContractRouter() {
        newsRouter = new NewsContract.Router() {
            @Override
            public void goToNewsDetailScreen(Activity activity, FragmentManager getSupportFragmentManager, NewsSubItemArticle newsSubItemArticle) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("ARTICLE", newsSubItemArticle);
                NewsDetailFragment newsDetailFragment = new NewsDetailFragment();
                newsDetailFragment.setArguments(bundle);
                getSupportFragmentManager.beginTransaction().replace(R.id.con_lay_news_home_root, newsDetailFragment).addToBackStack(null).commit();
            }
        };
        return newsRouter;
    }
}
