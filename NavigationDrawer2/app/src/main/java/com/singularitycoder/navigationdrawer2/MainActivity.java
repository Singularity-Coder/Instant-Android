package com.singularitycoder.navigationdrawer2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Navigation Drawer");
        toolbar.setSubtitle("Let's pop it open!");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        leftNavDrawBurger();
    }

    private void leftNavDrawBurger() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        final DrawerLayout drawerLeft = findViewById(R.id.drawer_layout_left);

        // If you want the nav drawer to be open at all times
        drawerLeft.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

        // For transparent no shadow nav drawer
        // Tansparent color is #00000000.
        // U can change shades by controlling the first 2 hex digits which denote alpha values.
        drawerLeft.setScrimColor(getResources().getColor(R.color.transparent));

        NavigationView navigationView = findViewById(R.id.nav_view_left);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLeft, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLeft.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLeft.isDrawerOpen(GravityCompat.START)) {
                    drawerLeft.closeDrawer(GravityCompat.START);
                } else {
                    drawerLeft.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    public void rightNavDrawBurger(MenuItem item) {
        final DrawerLayout drawerRight = findViewById(R.id.drawer_layout_right);

        // Transparent red #99FF2369
        drawerRight.setScrimColor(getResources().getColor(R.color.transparentRed));

        if (drawerRight.isDrawerOpen(GravityCompat.END)) {
            drawerRight.closeDrawer(GravityCompat.END);
        } else {
            drawerRight.openDrawer(GravityCompat.END);
        }

        // Set click listeners on the right nav drawer views
        NavigationView navigationView = findViewById(R.id.nav_view_right);
        navigationView.setNavigationItemSelectedListener(this);
        // Just like the way we set dialog view listeners for the views of a dialog.
        // First refer the parent view that the child views are resting on.
        // Then use the parent view to refer the child view.
        View headerview = navigationView.getHeaderView(0);
        Button btn1 = headerview.findViewById(R.id.nav_draw_btn1);
        Button btn2 = headerview.findViewById(R.id.nav_draw_btn2);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked first button", Toast.LENGTH_SHORT).show();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked second button", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void leftNavDrawCustom(View view) {
        final DrawerLayout drawerLeft = findViewById(R.id.drawer_layout_left);
        NavigationView navigationView = findViewById(R.id.nav_view_left);
        navigationView.setNavigationItemSelectedListener(this);

        if (drawerLeft.isDrawerOpen(GravityCompat.START)) {
            drawerLeft.closeDrawer(GravityCompat.START);
        } else {
            drawerLeft.openDrawer(GravityCompat.START);
        }
    }

    public void rightNavDrawCustom(View view) {
        final DrawerLayout drawerRight = findViewById(R.id.drawer_layout_right);
        NavigationView navigationView = findViewById(R.id.nav_view_right);
        navigationView.setNavigationItemSelectedListener(this);

        if (drawerRight.isDrawerOpen(GravityCompat.END)) {
            drawerRight.closeDrawer(GravityCompat.END);
        } else {
            drawerRight.openDrawer(GravityCompat.END);
        }
    }

    // Gravity END/START means the nav drawer in that direction.
    // If drawer is to the right then this calls the right drawer.
    // Its to find the drawer n has noting to do with its motion or direction.
    // Its the position of the nav drawer.
    @Override
    public void onBackPressed() {
        DrawerLayout drawerRight = findViewById(R.id.drawer_layout_right);
        DrawerLayout drawerLeft = findViewById(R.id.drawer_layout_left);
        // if its opened/started/positioned from the start or the left
        if (drawerLeft.isDrawerOpen(GravityCompat.START)) {
            // close it by pushing it to the start or left. Push it back to where it came from
            drawerLeft.closeDrawer(GravityCompat.START);
        } else if (drawerRight.isDrawerOpen(GravityCompat.END)) {
            // close it by pushing it to the end or right. Push it back to where it came from
            drawerRight.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    // Create menu view on the Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int showMenu = R.menu.menu_main;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(showMenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handle left navigation drawer menu item clicks here.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        final DrawerLayout drawerLeft = findViewById(R.id.drawer_layout_left);

        switch (id) {
            case R.id.nav_home:
                drawerLeft.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_gallery:
                drawerLeft.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_slideshow:
                drawerLeft.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_tools:
                drawerLeft.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_share:
                drawerLeft.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_send:
                drawerLeft.closeDrawer(GravityCompat.START);
                break;
            default:
                drawerLeft.closeDrawer(GravityCompat.START);
                break;
        }

        drawerLeft.closeDrawer(GravityCompat.START);
        return true;
    }
}
