package com.singularitycoder.phpmysqlconnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText etEmail, etUsername, etPassword;
    private Button btnAuthenticate, btnAlreadyRegistered;
    private ProgressDialog progressDialog;
    private TextView tvNoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
        initializeViews();
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
        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnAuthenticate = findViewById(R.id.btn_authenticate);
        btnAlreadyRegistered = findViewById(R.id.btn_already_registered);
        tvNoInternet = findViewById(R.id.tv_no_internet);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        btnAuthenticate.setText("SignUp");
        btnAlreadyRegistered.setText("Already Registered? LogIn");
    }

    private void setClickListeners() {
        btnAuthenticate.setOnClickListener(view -> btnAuthenticate());
        btnAlreadyRegistered.setOnClickListener(view -> btnAlreadyRegistered());
    }

    private void btnAuthenticate() {
        if (("signup").equals(valueOf(btnAuthenticate.getText()).toLowerCase().trim())) {
            if (hasInternet(MainActivity.this)) {
                tvNoInternet.setVisibility(View.GONE);
                if (hasValidInput(etEmail, etUsername, etPassword, "SIGNUP")) {
                    AsyncTask.SERIAL_EXECUTOR.execute(this::signUpWithApi);
                }
            } else {
                tvNoInternet.setVisibility(View.VISIBLE);
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (hasInternet(MainActivity.this)) {
                tvNoInternet.setVisibility(View.GONE);
                if (hasValidInput(etEmail, etUsername, etPassword, "SIGNIN")) {
                    AsyncTask.SERIAL_EXECUTOR.execute(this::signInInWithApi);
                }
            } else {
                tvNoInternet.setVisibility(View.VISIBLE);
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void btnAlreadyRegistered() {
        if (("signup").equals(valueOf(btnAuthenticate.getText()).toLowerCase().trim())) {
            etEmail.setVisibility(View.GONE);
            btnAuthenticate.setText("SignIn");
            btnAlreadyRegistered.setText("Not a member? Create New Account");
        } else {
            etEmail.setVisibility(View.VISIBLE);
            btnAuthenticate.setText("SignUp");
            btnAlreadyRegistered.setText("Already Registered? LogIn");
        }
    }

    private boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean hasValidInput(EditText etEmail, EditText etUsername, EditText etPassword, String authKey) {
        String email = valueOf(etEmail.getText()).trim();
        String username = valueOf(etUsername.getText()).trim();
        String password = valueOf(etPassword.getText()).trim();

        if (("SIGNUP").equals(authKey)) {
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
        }

        if (("").equals(username)) {
            etUsername.setError("Username is Required!");
            etUsername.requestFocus();
            return false;
        }

        if (username.length() < 6) {
            etUsername.setError("Username must be at least 6 characters!");
            etUsername.requestFocus();
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
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private void goToHomeActivity() {
        MainActivity.this.startActivity(new Intent(MainActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        MainActivity.this.finish();
    }

    private RequestBody sendSignUpParameters() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", valueOf(etUsername.getText()));
            jsonObject.put("email", valueOf(etEmail.getText()));
            jsonObject.put("password", valueOf(etPassword.getText()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), valueOf(jsonObject));
        return body;
    }

    private RequestBody sendSignInParameters() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", valueOf(etUsername.getText()));
            jsonObject.put("password", valueOf(etPassword.getText()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), valueOf(jsonObject));
        return body;
    }

    private void signUpWithApi() {
        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
        Call<String> call = apiService.signUpEndPoint(sendSignUpParameters());
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                runOnUiThread(() -> {
                    if (null == progressDialog) progressDialog.show();
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (null != progressDialog) progressDialog.dismiss();
                            goToHomeActivity();
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (null != progressDialog) progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Something is wrong. Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                runOnUiThread(() -> {
                    if (null != progressDialog) progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void signInInWithApi() {
        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
        Call<String> call = apiService.signInEndPoint(sendSignInParameters());
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                runOnUiThread(() -> {
                    if (null == progressDialog) progressDialog.show();
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (null != progressDialog) progressDialog.dismiss();
                            goToHomeActivity();
                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (null != progressDialog) progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Something is wrong. Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                runOnUiThread(() -> {
                    if (null != progressDialog) progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}