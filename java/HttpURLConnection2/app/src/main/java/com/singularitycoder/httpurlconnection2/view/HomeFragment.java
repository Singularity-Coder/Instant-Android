package com.singularitycoder.httpurlconnection2.view;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.singularitycoder.httpurlconnection2.R;
import com.singularitycoder.httpurlconnection2.adapter.RestaurantsAdapter;
import com.singularitycoder.httpurlconnection2.databinding.FragmentHomeBinding;
import com.singularitycoder.httpurlconnection2.helper.AppConstants;
import com.singularitycoder.httpurlconnection2.helper.AppUtils;
import com.singularitycoder.httpurlconnection2.helper.LocationTracker;
import com.singularitycoder.httpurlconnection2.helper.NetworkStateListenerBuilder;
import com.singularitycoder.httpurlconnection2.helper.PermissionsBuilder;
import com.singularitycoder.httpurlconnection2.helper.StateMediator;
import com.singularitycoder.httpurlconnection2.helper.UiState;
import com.singularitycoder.httpurlconnection2.model.ApiResponseModel;
import com.singularitycoder.httpurlconnection2.model.RestaurantModel;
import com.singularitycoder.httpurlconnection2.viewmodel.RestaurantViewModel;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

public final class HomeFragment extends Fragment {

    private double latitude, longitude = 0;

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private final List<RestaurantModel.Restaurant> restaurantList = new ArrayList<>();

    @Nullable
    private RestaurantsAdapter restaurantsAdapter;

    @Nullable
    private RestaurantViewModel restaurantViewModel;

    @Nullable
    private FragmentHomeBinding binding;

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise();
        setUpRecyclerView();
        getLocationAndLoadRestaurants();
        setUpSwipeRefreshLayout();
        setUpListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Causing memory leak
        new NetworkStateListenerBuilder(getContext())
                .setOnlineMobileFunction(() -> null)
                .setOnlineWifiFunction(() -> null)
                .setOfflineFunction(() -> null)
                .build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initialise() {
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
    }

    private void getLocationAndLoadRestaurants() {
        new PermissionsBuilder(getActivity())
                .setPermissionsGrantedFunction(() -> permissionGrantedFunction())
                .setPermissionsDeniedFunction(() -> permissionDeniedFunction())
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .build();
    }

    private void setUpListeners() {
        binding.btnGrantPermissions.setOnClickListener(v -> getLocationAndLoadRestaurants());
    }

    private Void permissionGrantedFunction() {
        binding.btnGrantPermissions.setVisibility(View.GONE);
        final LocationTracker locationTracker = new LocationTracker(getContext());
        if (!locationTracker.canGetLocation()) {
            locationTracker.showSettingsAlert();
            return null;
        }
        latitude = locationTracker.getLatitude();
        longitude = locationTracker.getLongitude();
        getRestaurantListFromApi();
        return null;
    }

    private Void permissionDeniedFunction() {
        binding.btnGrantPermissions.setVisibility(View.VISIBLE);
        return null;
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerRestaurant.setLayoutManager(linearLayoutManager);
        restaurantsAdapter = new RestaurantsAdapter(restaurantList, getContext());
        binding.recyclerRestaurant.setAdapter(restaurantsAdapter);
        binding.recyclerRestaurant.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerRestaurant.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void getRestaurantListFromApi() {
        new NetworkStateListenerBuilder(getContext())
                .setOnlineMobileFunction(() -> showRestaurantListOnlineState(1, latitude, longitude))
                .setOnlineWifiFunction(() -> showRestaurantListOnlineState(1, latitude, longitude))
                .setOfflineFunction(() -> showRestaurantListOfflineState())
                .build();
    }

    private Void showRestaurantListOnlineState(int pageNumber, double latitude, double longitude) {
        binding.tvNoInternet.setVisibility(View.GONE);
        final String url = AppConstants.BASE_URL + "start=" + pageNumber + "&lat=" + latitude + "&lon=" + longitude;
        final String apiKey = AppConstants.API_KEY;
        restaurantViewModel.getRestaurantsFromZomato(getActivity(), url, apiKey).observe(getViewLifecycleOwner(), observeLiveData());
        return null;
    }

    private Void showRestaurantListOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        hideLoading();
        return null;
    }

    private void setUpSwipeRefreshLayout() {
        restaurantList.clear();
        binding.swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.purple_500));
        binding.swipeRefreshLayout.setOnRefreshListener(() -> getLocationAndLoadRestaurants());
    }

    @Nullable
    private Observer<StateMediator<Object, UiState, String, String>> observeLiveData() {
        Observer<StateMediator<Object, UiState, String, String>> observer = null;
        if (appUtils.hasInternet(getContext())) {
            observer = stateMediator -> {
                if (UiState.LOADING == stateMediator.getStatus()) showLoadingState(stateMediator);

                if (UiState.SUCCESS == stateMediator.getStatus()) showSuccessState(stateMediator);

                if (UiState.EMPTY == stateMediator.getStatus()) showEmptyState(stateMediator);

                if (UiState.ERROR == stateMediator.getStatus()) showErrorState(stateMediator);
            };
        }
        return observer;
    }

    private void showLoadingState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> showLoading());
    }

    private void showSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            if ((AppConstants.KEY_GET_RESTAURANT_LIST_API_SUCCESS_STATE).equals(stateMediator.getKey())) {
                showRestaurantListSuccessState(stateMediator);
            }
        });
    }

    private void showRestaurantListSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        hideLoading();
        binding.tvNothing.setVisibility(View.GONE);

        final ApiResponseModel responseObject = (ApiResponseModel) stateMediator.getData();

        if (HttpURLConnection.HTTP_OK == responseObject.getResponseCode()) {
            showRestaurantListHttpOkState(stateMediator, responseObject.getRestaurantResponse());
        }
    }

    private void showRestaurantListHttpOkState(StateMediator<Object, UiState, String, String> stateMediator, RestaurantModel.RestaurantResponse restaurantResponse) {
        if (null == restaurantResponse) return;
        try {
            final List<RestaurantModel.Restaurant> restaurants = restaurantResponse.getRestaurants();
            restaurantList.addAll(restaurants);
            restaurantsAdapter.notifyDataSetChanged();
        } catch (Exception ignored) {
        }

        if (0 == restaurantList.size()) binding.tvNothing.setVisibility(View.VISIBLE);
    }

    private void showEmptyState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            binding.tvNothing.setVisibility(View.VISIBLE);
            binding.tvNothing.setText(getResources().getString(R.string.nothing_to_show));
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            appUtils.showSnack(binding.conLayRestaurantHomeRoot, valueOf(stateMediator.getMessage()), "OK", null);
        });
    }

    private void showErrorState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            binding.tvNothing.setVisibility(View.GONE);
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            appUtils.showSnack(binding.conLayRestaurantHomeRoot, valueOf(stateMediator.getMessage()), "OK", null);
            Log.d(TAG, "liveDataObserver: error: " + stateMediator.getMessage());
        });
    }

    private void showLoading() {
        binding.shimmerLoading.getRoot().setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.shimmerLoading.getRoot().setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }
}