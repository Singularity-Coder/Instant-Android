package com.singularitycoder.firebasegoogleauth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
        setUpGoogleSignIn();
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
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("27303614742-k16ohv6o6i102k3kvu4f6engdabp3lqp.apps.googleusercontent.com").requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        return googleSignInClient;
    }

    private void setClickListeners() {
        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> {
            if (hasInternet(MainActivity.this)) {
                runOnUiThread(() -> findViewById(R.id.progress_bar).setVisibility(View.VISIBLE));
                AsyncTask.execute(() -> startActivityForResult(setUpGoogleSignIn().getSignInIntent(), 1010));
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (null != user) {
            startActivity(new Intent(this, ProfileActivity.class).putExtra("GOOGLE_ACCOUNT_DETAILS", user));
            finish();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AsyncTask.execute(() -> {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            FirebaseAuth
                    .getInstance()
                    .signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            runOnUiThread(() -> findViewById(R.id.progress_bar).setVisibility(View.GONE));
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra("GOOGLE_ACCOUNT_DETAILS", firebaseUser));
                            finish();
                        } else {
                            runOnUiThread(() -> {
                                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .addOnFailureListener(e -> runOnUiThread(() -> {
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        });
    }

    private boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Back Pressed
        if (resultCode == RESULT_CANCELED) {
            findViewById(R.id.progress_bar).setVisibility(View.GONE);
            return;
        }

        // Google Sign In
        if (requestCode == 1010 && resultCode == Activity.RESULT_OK) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (null != googleSignInAccount) firebaseAuthWithGoogle(googleSignInAccount);
            } catch (ApiException e) {
                Toast.makeText(this, e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}