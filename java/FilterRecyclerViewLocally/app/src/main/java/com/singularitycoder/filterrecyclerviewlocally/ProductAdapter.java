package com.singularitycoder.filterrecyclerviewlocally;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.filterrecyclerviewlocally.databinding.ItemProductBinding;

import java.util.Collections;
import java.util.List;

public final class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private final String TAG = "ProductAdapter";

    @NonNull
    private List<ProductItem> productList = Collections.EMPTY_LIST;

    @Nullable
    private Context context;

    ProductAdapter(List<ProductItem> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductItem productItem = productList.get(position);
        if (null != holder) {
            ProductViewHolder productViewHolder = (ProductViewHolder) holder;
            productViewHolder.binding.tvName.setText(productItem.getName());
            productViewHolder.binding.tvPrice.setText("Price: " + productItem.getPrice());
            productViewHolder.binding.tvDate.setText("Date: " + productItem.getDate());
            productViewHolder.binding.tvTime.setText("Time: " + productItem.getTime());
            productViewHolder.binding.tvCategory.setText("Category: " + productItem.getCategory());
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public final void filterList(@NonNull final List<ProductItem> filteredList) {
        productList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    final class ProductViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private ItemProductBinding binding;

        private ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductBinding.bind(itemView);
        }
    }
}