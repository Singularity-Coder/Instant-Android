package com.singularitycoder.firestoregrocerylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GroceryItemModel> groceryList;
    private Context context;
    private OnGroceryItemClick onGroceryItemClick;

    public GroceryAdapter(ArrayList<GroceryItemModel> groceryList, Context context) {
        this.groceryList = groceryList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grocery, parent, false);
        return new GroceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final GroceryItemModel groceryItemModel = groceryList.get(position);
        if (holder instanceof GroceryViewHolder) {
            GroceryViewHolder groceryHolder = (GroceryViewHolder) holder;
            groceryHolder.tvGroceryName.setText(groceryItemModel.getGroceryName());
            groceryHolder.tvGroceryQuantity.setText("Qty: " + groceryItemModel.getGroceryQuantity());
            groceryHolder.tvTimeAdded.setText(groceryItemModel.getTimeAdded());
        }
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    interface OnGroceryItemClick {
        void onItemClick(View view, int position, String itemId);
    }

    public void setOnGroceryItemClick(OnGroceryItemClick onGroceryItemClick) {
        this.onGroceryItemClick = onGroceryItemClick;
    }

    class GroceryViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroceryName, tvGroceryQuantity, tvTimeAdded;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroceryName = itemView.findViewById(R.id.tv_grocery_name);
            tvGroceryQuantity = itemView.findViewById(R.id.tv_grocery_quantity);
            tvTimeAdded = itemView.findViewById(R.id.tv_time_added);

            itemView.setOnClickListener(view -> onGroceryItemClick.onItemClick(view, getAdapterPosition(), groceryList.get(getAdapterPosition()).getId()));
        }
    }
}
