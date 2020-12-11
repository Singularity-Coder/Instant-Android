package com.singularitycoder.httpurlconnection2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.httpurlconnection2.R;
import com.singularitycoder.httpurlconnection2.databinding.ListItemRestaurantBinding;
import com.singularitycoder.httpurlconnection2.model.RestaurantModel;

import java.util.Collections;
import java.util.List;

public final class RestaurantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RestaurantsAdapter";

    @NonNull
    private List<RestaurantModel.Restaurant> restaurantList = Collections.emptyList();

    @Nullable
    private Context context;

    public RestaurantsAdapter(List<RestaurantModel.Restaurant> restaurantList, Context context) {
        this.restaurantList = restaurantList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final ListItemRestaurantBinding binding = ListItemRestaurantBinding.inflate(layoutInflater, parent, false);
        return new RestaurantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final RestaurantModel.Restaurant restaurant = restaurantList.get(position);
        if (null != holder && holder instanceof RestaurantViewHolder) {
            final RestaurantViewHolder restaurantViewHolder = (RestaurantViewHolder) holder;
            restaurantViewHolder.binding.setRestaurant(restaurant.getRestaurant());
            restaurantViewHolder.binding.executePendingBindings();
            setAnimation(restaurantViewHolder);
        }
    }

    private void setAnimation(RestaurantViewHolder restaurantViewHolder) {
        restaurantViewHolder.binding.ivRestaurantImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        restaurantViewHolder.binding.linLayRestaurantItem.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale));
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    final class RestaurantViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private ListItemRestaurantBinding binding;

        RestaurantViewHolder(@NonNull final ListItemRestaurantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
            });
        }
    }
}