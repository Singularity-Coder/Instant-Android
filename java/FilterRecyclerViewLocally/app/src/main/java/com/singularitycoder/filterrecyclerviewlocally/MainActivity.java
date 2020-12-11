package com.singularitycoder.filterrecyclerviewlocally;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.jakewharton.rxbinding3.widget.TextViewTextChangeEvent;
import com.singularitycoder.filterrecyclerviewlocally.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static java.lang.String.valueOf;

public final class MainActivity extends AppCompatActivity implements FiltersDialogFragment.AlertDialogListener {

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private final List<ProductItem> productList = new ArrayList<>();

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    private ProductAdapter productAdapter;

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(this, R.color.colorPrimaryDark);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpRecyclerView();
        setProductData();
        performSearchObservable();
        setUpClickListeners();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setStatusBarColor(Activity activity, int statusBarColor) {
        final Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        window.requestFeature(window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setUpRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productList, this);
        binding.recyclerView.setAdapter(productAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setUpClickListeners() {
        compositeDisposable.add(
                RxView.clicks(binding.fabFilter)
                        .map(o -> binding.fabFilter)
                        .subscribe(
                                button -> showFilterDialog(),
                                throwable -> Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                        )
        );
    }

    public void showFilterDialog() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "simpleAlert");
        bundle.putString("KEY_DIALOG_ALERT_TYPE", "FilterDialog");
        bundle.putString("KEY_CONTEXT_TYPE", "activity");
        bundle.putString("KEY_CONTEXT_OBJECT", "MainActivity");
        bundle.putString("KEY_TITLE", "Filter Products");
        bundle.putString("KEY_MESSAGE", "");
        bundle.putString("KEY_POSITIVE_BUTTON_TEXT", "APPLY");
        bundle.putString("KEY_NEGATIVE_BUTTON_TEXT", "CANCEL");
        bundle.putString("KEY_NEUTRAL_BUTTON_TEXT", "RESET");

        final DialogFragment dialogFragment = new FiltersDialogFragment();
        dialogFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        final Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_MainActivity");
        if (null != previousFragment) fragmentTransaction.remove(previousFragment);
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_MainActivity");
    }

    private void setProductData() {
        productList.add(new ProductItem("Star Ship", "100000.000", "Space Ships", "20/07/20", "00:23", "35"));
        productList.add(new ProductItem("Laser Beams", "300000.000", "Space Ships", "10/07/20", "00:23", "35"));
        productList.add(new ProductItem("Capsule Houses", "200000.000", "Space Ships", "15/07/20", "00:23", "35"));
        productList.add(new ProductItem("Quantum Computer T300", "400000.000", "Space Ships", "20/07/20", "00:23", "35"));
        productList.add(new ProductItem("Air Ship", "600000.000", "Space Ships", "20/07/20", "00:23", "25"));
        productList.add(new ProductItem("Death Star", "500000.000", "Space Ships", "20/07/20", "00:23", "35"));
        productList.add(new ProductItem("Baloon Shooter", "700000.000", "Space Ships", "22/07/20", "00:23", "55"));
        productList.add(new ProductItem("Gauge", "900000.000", "Space Ships", "20/07/20", "00:23", "35"));
        productList.add(new ProductItem("Power Reader", "800000.000", "Space Ships", "20/07/20", "00:23", "75"));

        productAdapter.notifyDataSetChanged();
        binding.tvSearchResults.setText("Results: " + productList.size());
    }

    private void performSearchObservable() {
        compositeDisposable.add(
                RxTextView
                        .textChangeEvents(binding.etSearch)
                        .skipInitialValue()
                        .filter(s -> s.toString().length() > 1)
                        .distinctUntilChanged()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(performSearchObserver())
        );
    }

    private DisposableObserver<TextViewTextChangeEvent> performSearchObserver() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                searchProducts(valueOf(textViewTextChangeEvent.getText()));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void searchProducts(@NonNull final String filteredText) {
        final List<ProductItem> filteredList = new ArrayList<>();
        for (ProductItem item : productList) {
            if (item.getName().toLowerCase().trim().contains(filteredText.toLowerCase().trim())) {
                filteredList.add(item);
            }
        }
        productAdapter.filterList(filteredList);
        productAdapter.notifyDataSetChanged();
        binding.tvSearchResults.setText("Results: " + filteredList.size());
    }

    private void filterLocally(@NonNull final Bundle bundle) {
        final String selectedProximity = bundle.getString("KEY_PROXIMITY");
        final String selectedCategory = bundle.getString("KEY_CATEGORY");
        final String selectedPrice = bundle.getString("KEY_PRICE");
        final String selectedPriceRange = bundle.getString("KEY_PRICE_RANGE");
        final String selectedAlphabeticOrder = bundle.getString("KEY_ALPHABET");
        final String selectedDate = bundle.getString("KEY_DATE");
        final String selectedTime = bundle.getString("KEY_TIME");

        final List<ProductItem> filteredList = new ArrayList<>();

        if (("All Categories").equals(selectedCategory)
                && ("All Prices").equals(selectedPrice)
                && ("0").equals(selectedProximity)
                && ("NONE").equals(selectedPriceRange)
                && ("NONE").equals(selectedDate)
                && ("NONE").equals(selectedTime)) {
            productList.clear();
            productAdapter.filterList(productList);
            productAdapter.notifyDataSetChanged();
        } else {
            filterByAlphabeticOrder(selectedAlphabeticOrder, filteredList);
            filterByPriceRange(selectedPriceRange, filteredList);
            filterByProximity(selectedProximity, filteredList);
            filterByCategory(selectedCategory, filteredList);
            filterByPrice(selectedPrice, filteredList);
            filterByDate(selectedDate, filteredList);
            filterByTime(selectedTime, filteredList);
        }

        binding.tvSearchResults.setText("Results: " + productList.size() + ", "
                + "Proximity: " + selectedProximity + " meters, "
                + "Category: " + selectedCategory + ", "
                + "Price: " + selectedPrice + " USD, "
                + "PriceRange: " + selectedPriceRange + ", "
                + "Date: " + selectedDate + ", "
                + "Time: " + selectedTime);
    }

