package com.singularitycoder.tablayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DummyAdapter extends RecyclerView.Adapter<DummyAdapter.DummyViewHolder> {

    ArrayList<DummyItem> dummyList;
    Context dummyContext;

    public DummyAdapter(ArrayList<DummyItem> dummyList, Context dummyContext) {
        this.dummyList = dummyList;
        this.dummyContext = dummyContext;
    }

    @NonNull
    @Override
    public DummyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dummy, parent, false);
        return new DummyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DummyViewHolder holder, int position) {
        DummyItem dummyItem = dummyList.get(position);

        holder.imgDummyHeader.setImageResource(dummyItem.getIntDummyImage());
        holder.tvDummyTitle.setText(dummyItem.getStrDummyTitle());
        holder.tvDummySubtitle.setText(dummyItem.getStrDummySubtitle());
    }

    @Override
    public int getItemCount() {
        return dummyList.size();
    }

    class DummyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgDummyHeader;
        TextView tvDummyTitle, tvDummySubtitle;

        public DummyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgDummyHeader = itemView.findViewById(R.id.img_dummy);
            tvDummyTitle = itemView.findViewById(R.id.tv_dummy_title);
            tvDummySubtitle = itemView.findViewById(R.id.tv_dummy_subtitle);

        }
    }
}