package com.singularitycoder.tablayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DummyFragment extends Fragment {
    int dummyColor;
    DummyAdapter dummyAdapter;
    ArrayList<DummyItem> dummyList;

    public DummyFragment() {
    }

    @SuppressLint("ValidFragment")
    public DummyFragment(int color) {
        this.dummyColor = color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dummy, container, false);

        final FrameLayout frameLayout = view.findViewById(R.id.frame_layout_dummy);
        frameLayout.setBackgroundColor(dummyColor);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_dummy);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        dummyList = new ArrayList<>();
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));
        dummyList.add(new DummyItem(R.mipmap.ic_launcher, "Palm Beach Food Fest", "Saturday, AUG 23 - AUG 25 @ Rode Island"));

        dummyAdapter = new DummyAdapter(dummyList, getContext());
        dummyAdapter.setHasStableIds(true);
        recyclerView.setAdapter(dummyAdapter);

        return view;
    }
}