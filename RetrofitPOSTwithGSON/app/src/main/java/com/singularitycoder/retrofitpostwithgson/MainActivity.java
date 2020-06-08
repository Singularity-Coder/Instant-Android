package com.singularitycoder.retrofitpostwithgson;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Nullable
    @BindView(R.id.tv_no_internet)
    TextView tvNoInternet;
    @Nullable
    @BindView(R.id.iv_profile_image)
    ImageView ivProfileImage;
    @Nullable
    @BindView(R.id.et_name)
    EditText etName;
    @Nullable
    @BindView(R.id.et_email)
    EditText etEmail;
    @Nullable
    @BindView(R.id.et_phone)
    EditText etPhone;
    @Nullable
    @BindView(R.id.et_password)
    EditText etPassword;
    @Nullable
    @BindView(R.id.btn_create_account)
    Button btnCreateAccount;

    private Unbinder unbinder;
    private ProgressDialog progressDialog;

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
        ButterKnife.bind(this);
        unbinder = ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
    }

    private void setClickListeners() {
        btnCreateAccount.setOnClickListener(view -> createAccount());
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
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private void createAccount() {
        if (hasInternet(this)) {
            if (hasValidInput(etName, etEmail, etPhone, etPassword)) {
                MyViewModel myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
                myViewModel.createAccountFromRepository(
                        encodedProfileImage(),
                        valueOf(etName.getText()),
                        valueOf(etEmail.getText()),
                        valueOf(etPhone.getText()),
                        valueOf(etPassword.getText())
                ).observe(MainActivity.this, liveDataObserver());
            }
        } else {
            tvNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private Observer liveDataObserver() {
        Observer<RequestStateMediator> observer = null;
        if (hasInternet(this)) {
            observer = requestStateMediator -> {

                if (Status.LOADING == requestStateMediator.getStatus()) {
                    runOnUiThread(() -> {
                        progressDialog.setMessage(valueOf(requestStateMediator.getMessage()));
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        if (null != progressDialog && !progressDialog.isShowing()) progressDialog.show();
                    });
                }

                if (Status.SUCCESS == requestStateMediator.getStatus()) {
                    runOnUiThread(() -> {
                        if (("CREATE ACCOUNT").equals(requestStateMediator.getKey())) {
                            Toast.makeText(MainActivity.this, valueOf(requestStateMediator.getData()), Toast.LENGTH_SHORT).show();
                            if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                            tvNoInternet.setVisibility(View.GONE);
                        }
                    });
                }

                if (Status.EMPTY == requestStateMediator.getStatus()) {

                }

                if (Status.ERROR == requestStateMediator.getStatus()) {
                    runOnUiThread(() -> {
                        if (null != progressDialog && progressDialog.isShowing()) progressDialog.dismiss();
                        tvNoInternet.setVisibility(View.GONE);
                        Toast.makeText(this, valueOf(requestStateMediator.getMessage()), Toast.LENGTH_SHORT).show();
                    });
                }
            };
        }
        return observer;
    }

    private String encodedProfileImage() {
        String encodedImage = "";
        Uri uri = Uri.parse("android.resource://com.singularitycoder.postretrofitting/" + R.mipmap.ic_launcher);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] byteArray = stream.toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage;
    }

    private RequestBody sendParametersTypeOne() {
        // U can use a Map instead of JSONObject as well. This is how you pass it to RequestBody - String.valueOf(new JSONObject(mapParams)))
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_profile_image", encodedProfileImage());
            jsonObject.put("user_name", valueOf(etName.getText()));
            jsonObject.put("user_email", valueOf(etEmail.getText()));
            jsonObject.put("user_phone", valueOf(etPhone.getText()));
            jsonObject.put("user_password", valueOf(etPassword.getText()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonObject));
        return body;
    }

    private CreateAccountRequest sendParametersTypeTwo() {
        CreateAccountRequest createAccountRequest = new CreateAccountRequest(
                encodedProfileImage(),
                valueOf(etName.getText()),
                valueOf(etEmail.getText()),
                valueOf(etPhone.getText()),
                valueOf(etPassword.getText())
        );
        return createAccountRequest;
    }

    private HashMap<String, String> sendParametersTypeThree() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("user_profile_image", encodedProfileImage());
        parameters.put("user_name", valueOf(etName.getText()));
        parameters.put("user_email", valueOf(etEmail.getText()));
        parameters.put("user_phone", valueOf(etPhone.getText()));
        parameters.put("user_password", valueOf(etPassword.getText()));
        return parameters;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
