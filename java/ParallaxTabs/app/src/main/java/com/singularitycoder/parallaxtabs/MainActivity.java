package com.singularitycoder.parallaxtabs;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // To set full screen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        mCoordinatorLayout = findViewById(R.id.collapsing_maincontent);

        // Set Toolbar
        final Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Parallax Tabs");

        // Set ViewPager
        final ViewPager viewPager = findViewById(R.id.collapsing_viewpager);
        setupViewPager(viewPager);

        // Set TabLayout
        TabLayout tabLayout = findViewById(R.id.collapsing_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Set CollapsingToolbar
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_collapse_toolbar);

        // Set color of CollaspongToolbar when collapsing
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.accent_500));
        collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.accent_700));

//        try {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header);
//            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//                @SuppressWarnings("ResourceType")
//                @Override
//                public void onGenerated(Palette palette) {
//                    int vibrantColor = palette.getVibrantColor(R.color.accent_500);
//                    int vibrantDarkColor = palette.getDarkVibrantColor(R.color.accent_700);
//                    collapsingToolbarLayout.setContentScrimColor(vibrantColor);
//                    collapsingToolbarLayout.setStatusBarScrimColor(vibrantDarkColor);
//                }
//            });
//        } catch (Exception e) {
//            // if fetching Bitmap fails, fallback on primary colors
//            Log.e(TAG, "onCreate: failed to create bitmap from background", e.fillInStackTrace());
//            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
//            collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//        }

        // Do something on selecting each tab of tab layout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
                Log.d(TAG, "onTabSelected: pos: " + tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "You clciked this 1", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "You clciked this 2", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "You clciked this 3", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "You clciked this 4", Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Snackbar.make(mCoordinatorLayout, "1 got away", Snackbar.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Snackbar.make(mCoordinatorLayout, "2 got away", Snackbar.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Snackbar.make(mCoordinatorLayout, "3 got away", Snackbar.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Snackbar.make(mCoordinatorLayout, "4 got away", Snackbar.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "You clciked 1 again", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "You clciked 2 again", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "You clciked 3 again", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "You clciked 4 again", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new FriendsListFragment(ContextCompat.getColor(this, R.color.yellow)), "LISTS");
        adapter.addFrag(new FriendsListFragment(ContextCompat.getColor(this, R.color.amber)), "FRIENDS");
        adapter.addFrag(new FriendsListFragment(ContextCompat.getColor(this, R.color.orange)), "PHOTOS");
        viewPager.setAdapter(adapter);
    }

    // Optional Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // onClick of menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                return true;
            case R.id.action_share:
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}