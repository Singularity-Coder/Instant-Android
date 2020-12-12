package com.singularitycoder.parallaxtabs;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class FriendsListFragment extends Fragment {
    int color;
    ArrayList<FriendsModel> friendsList;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    public FriendsListFragment(int color) {
        this.color = color;

        friendsList = new ArrayList<>();
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));
        friendsList.add(new FriendsModel("List of Games", "Lorem ipsum dolor sit amet, id augue quando vis. Tale praesent in sit, zril nobis pri no. Dico corpora mea ut. Nulla commodo eum ei. Eu eruditi posidonium pro, quo no vidisse nonumes principes. Ea sea eruditi apeirian facilisis."));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        final FrameLayout frameLayout = view.findViewById(R.id.fragment_frame_layout);
        frameLayout.setBackgroundColor(color);

        RecyclerView recyclerView = view.findViewById(R.id.fragment_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        FriendsAdapter adapter = new FriendsAdapter(getContext(), friendsList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}