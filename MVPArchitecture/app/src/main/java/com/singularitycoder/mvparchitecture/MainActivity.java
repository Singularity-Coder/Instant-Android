package com.singularitycoder.mvparchitecture;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

// VIEW: This is the "V" in MVP
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoFeedText;
    private TextView tvChooseCountry, tvChooseCategory;
    private TextView noInternetText;
    private NewsPresenter newsPresenter;
    private NewsContract.View newsView;

    private ArrayList<NewsSubItemArticle> newsList = new ArrayList<>();
    private ArrayList<NewsSubItemArticle> getDataFromNewsView = new ArrayList<>();

    private String strSelectedCountry = "in";
    private String strSelectedCategory = "technology";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
        initializeViews();
        setUpRecyclerView();
        setNewsDataFromNewsContract();
        initializeClasses();
        getNewsDataFromApi();
        setClickListeners();
        swipeRefreshLayout.setOnRefreshListener(this::getNewsDataFromApi);
    }

    private void setStatusBarColor(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        window.requestFeature(window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progress_circular);
        recyclerView = findViewById(R.id.recycler_news);
        tvNoFeedText = findViewById(R.id.tv_nothing_to_show);
        noInternetText = findViewById(R.id.tv_no_internet);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvChooseCountry = findViewById(R.id.tv_choose_country);
        tvChooseCategory = findViewById(R.id.tv_choose_category);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList, this);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setNewsDataFromNewsContract() {
        newsView = new NewsContract.View() {
            @Override
            public void showProgress() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void hideProgress() {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void setResponseToViews(List<NewsSubItemArticle> data) {
                getDataFromNewsView.clear();
                getDataFromNewsView.addAll(data);
                setNewsData();
            }

            @Override
            public void ifResponseFailed(Throwable throwable) {
                Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                tvNoFeedText.setVisibility(View.VISIBLE);
                tvNoFeedText.setText("Something is wrong.\nTry again!");
            }

            @Override
            public void ifResponseEmpty() {
                tvNoFeedText.setText("Nothing to show :(");
            }
        };
    }

    private void initializeClasses() {
        newsPresenter = new NewsPresenter(newsView);
    }

    private void getNewsDataFromApi() {
        if (hasInternet(this)) {
            newsPresenter.implementContractPresenter().showNews(strSelectedCountry, strSelectedCategory);
            noInternetText.setVisibility(View.GONE);
        } else {
            noInternetText.setVisibility(View.VISIBLE);
        }
    }

    private void setClickListeners() {
        tvChooseCountry.setOnClickListener(view -> dialogCountrySelection(tvChooseCountry));
        tvChooseCategory.setOnClickListener(view -> dialogNewsCategorySelection(tvChooseCategory));
    }

    private void setNewsData() {
        newsList.clear();
        newsList.addAll(getDataFromNewsView);
        newsAdapter.notifyDataSetChanged();
        noInternetText.setVisibility(View.GONE);
    }

    public boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private void dialogCountrySelection(TextView tvChooseCountry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Country");
        String[] selectArray = {"in", "jp", "cn", "ru", "us", "gb", "il", "de", "br", "au"};
        String[] selectArrayAlias = {"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"};
        builder.setItems(selectArrayAlias, (dialog, which) -> {
            switch (which) {
                case 0:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[0]);
                    strSelectedCountry = selectArray[0];
                    getNewsDataFromApi();
                    break;
                case 1:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[1]);
                    strSelectedCountry = selectArray[1];
                    getNewsDataFromApi();
                    break;
                case 2:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[2]);
                    strSelectedCountry = selectArray[2];
                    getNewsDataFromApi();
                    break;
                case 3:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[3]);
                    strSelectedCountry = selectArray[3];
                    getNewsDataFromApi();
                    break;
                case 4:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[4]);
                    strSelectedCountry = selectArray[4];
                    getNewsDataFromApi();
                    break;
                case 5:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[5]);
                    strSelectedCountry = selectArray[5];
                    getNewsDataFromApi();
                    break;
                case 6:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[6]);
                    strSelectedCountry = selectArray[6];
                    getNewsDataFromApi();
                    break;
                case 7:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[7]);
                    strSelectedCountry = selectArray[7];
                    getNewsDataFromApi();
                    break;
                case 8:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[8]);
                    strSelectedCountry = selectArray[8];
                    getNewsDataFromApi();
                    break;
                case 9:
                    tvChooseCountry.setText("Country: " + selectArrayAlias[9]);
                    strSelectedCountry = selectArray[9];
                    getNewsDataFromApi();
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dialogNewsCategorySelection(TextView tvChooseCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category");
        String[] selectArray = {"business", "entertainment", "health", "science", "sports", "technology"};
        builder.setItems(selectArray, (dialog, which) -> {
            switch (which) {
                case 0:
                    tvChooseCategory.setText("Category: " + selectArray[0]);
                    strSelectedCategory = selectArray[0];
                    getNewsDataFromApi();
                    break;
                case 1:
                    tvChooseCategory.setText("Category: " + selectArray[1]);
                    strSelectedCategory = selectArray[1];
                    getNewsDataFromApi();
                    break;
                case 2:
                    tvChooseCategory.setText("Category: " + selectArray[2]);
                    strSelectedCategory = selectArray[2];
                    getNewsDataFromApi();
                    break;
                case 3:
                    tvChooseCategory.setText("Category: " + selectArray[3]);
                    strSelectedCategory = selectArray[3];
                    getNewsDataFromApi();
                    break;
                case 4:
                    tvChooseCategory.setText("Category: " + selectArray[4]);
                    strSelectedCategory = selectArray[4];
                    getNewsDataFromApi();
                    break;
                case 5:
                    tvChooseCategory.setText("Category: " + selectArray[5]);
                    strSelectedCategory = selectArray[5];
                    getNewsDataFromApi();
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}