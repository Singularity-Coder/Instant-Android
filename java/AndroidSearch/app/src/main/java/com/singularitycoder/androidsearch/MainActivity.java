package com.singularitycoder.androidsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DummyAdapter adapter;
    private List<DummyItem> dummyList;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        fillDummyList();
        setUpRecyclerView();
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar_home_events);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Search");
    }

    private void fillDummyList() {
        dummyList = new ArrayList<>();
        dummyList.add(new DummyItem("Lorem Ipsum Ilor Elum"));
        dummyList.add(new DummyItem("Kilum Kipsum Kichor Kelum"));
        dummyList.add(new DummyItem("Telum Kiplam Tellor Ilum"));
        dummyList.add(new DummyItem("Khiram Kipsum Zelor Zarum"));
        dummyList.add(new DummyItem("Fila Fipsa Fikul Killam"));
        dummyList.add(new DummyItem("Fiiiram fiisam Filal Farum"));
        dummyList.add(new DummyItem("Sorum Sipsum Silor Selum"));
        dummyList.add(new DummyItem("Selum Sipsum Sulor Silum"));
        dummyList.add(new DummyItem("Eorum Epsum Emule Emmum"));
        dummyList.add(new DummyItem("Lorem Ipsum Ilor Elum"));
        dummyList.add(new DummyItem("Kilum Kipsum Kichor Kelum"));
        dummyList.add(new DummyItem("Telum Kiplam Tellor Ilum"));
        dummyList.add(new DummyItem("Khiram Kipsum Zelor Zarum"));
        dummyList.add(new DummyItem("Fila Fipsa Fikul Killam"));
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.dummy_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new DummyAdapter(dummyList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dummy_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // Set keyboard return key to "done" to dismiss keyboard upon entering text in search field
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // we are doing nothing on pressing the submit button as we have already set the IME action to done
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // automatically filter the list upon entering text in the search field
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
