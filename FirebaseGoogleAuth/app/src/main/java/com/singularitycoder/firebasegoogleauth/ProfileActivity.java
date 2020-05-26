package com.singularitycoder.firebasegoogleauth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_profile);
        setUpGoogleSignIn();
        setIntentDataToViews();
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

    private GoogleSignInClient setUpGoogleSignIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        return googleSignInClient;
    }

    private void setClickListeners() {
        findViewById(R.id.btn_sign_out).setOnClickListener(v -> {
            if (hasInternet(ProfileActivity.this)) {
                runOnUiThread(() -> findViewById(R.id.progress_bar).setVisibility(View.VISIBLE));
                AsyncTask.execute(() -> signOut(setUpGoogleSignIn()));
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn_revoke_access).setOnClickListener(v -> {
            if (hasInternet(ProfileActivity.this)) {
                runOnUiThread(() -> findViewById(R.id.progress_bar).setVisibility(View.VISIBLE));
                AsyncTask.execute(() -> revokeAccess(setUpGoogleSignIn()));
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setIntentDataToViews() {
        FirebaseUser firebaseUser = getIntent().getParcelableExtra("GOOGLE_ACCOUNT_DETAILS");
        if (null != firebaseUser) {     // There is a bug in Firebase. If getPhotoUrl() doesn't work then try getPhoneNumber. Somehow the image url is inside this method.
            if (null != firebaseUser.getPhotoUrl()) {
                AsyncTask.execute(() -> {
                    URL url = null;
                    try {
                        url = new URL(String.valueOf(firebaseUser.getPhotoUrl()));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (null != url) {
                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            runOnUiThread(() -> ((ImageView) findViewById(R.id.iv_image)).setImageBitmap(bmp));
                            runOnUiThread(() -> findViewById(R.id.progress_bar).setVisibility(View.GONE));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            ((TextView) findViewById(R.id.tv_name)).setText(firebaseUser.getDisplayName());
            ((TextView) findViewById(R.id.tv_email)).setText(firebaseUser.getEmail());
        }
    }

    private void signOut(GoogleSignInClient googleSignInClient) {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient
                .signOut()
                .addOnCompleteListener(task -> {
                    runOnUiThread(() -> findViewById(R.id.progress_bar).setVisibility(View.GONE));
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                })
                .addOnFailureListener(e -> runOnUiThread(() -> {
                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void revokeAccess(GoogleSignInClient googleSignInClient) {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient
                .revokeAccess()
                .addOnCompleteListener(task -> {
                    runOnUiThread(() -> findViewById(R.id.progress_bar).setVisibility(View.GONE));
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                })
                .addOnFailureListener(e -> runOnUiThread(() -> {
                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }
}
