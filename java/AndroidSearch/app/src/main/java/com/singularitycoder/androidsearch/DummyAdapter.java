package com.singularitycoder.androidsearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// implement Filterable interface n override getFilter method
public class DummyAdapter extends RecyclerView.Adapter<DummyAdapter.DummyViewHolder> implements Filterable {

    private List<DummyItem> dummyList;
    private List<DummyItem> dummySearchList;

    DummyAdapter(List<DummyItem> dummyList) {
        this.dummyList = dummyList;
        dummySearchList = new ArrayList<>(dummyList);
    }

    @NonNull
    @Override
    public DummyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dummy_item, parent, false);
        return new DummyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DummyViewHolder holder, int position) {
        DummyItem currentItem = dummyList.get(position);

        holder.dummyTextView.setText(currentItem.getDummyText());
    }

    @Override
    public int getItemCount() {
        return dummyList.size();
    }

    // override getFilter method and return the filtered list after it
    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    private Filter eventFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<DummyItem> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(dummySearchList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (DummyItem item : dummySearchList) {
                    if (item.getDummyText().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dummyList.clear();
            dummyList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class DummyViewHolder extends RecyclerView.ViewHolder {
        TextView dummyTextView;

        DummyViewHolder(View itemView) {
            super(itemView);
            dummyTextView = itemView.findViewById(R.id.dummy_text_view);
        }
    }
}