    private void filterByAlphabeticOrder(@NonNull final String selectedAlphabeticOrder, @NonNull final List<ProductItem> filteredList) {

    }

    private void filterByProximity(@NonNull final String selectedProximity, @NonNull final List<ProductItem> filteredList) {
        if (("0").equals(selectedProximity)) {

        } else {
            for (ProductItem item : productList) {
                if (Integer.parseInt(selectedProximity) <= 20) {
                    if (Integer.parseInt(item.getProximity()) <= 20) filteredList.add(item);
                }

                if (Integer.parseInt(selectedProximity) <= 40) {
                    if (Integer.parseInt(item.getProximity()) <= 40) filteredList.add(item);
                }

                if (Integer.parseInt(selectedProximity) <= 60) {
                    if (Integer.parseInt(item.getProximity()) <= 60) filteredList.add(item);
                }

                if (Integer.parseInt(selectedProximity) <= 80) {
                    if (Integer.parseInt(item.getProximity()) <= 80) filteredList.add(item);
                }

                if (Integer.parseInt(selectedProximity) <= 100) {
                    if (Integer.parseInt(item.getProximity()) <= 100) filteredList.add(item);
                }
            }
            productAdapter.filterList(filteredList);
            productAdapter.notifyDataSetChanged();
        }
    }

    private void filterByCategory(@NonNull final String selectedCategory, @NonNull final List<ProductItem> filteredList) {
        if (("All Categories").equals(selectedCategory)) {

        } else {
            for (ProductItem item : productList) {
                if (item.getCategory().trim().equalsIgnoreCase(selectedCategory.toLowerCase().trim())) {
                    filteredList.add(item);
                }
            }
            productAdapter.filterList(filteredList);
            productAdapter.notifyDataSetChanged();
        }
    }

    private void filterByPrice(@NonNull final String selectedPrice, @NonNull final List<ProductItem> filteredList) {
        if (("All Prices").equals(selectedPrice)) {

        } else if (("Its Over 9000").toLowerCase().trim().equalsIgnoreCase(selectedPrice.trim())) {
            for (ProductItem item : productList) {
                if (Math.round(Double.parseDouble(item.getPrice())) > 9000) filteredList.add(item);
            }
            productAdapter.filterList(filteredList);
            productAdapter.notifyDataSetChanged();
        } else {
            for (ProductItem item : productList) {
                if (selectedPrice.equals(valueOf(Math.round(Double.parseDouble(item.getPrice()))))) {
                    filteredList.add(item);
                }
            }
            productAdapter.filterList(filteredList);
            productAdapter.notifyDataSetChanged();
        }
    }

    private void filterByPriceRange(@NonNull final String selectedPriceRange, @NonNull final List<ProductItem> filteredList) {
        if (0 == productList.size()) return;

        if (("HighToLow").equals(selectedPriceRange)) {
            ProductItem small;
            for (int i = 0; i < productList.size(); i++) {
                for (int j = i + 1; j < productList.size(); j++) {
                    if (Math.round(Double.parseDouble(productList.get(i).getPrice())) < Math.round(Double.parseDouble(productList.get(j).getPrice()))) {
                        small = productList.get(i);
                        productList.set(i, productList.get(j));
                        productList.set(j, small);
                    }
                }
            }

            filteredList.addAll(productList);
            productAdapter.filterList(filteredList);
            productAdapter.notifyDataSetChanged();
        }

        if (("LowToHigh").equals(selectedPriceRange)) {
            ProductItem large;
            for (int i = 0; i < productList.size(); i++) {
                for (int j = i + 1; j < productList.size(); j++) {
                    if (Math.round(Double.parseDouble(productList.get(i).getPrice())) > Math.round(Double.parseDouble(productList.get(j).getPrice()))) {
                        large = productList.get(i);
                        productList.set(i, productList.get(j));
                        productList.set(j, large);
                    }
                }
            }

            filteredList.addAll(productList);
            productAdapter.filterList(filteredList);
            productAdapter.notifyDataSetChanged();
        }
    }

    private void filterByDate(@NonNull final String selectedDate, @NonNull final List<ProductItem> filteredList) {
        for (ProductItem item : productList) {
            if (item.getDate().equals(selectedDate)) filteredList.add(item);
        }
        productAdapter.filterList(filteredList);
        productAdapter.notifyDataSetChanged();
    }

    private void filterByTime(@NonNull final String selectedTime, @NonNull final List<ProductItem> filteredList) {
        for (ProductItem item : productList) {
            if (item.getTime().equals(selectedTime)) filteredList.add(item);
        }
        productAdapter.filterList(filteredList);
        productAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    public void onAlertDialogPositiveClick(String dialogType, DialogFragment dialog, Bundle bundle) {
        if (("FilterDialog").equals(bundle.getString("KEY_DIALOG_ALERT_TYPE"))) {
            filterLocally(bundle);
        }
    }

    @Override
    public void onAlertDialogNegativeClick(String dialogType, DialogFragment dialog, Bundle bundle) {

    }

    @Override
    public void onAlertDialogNeutralClick(String dialogType, DialogFragment dialog, Bundle bundle) {
        if (("FilterDialog").equals(bundle.getString("KEY_DIALOG_ALERT_TYPE"))) {
            binding.etSearch.setText("");
            productList.clear();
            setProductData();
        }
    }
}