package com.singularitycoder.firebasestorage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_SELECT_IMAGE = 102;
    private static final int REQUEST_CODE_SELECT_AUDIO = 103;
    private static final int REQUEST_CODE_SELECT_VIDEO = 104;
    private static final int REQUEST_CODE_SELECT_DOCUMENT = 105;

    private static final String COLL_FILES = "MyFiles";
    private static final String DIR_IMAGES = "MyPhotos/";
    private static final String DIR_AUDIOS = "MyAudios/";
    private static final String DIR_VIDEOS = "MyVideos/";
    private static final String DIR_DOCUMENTS = "MyDocuments/";

    private static final String FILE_TYPE_IMAGE = "IMG";
    private static final String FILE_TYPE_AUDIO = "AUD";
    private static final String FILE_TYPE_VIDEO = "VID";
    private static final String FILE_TYPE_DOCUMENT = "DOC";

    private FloatingActionButton btnAdd;
    private RecyclerView recyclerFileList;
    private FilesAdapter fileAdapter;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoInternetText, tvNothing;

    private List<FileItem> fileList = new ArrayList<>();
    private List<FileItem> fileToUploadList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
        initializeViews();
        setUpRecyclerView();
        setPlaceHolder();
        showHideFab();
        onNetworkStateChange();
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
        btnAdd = findViewById(R.id.btn_add);
        recyclerFileList = findViewById(R.id.recycler_file_list);
        tvNoInternetText = findViewById(R.id.tv_no_internet);
        tvNothing = findViewById(R.id.tv_nothing);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    private void setUpRecyclerView() {
        fileAdapter = new FilesAdapter(fileList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerFileList.setLayoutManager(linearLayoutManager);
        recyclerFileList.setHasFixedSize(true);
        recyclerFileList.setAdapter(fileAdapter);
        recyclerFileList.setItemAnimator(new DefaultItemAnimator());
    }

    private void setPlaceHolder() {
        fileAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (0 == fileAdapter.getItemCount()) {
                    tvNothing.setVisibility(View.VISIBLE);
                    fileList.clear();
                } else {
                    tvNothing.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showHideFab() {
        recyclerFileList.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    btnAdd.hide();
                else if (dy < 0)
                    btnAdd.show();
            }
        });
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this::getFileData);
    }

    private void setClickListeners() {
        btnAdd.setOnClickListener(view -> dialogAddFilesToUpload());
        fileAdapter.setOnFileItemClick(url -> showInChromeBrowser(url));
        swipeToDelete();
    }

    private void showInChromeBrowser(String url) {
        if (!("").equals(url)) {
            URLEncoder.encode(url);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                // If Chrome not installed
                intent.setPackage(null);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Missing URL!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onNetworkStateChange() {
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                if (hasActiveInternetConnection()) {
                    runOnUiThread(() -> {
                        fileList.clear();
                        AsyncTask.execute(() -> getFileListFromFirestore());
                        Toast.makeText(MainActivity.this, "Hello Internet", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        tvNoInternetText.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> {
                    tvNoInternetText.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
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
                if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            });
        }
        return false;
    }

    public void getFileData() {
        if (hasInternet(this)) {
            fileList.clear();
            AsyncTask.execute(this::getFileListFromFirestore);
        } else {
            tvNoInternetText.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
        }
    }

    // UPLOAD TO FIREBASE STORAGE
    private void uploadFiles(String fileDirectory) {
        runOnUiThread(() -> {
            if (null != progressDialog && !progressDialog.isShowing()) progressDialog.show();
            progressDialog.setMessage("Uploaded 0/" + fileToUploadList.size());
        });
        if (0 != fileToUploadList.size()) {
            for (int i = 0; i < fileToUploadList.size(); i++) {
                final int finalI = i;
                FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child(fileDirectory)
                        .child(fileToUploadList.get(i).getFileName())
                        .putFile(fileToUploadList.get(i).getFileUri())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                runOnUiThread(() -> progressDialog.setMessage("Uploaded " + finalI + "/" + fileToUploadList.size()));
                                Uri uri = task.getResult().getUploadSessionUri();
                                Log.d(TAG, "uploadImages: uri: " + uri);
                                getUriFromFirebaseStorage(finalI, fileDirectory);
                            } else {
                                runOnUiThread(() -> {
                                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Couldn't upload " + fileToUploadList.get(finalI).getFileName(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        })
                        .addOnFailureListener(e -> runOnUiThread(() -> {
                            if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }))
                        .addOnProgressListener(taskSnapshot -> runOnUiThread(() -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }));
            }
        } else {
            runOnUiThread(() -> {
                if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                Toast.makeText(this, "Upload something!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // GET URI FROM FIREBASE STORAGE
    private void getUriFromFirebaseStorage(int iterationCount, String fileDirectory) {
        FirebaseStorage
                .getInstance()
                .getReference()
                .child(fileDirectory)
                .child(fileToUploadList.get(iterationCount).getFileName())
                .getDownloadUrl()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        runOnUiThread(() -> progressDialog.setMessage("Uploaded " + iterationCount + "/" + fileToUploadList.size()));
                        String fileUrl = valueOf(task1.getResult());
                        addFileItemToFirestore(iterationCount, fileUrl);
                        Log.d(TAG, "getUriFromFirebaseStorage: uri: " + task1.getResult().toString());
                    } else {
                        deleteFromFirebaseStorage(iterationCount, fileDirectory);
                        runOnUiThread(() -> {
                            if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                            fileToUploadList.clear();
                            Toast.makeText(MainActivity.this, "Couldn't save " + fileToUploadList.get(iterationCount).getFileName(), Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    fileToUploadList.clear();
                }));
    }

    // DELETE FROM FIREBASE STORAGE
    private void deleteFromFirebaseStorage(int iterationCount, String fileDirectory) {
        FirebaseStorage
                .getInstance()
                .getReference()
                .child(fileDirectory)
                .child(fileToUploadList.get(iterationCount).getFileName())
                .delete();
        runOnUiThread(() -> fileToUploadList.clear());
    }

    // CREATE FROM FIRESTORE
    private void addFileItemToFirestore(int iterationCount, String fileUrl) {
        runOnUiThread(() -> {
            if (null != progressDialog && !progressDialog.isShowing()) progressDialog.show();
        });
        FileItem fileItem = new FileItem(fileToUploadList.get(iterationCount).getFileType(), fileToUploadList.get(iterationCount).getFileName(), fileToUploadList.get(iterationCount).getTimeAdded(), fileToUploadList.get(iterationCount).getEpochTimeAdded(), fileToUploadList.get(iterationCount).getFileUri());
        fileItem.setFileUrl(fileUrl);
        FirebaseFirestore
                .getInstance()
                .collection(COLL_FILES)
                .add(fileItem)
                .addOnSuccessListener(documentReference -> {
                    getFileData();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Item Added", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> runOnUiThread(() -> {
                    if (null != progressDialog && progressDialog.isShowing())
                        progressDialog.dismiss();
                    fileToUploadList.clear();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    // READ FROM FIRESTORE
    private void getFileListFromFirestore() {
        runOnUiThread(() -> {
            if (null != progressDialog && !progressDialog.isShowing()) progressDialog.show();
        });
        FirebaseFirestore
                .getInstance()
                .collection(COLL_FILES)
                .orderBy("epochTimeAdded")  // Always fetch using epoch time to get the list in the right order as this is more accurate time measurement than other time formats. Add a field in Firestore.
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        fileList.clear();
                        List<DocumentSnapshot> docList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot docSnap : docList) {
                            FileItem fileItem = docSnap.toObject(FileItem.class);
                            if (null != fileItem) fileItem.setId(docSnap.getId());
                            fileList.add(fileItem);
                        }
                        fileAdapter.notifyDataSetChanged();
                        runOnUiThread(() -> {
                            tvNoInternetText.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Got File List", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            fileList.clear();
                            fileAdapter.notifyDataSetChanged();
                            tvNoInternetText.setVisibility(View.GONE);
                            tvNothing.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                            if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                            Toast.makeText(this, "Nothing to show!", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> runOnUiThread(() -> {
                    tvNoInternetText.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    // DELETE FROM FIRESTORE
    public void deleteFileItemFromFirestore(String itemId, String fileType, int position) {
        runOnUiThread(() -> {
            if (null != progressDialog && !progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Deleting item...");
            }
        });

        String fileDirectory = "";
        if ((FILE_TYPE_IMAGE).equals(fileType)) {
            fileDirectory = DIR_IMAGES;
        }

        if ((FILE_TYPE_AUDIO).equals(fileType)) {
            fileDirectory = DIR_AUDIOS;
        }

        if ((FILE_TYPE_VIDEO).equals(fileType)) {
            fileDirectory = DIR_VIDEOS;
        }

        if ((FILE_TYPE_DOCUMENT).equals(fileType)) {
            fileDirectory = DIR_DOCUMENTS;
        }

        String finalFileDirectory = fileDirectory;
        FirebaseFirestore
                .getInstance()
                .collection(COLL_FILES)
                .document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> runOnUiThread(() -> {
                    FirebaseStorage
                            .getInstance()
                            .getReference()
                            .child(finalFileDirectory)
                            .child(fileList.get(position).getFileName())
                            .delete();
                    fileList.remove(position);
                    fileAdapter.notifyItemRemoved(position);
                    tvNoInternetText.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                    if (0 == fileList.size()) {
                        fileList.clear();
                        tvNothing.setVisibility(View.VISIBLE);
                    }
                }))
                .addOnFailureListener(e -> runOnUiThread(() -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()));
    }

    private void checkPermissionsThenDoWork(Callable<Void> work, String permission) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
            try {
                work.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!this.shouldShowRequestPermissionRationale(permission)) {
                    showSettingsDialog(MainActivity.this);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, REQUEST_CODE_PERMISSIONS);
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, REQUEST_CODE_PERMISSIONS);
            }
        }
    }

    public void showSettingsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Give Permissions!");
        builder.setMessage("We need you to grant the permissions for the camera feature to work!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            openDeviceSettings(context);
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void openDeviceSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
                Toast.makeText(MainActivity.this, "File Item at position " + viewHolder.getAdapterPosition() + " was deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerFileList);
    }

    private Void deleteItem(int position, String actionType) {
        if (("POSITIVE").equals(actionType)) {
            AsyncTask.execute(() -> deleteFileItemFromFirestore(fileList.get(position).getId(), fileList.get(position).getFileType(), position));
        }

        if (("NEGATIVE").equals(actionType)) {
            fileAdapter.notifyDataSetChanged();
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

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeType = MimeTypeMap.getSingleton();
        return mimeType.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private Void selectImageTypeOne() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_SELECT_IMAGE);
        return null;
    }

    private Void selectAudioTypeOne() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), REQUEST_CODE_SELECT_AUDIO);
        return null;
    }

    private Void selectVideoTypeOne() {
        Intent intent = new Intent();
        intent.setType("Video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE_SELECT_VIDEO);
        return null;
    }

    private Void selectDocumentTypeOne() {
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), REQUEST_CODE_SELECT_DOCUMENT);
        return null;
    }

    private Void selectImageTypeTwo() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_SELECT_IMAGE);
        return null;
    }

    private Void selectAudioTypeTwo() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), REQUEST_CODE_SELECT_AUDIO);
        return null;
    }

    private Void selectVideoTypeTwo() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE_SELECT_VIDEO);
        return null;
    }

    private Void selectDocumentTypeTwo() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Files.getContentUri("external"));
        startActivityForResult(Intent.createChooser(intent, "Select Document"), REQUEST_CODE_SELECT_DOCUMENT);
        return null;
    }

    private void dialogAddFilesToUpload() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload");
        String[] selectArray = {"Images", "Audios", "Videos", "Documents"};
        builder.setItems(selectArray, (dialog, which) -> {
            switch (which) {
                case 0:
                    fileToUploadList.clear();
                    checkPermissionsThenDoWork(() -> selectImageTypeOne(), Manifest.permission.READ_EXTERNAL_STORAGE);
//                    checkPermissionsThenDoWork(() -> selectImageTypeTwo(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    break;
                case 1:
                    fileToUploadList.clear();
                    checkPermissionsThenDoWork(() -> selectAudioTypeOne(), Manifest.permission.READ_EXTERNAL_STORAGE);
//                    checkPermissionsThenDoWork(() -> selectAudioTypeTwo(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    break;
                case 2:
                    fileToUploadList.clear();
                    checkPermissionsThenDoWork(() -> selectVideoTypeOne(), Manifest.permission.READ_EXTERNAL_STORAGE);
//                    checkPermissionsThenDoWork(() -> selectVideoTypeTwo(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    break;
                case 3:
                    fileToUploadList.clear();
                    checkPermissionsThenDoWork(() -> selectDocumentTypeOne(), Manifest.permission.READ_EXTERNAL_STORAGE);
//                    checkPermissionsThenDoWork(() -> selectDocumentTypeTwo(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // BACK PRESSED
        if (requestCode == RESULT_CANCELED) {
            return;
        }

        // IMAGE
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && null != data) {
            if (null != data.getClipData()) {
                // Multiple Images
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    FileItem fileItem = new FileItem(FILE_TYPE_IMAGE, "Image_" + getCurrentEpochTime() + "." + getFileExtension(uri), currentDateTime(), valueOf(getCurrentEpochTime()), uri);
                    fileToUploadList.add(fileItem);
                }
                AsyncTask.execute(() -> uploadFiles(DIR_IMAGES));
            } else {
                // Single Image
                Uri uri = data.getData();
                FileItem fileItem = new FileItem(FILE_TYPE_IMAGE, "Image_" + getCurrentEpochTime() + "." + getFileExtension(uri), currentDateTime(), valueOf(getCurrentEpochTime()), uri);
                fileToUploadList.add(fileItem);
                AsyncTask.execute(() -> uploadFiles(DIR_IMAGES));
            }
        }

        // AUDIO
        if (requestCode == REQUEST_CODE_SELECT_AUDIO && resultCode == RESULT_OK && null != data) {
            if (null != data.getData()) {
                Uri uri = data.getData();
                FileItem fileItem = new FileItem(FILE_TYPE_AUDIO, "Audio_" + getCurrentEpochTime() + "." + getFileExtension(uri), currentDateTime(), valueOf(getCurrentEpochTime()), uri);
                fileToUploadList.add(fileItem);
                AsyncTask.execute(() -> uploadFiles(DIR_AUDIOS));
            } else {
                Toast.makeText(this, "No audio chosen", Toast.LENGTH_SHORT).show();
            }
        }

        // VIDEO
        if (requestCode == REQUEST_CODE_SELECT_VIDEO && resultCode == RESULT_OK && null != data) {
            if (null != data.getData()) {
                Uri uri = data.getData();
                FileItem fileItem = new FileItem(FILE_TYPE_VIDEO, "Video_" + getCurrentEpochTime() + "." + getFileExtension(uri), currentDateTime(), valueOf(getCurrentEpochTime()), uri);
                fileToUploadList.add(fileItem);
                AsyncTask.execute(() -> uploadFiles(DIR_VIDEOS));
            } else {
                Toast.makeText(this, "No video chosen", Toast.LENGTH_SHORT).show();
            }
        }

        // DOCUMENT
        if (requestCode == REQUEST_CODE_SELECT_DOCUMENT && resultCode == RESULT_OK && null != data) {
            if (null != data.getData()) {
                Uri uri = data.getData();
                FileItem fileItem = new FileItem(FILE_TYPE_DOCUMENT, "Document_" + getCurrentEpochTime() + "." + getFileExtension(uri), currentDateTime(), valueOf(getCurrentEpochTime()), uri);
                fileToUploadList.add(fileItem);
                AsyncTask.execute(() -> uploadFiles(DIR_DOCUMENTS));
            } else {
                Toast.makeText(this, "No document chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}