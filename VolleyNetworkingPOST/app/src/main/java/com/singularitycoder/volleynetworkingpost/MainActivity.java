package com.singularitycoder.volleynetworkingpost;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_SELECT_IMAGE = 102;

    private ProgressDialog progressDialog;
    private TextView tvNoInternet;
    private ImageView ivProfileImage;
    private EditText etName, etEmail, etPhone, etPassword;
    private RequestQueue requestQueue;
    private String BASE_URL = "https://reqres.in";
    private byte[] byteArrayImage = new byte[0];
    private byte[] savedInstanceStateByteArrayImage = new byte[0];
    private Uri profileImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
        initializeViews();
        setSavedInstanceState(savedInstanceState);
        setClickListeners();
    }

    private void setStatusBarColor(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        window.requestFeature(window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void initializeViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        tvNoInternet = findViewById(R.id.tv_no_internet);
        ivProfileImage = findViewById(R.id.iv_profile_image);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
    }

    private void setSavedInstanceState(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            AsyncTask.execute(() -> {
                byte[] byteArrayImage = savedInstanceState.getByteArray("userProfileImage");
                savedInstanceStateByteArrayImage = byteArrayImage;
                if (null != byteArrayImage) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArrayImage, 0, byteArrayImage.length, new BitmapFactory.Options());
                    runOnUiThread(() -> ivProfileImage.setImageBitmap(bitmap));
                }
            });
            etName.setText(savedInstanceState.getString("userName"));
            etEmail.setText(savedInstanceState.getString("userEmail"));
            etPhone.setText(savedInstanceState.getString("userPhone"));
            etPassword.setText(savedInstanceState.getString("userPassword"));
            Toast.makeText(this, "State Restored in onCreate", Toast.LENGTH_LONG).show();
        }
    }

    private void setClickListeners() {
        findViewById(R.id.btn_create_account).setOnClickListener(view -> createAccount());
        findViewById(R.id.iv_profile_image).setOnClickListener(view -> checkPermissionsThenDoWork(() -> selectImage(), Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    private Void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_SELECT_IMAGE);
        return null;
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
        builder.setMessage("We need you to grant the permissions for this feature to work!");
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // Multi-window mode, System Font change, Device Orientation change, Theme Change like Dark-Mode
        AsyncTask.execute(() -> outState.putByteArray("userProfileImage", byteArrayProfileImage()));
        outState.putString("userName", valueOf(etName.getText()));
        outState.putString("userEmail", valueOf(etEmail.getText()));
        outState.putString("userPhone", valueOf(etPhone.getText()));
        outState.putString("userPassword", valueOf(etPassword.getText()));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            Toast.makeText(this, "State Restored", Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasValidInput(
            EditText etName,
            EditText etEmail,
            EditText etPhone,
            EditText etPassword) {

        String name = valueOf(etName.getText()).trim();
        String email = valueOf(etEmail.getText()).trim();
        String phone = valueOf(etPhone.getText()).trim();
        String password = valueOf(etPassword.getText()).trim();

        if (("").equals(name)) {
            etName.setError("Name is Required!");
            etName.requestFocus();
            return false;
        }

        if (("").equals(email)) {
            etEmail.setError("Email is Required!");
            etEmail.requestFocus();
            return false;
        }

        if (!hasValidEmail(email)) {
            etEmail.setError("Enter valid Email!");
            etEmail.requestFocus();
            return false;
        }

        if (("").equals(phone)) {
            etPhone.setError("Phone Number is Required!");
            etPhone.requestFocus();
            return false;
        }

        if (phone.length() < 10) {
            etPhone.setError("Enter valid Phone Number!");
            etPhone.requestFocus();
            return false;
        }

        if (("").equals(password)) {
            etPassword.setError("Password is Required!");
            etPassword.requestFocus();
            return false;
        }

        if (!hasValidPassword(password)) {
            etPassword.setError("Password must be > 8 characters, must contain numbers, at least 1 upper case character and at least 1 lower case character!");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean hasValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private boolean hasValidEmail(final String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private void createAccount() {
        if (hasInternet(this)) {
            if (hasValidInput(etName, etEmail, etPhone, etPassword)) {
                AsyncTask.execute(this::setUserDataWithApi);
            }
        } else {
            tvNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private void setUserDataWithApi() {
        requestQueue = VolleyRequestQueue.getInstance(this).getRequestQueue();
        createAccountWithJsonObjectRequest();
        requestQueue.addRequestFinishedListener(request -> {
            if (("application/json").equals(request.getBodyContentType())) {
                runOnUiThread(() -> Toast.makeText(this, "JSON User Created!", Toast.LENGTH_SHORT).show());
                createAccountWithStringRequest();
            } else {
                runOnUiThread(() -> Toast.makeText(this, "String User Created!!", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private byte[] byteArrayProfileImage() {
        if (null == savedInstanceStateByteArrayImage) {
            AsyncTask.execute(() -> {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profileImageUri);
                    runOnUiThread(() -> ((ImageView) findViewById(R.id.iv_profile_image)).setImageBitmap(bitmap));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                    byteArrayImage = stream.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return byteArrayImage;
        } else {
            return savedInstanceStateByteArrayImage;
        }
    }

    private String encodedStringProfileImage() {
        return Base64.encodeToString(byteArrayProfileImage(), Base64.DEFAULT);
    }

    public long getCurrentEpochTime() {
        return System.currentTimeMillis();
    }

    private void createAccountWithJsonObjectRequest() {
        runOnUiThread(() -> {
            if (null != progressDialog && !progressDialog.isShowing()) progressDialog.show();
        });

        String url = BASE_URL + "/api/users";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_profile_image", encodedStringProfileImage());
            jsonObject.put("user_name", valueOf(etName.getText()));
            jsonObject.put("user_email", valueOf(etEmail.getText()));
            jsonObject.put("user_phone", valueOf(etPhone.getText()));
            jsonObject.put("user_password", valueOf(etPassword.getText()));
            jsonObject.put("user_created_at", String.valueOf(getCurrentEpochTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestQueue.add(
                new JsonObjectRequest(
                        url,
                        jsonObject,
                        response -> {
                            if (!("").equals(response)) {
                                runOnUiThread(() -> {
                                    Toast.makeText(MainActivity.this, String.valueOf(response), Toast.LENGTH_LONG).show();
                                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(MainActivity.this, "Something is wrong. Try again!", Toast.LENGTH_SHORT).show();
                                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                                });
                            }
                        },
                        error -> {
                            if (error instanceof NetworkError) {
                                tvNoInternet.setVisibility(View.VISIBLE);
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show());
                            } else {
                                runOnUiThread(() -> {
                                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                                    tvNoInternet.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            }
                        }
                ) {
                    @Override
                    public int getMethod() {
                        return Method.POST;
                    }

                    @Override
                    public Priority getPriority() {
                        return Priority.IMMEDIATE;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                }
        );
    }

    private void createAccountWithStringRequest() {
        runOnUiThread(() -> {
            if (null != progressDialog && !progressDialog.isShowing()) progressDialog.show();
        });

        String url = BASE_URL + "/api/users";

        requestQueue.add(
                new StringRequest(
                        Request.Method.POST,
                        url,
                        response -> {
                            if (!("").equals(response)) {
                                runOnUiThread(() -> {
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(MainActivity.this, "Something is wrong. Try again!", Toast.LENGTH_SHORT).show();
                                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                                });
                            }
                        },
                        error -> {
                            if (error instanceof NetworkError) {
                                tvNoInternet.setVisibility(View.VISIBLE);
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show());
                            } else {
                                runOnUiThread(() -> {
                                    if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                                    tvNoInternet.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            }
                        }
                ) {
                    @Override
                    public Priority getPriority() {
                        return Priority.NORMAL;
                    }

                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_profile_image", encodedStringProfileImage());
                        params.put("user_name", valueOf(etName.getText()));
                        params.put("user_email", valueOf(etEmail.getText()));
                        params.put("user_phone", valueOf(etPhone.getText()));
                        params.put("user_password", valueOf(etPassword.getText()));
                        params.put("user_created_at", String.valueOf(getCurrentEpochTime()));
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                        return headers;
                    }

                    @Override
                    public byte[] getBody() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("user_profile_image", encodedStringProfileImage());
                            jsonObject.put("user_name", valueOf(etName.getText()));
                            jsonObject.put("user_email", valueOf(etEmail.getText()));
                            jsonObject.put("user_phone", valueOf(etPhone.getText()));
                            jsonObject.put("user_password", valueOf(etPassword.getText()));
                            jsonObject.put("user_created_at", String.valueOf(getCurrentEpochTime()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String requestBody = String.valueOf(jsonObject);
                        return requestBody.getBytes(StandardCharsets.UTF_8);
                    }
                }
        );
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
            profileImageUri = data.getData();
            byteArrayProfileImage();
        }
    }
}