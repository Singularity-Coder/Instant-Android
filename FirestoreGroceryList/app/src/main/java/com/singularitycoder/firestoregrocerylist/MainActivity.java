package com.singularitycoder.firestoregrocerylist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    // Realtime
    // Pagination
    // Security Rules

    private static final String TAG = "MainActivity";

    private final String COLL_GROCERIES = "groceries";
    private final String KEY_GROCERY_ITEM_ID = "id";
    private final String KEY_GROCERY_NAME = "groceryName";
    private final String KEY_GROCERY_QUANTITY = "groceryQuantity";
    private final String KEY_GROCERY_TIME_ADDED = "timeAdded";
    private final String KEY_GROCERY_EPOCH_TIME_ADDED = "epochTimeAdded";

    private RecyclerView recyclerGrocery;
    private GroceryAdapter groceryAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoInternetText;
    private FloatingActionButton btnAddItem;
    private GroceryItemModel groceryItem;
    private ArrayList<GroceryItemModel> groceryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
        initializeViews();
        setUpRecyclerView();
        AsyncTask.execute(this::onNetworkStateChange);
        setSwipeRefreshLayout();
        setClickListeners();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setStatusBarColor(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        window.requestFeature(window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progress_bar);
        btnAddItem = findViewById(R.id.btn_add);
        tvNoInternetText = findViewById(R.id.tv_no_internet);
        recyclerGrocery = findViewById(R.id.recycler_grocery_list);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
    }

    private void setUpRecyclerView() {
        groceryList = new ArrayList<>();
        groceryAdapter = new GroceryAdapter(groceryList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerGrocery.setHasFixedSize(true);
        recyclerGrocery.setLayoutManager(linearLayoutManager);
        recyclerGrocery.setAdapter(groceryAdapter);
        recyclerGrocery.setItemAnimator(new DefaultItemAnimator());
    }

    private void onNetworkStateChange() {
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                if (hasActiveInternetConnection()) {
                    runOnUiThread(() -> {
                        groceryList.clear();
                        AsyncTask.execute(() -> getGroceryList());
                        Toast.makeText(MainActivity.this, "Hello Internet", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        tvNoInternetText.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> {
                    tvNoInternetText.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                });
            }
        };

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }

    private boolean hasActiveInternetConnection() {
        if (hasInternet(this)) {
            try {
                URL url = new URL("http://clients3.google.com/generate_204");
                HttpURLConnection connection = (HttpURLConnection) (url).openConnection();
                connection.setRequestProperty("User-Agent", "Android");
                connection.setRequestProperty("Connection", "close");
                connection.setConnectTimeout(5000);
                connection.connect();
                return (connection.getResponseCode() == 204 && connection.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            runOnUiThread(() -> {
                tvNoInternetText.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            });
        }
        return false;
    }

    public void getGroceryData() {
        if (hasInternet(this)) {
            groceryList.clear();
            AsyncTask.execute(this::getGroceryList);
        } else {
            tvNoInternetText.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setClickListeners() {
        btnAddItem.setOnClickListener(view -> btnAddItem("ADD", "", "", ""));
        groceryAdapter.setOnGroceryItemClick((view, position, itemId) -> getGroceryItemThenUpdate(itemId));
        swipeToDelete();
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this::getGroceryData);
    }

    private void btnAddItem(String positiveButtonText, String itemId, String groceryName, String groceryQuantity) {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        final EditText etGroceryName = new EditText(MainActivity.this);
        etGroceryName.setHint("Type Grocery Name");
        etGroceryName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        LinearLayout.LayoutParams etGroceryNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etGroceryNameParams.setMargins(48, 16, 48, 0);
        etGroceryName.setLayoutParams(etGroceryNameParams);

        final EditText etGroceryQuantity = new EditText(MainActivity.this);
        etGroceryQuantity.setHint("Type Grocery Quantity");
        etGroceryQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams etGroceryQuantityParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etGroceryQuantityParams.setMargins(48, 16, 48, 0);
        etGroceryQuantity.setLayoutParams(etGroceryQuantityParams);

        linearLayout.addView(etGroceryName);
        linearLayout.addView(etGroceryQuantity);

        if (("UPDATE").equals(positiveButtonText.toUpperCase().trim())) {
            etGroceryName.setText(groceryName);
            etGroceryQuantity.setText(groceryQuantity);
        }

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add Grocery Item")
                .setView(linearLayout)  // Drawback with layout params is that its visibly not possible to sanitize input. So use separate xml layout.
                .setPositiveButton(positiveButtonText, (dialog1, which) -> {
                    if (hasInternet(MainActivity.this)) {
                        if (!TextUtils.isEmpty(valueOf(etGroceryName.getText())) && !TextUtils.isEmpty(valueOf(etGroceryQuantity.getText()))) {
                            if (("ADD").equals(positiveButtonText.toUpperCase().trim())) {
                                AsyncTask.execute(() -> addGroceryItem(valueOf(etGroceryName.getText()), valueOf(etGroceryQuantity.getText())));
                            } else {
//                                AsyncTask.execute(() -> updateGroceryItem(valueOf(etGroceryName.getText()), valueOf(etGroceryQuantity.getText()), itemId));
                                AsyncTask.execute(() -> updateWholeGroceryItem(valueOf(etGroceryName.getText()), valueOf(etGroceryQuantity.getText()), itemId));
                            }
                        } else {
                            Toast.makeText(this, "Name and Quantity are Required!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .setCancelable(false)
                .create();
        dialog.show();
    }

    private void swipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                dialogActionMessage(MainActivity.this, "", "Delete this item?", "DELETE", "CANCEL", () -> deleteItem(viewHolder.getAdapterPosition(), "POSITIVE"), () -> deleteItem(viewHolder.getAdapterPosition(), "NEGATIVE"), true);
                Toast.makeText(MainActivity.this, "Grocery Item at position " + viewHolder.getAdapterPosition() + " was deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerGrocery);
    }

    private Void deleteItem(int position, String actionType) {
        if (("POSITIVE").equals(actionType)) {
            runOnUiThread(() -> deleteGroceryItem(groceryList.get(position).getId(), position));
        }

        if (("NEGATIVE").equals(actionType)) {
            groceryAdapter.notifyDataSetChanged();
        }
        return null;
    }

    public void dialogActionMessage(Activity activity, String title, String message, String positiveActionWord, String negativeActionWord, Callable<Void> positiveAction, Callable<Void> negativeAction, boolean cancelableDialog) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveActionWord, (dialog, which) -> {
                    try {
                        positiveAction.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(negativeActionWord, (dialog, which) -> {
                    try {
                        negativeAction.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setCancelable(cancelableDialog)
                .show();
    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    @SuppressLint("SimpleDateFormat")
    private String currentDateTime() {
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String[] arrOfStr = dateTime.split(" ", 2);   // Split date and time for event created date
        ArrayList<String> dateAndTime = new ArrayList<>(Arrays.asList(arrOfStr));

        Date dateObj = null;
        try {
            dateObj = new SimpleDateFormat("yyyy-MM-dd").parse(dateAndTime.get(0));   // Convert date to dd/mm/yyyy
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDate = new SimpleDateFormat("dd MMM yyyy").format(dateObj);
        Log.d(TAG, "date: " + outputDate);

        Date timeObj = null;
        try {
            timeObj = new SimpleDateFormat("H:mm:ss").parse(dateAndTime.get(1));   // Convert time to 12 hr format
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        String outputTime = new SimpleDateFormat("hh:mm a").format(timeObj);
        Log.d(TAG, "time: " + outputTime);

        return outputDate + " at " + outputTime;
    }

    public long getCurrentEpochTime() {
        return System.currentTimeMillis();
    }

    // CREATE
    private void addGroceryItem(String itemName, String itemQty) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        groceryItem = new GroceryItemModel(itemName, itemQty, currentDateTime(), valueOf(getCurrentEpochTime()));
        FirebaseFirestore
                .getInstance()
                .collection(COLL_GROCERIES)
                .add(groceryItem)
                .addOnSuccessListener(documentReference -> {
                    getGroceryData();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Item Added", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to add Item", Toast.LENGTH_SHORT).show());
    }

    // READ
    private void getGroceryList() {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        FirebaseFirestore
                .getInstance()
                .collection(COLL_GROCERIES)
                .orderBy(KEY_GROCERY_EPOCH_TIME_ADDED)  // Always fetch using epoch time to get the list in the right order as this is more accurate time measurement than other time formats
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot docSnap : docList) {
                            groceryItem = docSnap.toObject(GroceryItemModel.class);
                            if (null != groceryItem) groceryItem.setId(docSnap.getId());
                            groceryList.add(groceryItem);
                        }
                        groceryAdapter.notifyDataSetChanged();
                        runOnUiThread(() -> {
                            tvNoInternetText.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Got Grocery List", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        tvNoInternetText.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Nothing to show!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        tvNoInternetText.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                });
    }

    // READ SINGLE ITEM
    private void getGroceryItemThenUpdate(String itemId) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        FirebaseFirestore
                .getInstance()
                .collection(COLL_GROCERIES)
                .document(itemId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (null != documentSnapshot) {
                        btnAddItem("UPDATE", itemId, documentSnapshot.getString(KEY_GROCERY_NAME), documentSnapshot.getString(KEY_GROCERY_QUANTITY));
                        runOnUiThread(() -> {
                            tvNoInternetText.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Got Grocery Item", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        tvNoInternetText.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                });
    }

    // UPDATE WHOLE DOCUMENT
    private void updateWholeGroceryItem(String itemName, String itemQty, String itemId) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        Map<String, Object> groceryItem = new HashMap<>();
        groceryItem.put(KEY_GROCERY_NAME, itemName);
        groceryItem.put(KEY_GROCERY_QUANTITY, itemQty);

        FirebaseFirestore
                .getInstance()
                .collection(COLL_GROCERIES)
                .document(itemId)   // If id is missing then it will create a new document with this id n will succeed
//                .set(groceryItem)   // Completely overrides the item with the above fields in the map. So better to use update method for updating few fields or use the below merge option
                .set(groceryItem, SetOptions.merge()) // Merges the above object with any extra fields in the collection that u want to persist
                .addOnSuccessListener(aVoid -> {
                    getGroceryData();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Item Updated", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // UPDATE DOCUMENT FIELDS
    public void updateGroceryItem(String itemName, String itemQty, String itemId) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        FirebaseFirestore
                .getInstance()
                .collection(COLL_GROCERIES)
                .document(itemId)   // If document with this ID is missing then update method will fail while set method will create a document with that ID if its missing n will succeed
                .update(    // U can create new fields with the update method that actually didn't exist previously
                        KEY_GROCERY_NAME, itemName,
                        KEY_GROCERY_QUANTITY, itemQty
                )
                .addOnSuccessListener(aVoid -> {
                    getGroceryData();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Item Updated", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    // DELETE
    public void deleteGroceryItem(String itemId, int position) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        FirebaseFirestore
                .getInstance()
                .collection(COLL_GROCERIES)
                .document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                    groceryList.remove(position);
                    groceryAdapter.notifyItemRemoved(position);
                    tvNoInternetText.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()));
    }
}
