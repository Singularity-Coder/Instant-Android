package com.singularitycoder.parallaxtabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {
    private Context context;
    private ArrayList<FriendsModel> friends = new ArrayList<>();

    public FriendsAdapter(Context context, ArrayList<FriendsModel> friends) {
        this.context = context;
        this.friends = friends;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_friend, parent, false);
        return new FriendsAdapter.FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        FriendsModel friend = friends.get(position);

        holder.mName.setText(friend.getName());
        holder.mDescription.setText(friend.getDescription());
        holder.mFirstLetter.setText(String.valueOf(friend.getFirstLetter()));
    }

    @Override
    public int getItemCount() {
        return friends == null ? 0 : friends.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mDescription;
        private TextView mFirstLetter;

        public FriendViewHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mDescription = (TextView) itemView.findViewById(R.id.tv_desc);
            mFirstLetter = (TextView) itemView.findViewById(R.id.tv_firstletter);
        }
    }
}