package com.singularitycoder.actionbarmenus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dummy, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Do something
                return false;
            }
        });
        return true;
    }

    // What should happen on click of menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Do something
                Toast.makeText(this, "Search got clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_add:
                // Do something
                Toast.makeText(this, "Add got clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_filter:
                // Do something
                Toast.makeText(this, "Filter got clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                // Do something
                Toast.makeText(this, "Settings got clicked", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
