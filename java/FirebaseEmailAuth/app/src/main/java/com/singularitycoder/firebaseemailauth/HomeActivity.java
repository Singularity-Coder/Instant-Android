package com.singularitycoder.firebaseemailauth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import static java.lang.String.valueOf;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private Button btnUpdateEmail, btnChangePassword, btnRemoveUser, btnSignOut;
    private TextView tvEmail;
    private ProgressDialog progressDialog;

    private FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (null == firebaseAuth.getCurrentUser()) goToMainActivity();
            else tvEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_home);
        initializeViews();
        checkIfUserExists();
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
        btnUpdateEmail = findViewById(R.id.btn_update_email);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnRemoveUser = findViewById(R.id.btn_remove_user);
        btnSignOut = findViewById(R.id.btn_sign_out);
        tvEmail = findViewById(R.id.tv_email);

        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
    }

    private void checkIfUserExists() {
        authListener = firebaseAuth -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) goToMainActivity();
            else tvEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        };
    }

    private void setClickListeners() {
        btnUpdateEmail.setOnClickListener(view -> btnUpdateEmail());
        btnChangePassword.setOnClickListener(view -> btnChangePassword());
        btnRemoveUser.setOnClickListener(view -> removeUser());
        btnSignOut.setOnClickListener(view -> signOut());
    }

    private void btnUpdateEmail() {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        final EditText etUpdateEmail = new EditText(HomeActivity.this);
        etUpdateEmail.setHint("Type New Email");
        etUpdateEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        LinearLayout.LayoutParams etUpdateEmailParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etUpdateEmailParams.setMargins(48, 16, 48, 0);
        etUpdateEmail.setLayoutParams(etUpdateEmailParams);

        linearLayout.addView(etUpdateEmail);

        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Update Email")
                .setMessage("Enter new Email ID!")
                .setView(linearLayout)
                .setPositiveButton("UPDATE", (dialog1, which) -> {
                    if (hasInternet(HomeActivity.this)) {
                        if (!TextUtils.isEmpty(valueOf(etUpdateEmail.getText()))) {
                            AsyncTask.execute(() -> updateEmail(dialog1, valueOf(etUpdateEmail.getText())));
                        } else {
                            Toast.makeText(this, "Email is Required!", Toast.LENGTH_SHORT).show();
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

    private void updateEmail(DialogInterface dialog, String newEmail) {
        FirebaseAuth
                .getInstance()
                .getCurrentUser()
                .updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            signOut();
                        });
                    } else {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(HomeActivity.this, "Failed to update Email!", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "updateEmail: trace: " + e.getMessage()));
    }

    private void btnChangePassword() {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        final EditText etNewPassword = new EditText(HomeActivity.this);
        etNewPassword.setHint("Type New Password");
        etNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        LinearLayout.LayoutParams etNewPasswordParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etNewPasswordParams.setMargins(48, 16, 48, 0);
        etNewPassword.setLayoutParams(etNewPasswordParams);

        linearLayout.addView(etNewPassword);

        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Change Password")
                .setMessage("Type new password. ")
                .setView(linearLayout)
                .setPositiveButton("CHANGE", (dialog1, which) -> {
                    if (hasInternet(HomeActivity.this)) {
                        if (!TextUtils.isEmpty(valueOf(etNewPassword.getText()))) {
                            AsyncTask.execute(() -> changePassword(dialog1, valueOf(etNewPassword.getText())));
                        } else {
                            Toast.makeText(this, "Password is Required!", Toast.LENGTH_SHORT).show();
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

    private void changePassword(DialogInterface dialog, String password) {
        runOnUiThread(() -> progressDialog.show());
        if (null != FirebaseAuth.getInstance().getCurrentUser()) {
            FirebaseAuth
                    .getInstance()
                    .getCurrentUser()
                    .updatePassword(password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                signOut();
                            });
                        } else {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Failed to update Password!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "changePassword: trace: " + e.getMessage()));
        }
    }

    private void removeUser() {
        AsyncTask.execute(() -> {
            runOnUiThread(() -> progressDialog.show());
            if (null != FirebaseAuth.getInstance().getCurrentUser()) {
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                runOnUiThread(() -> {
                                    goToMainActivity();
                                    progressDialog.dismiss();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    Toast.makeText(HomeActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                });
                            }
                        })
                        .addOnFailureListener(e -> Log.d(TAG, "removeUser: trace: " + e.getMessage()));
            }
        });
    }

    private void signOut() {
        AsyncTask.execute(() -> {
            FirebaseAuth.getInstance().signOut();
            authListener = firebaseAuth -> {
                if (null == firebaseAuth.getCurrentUser()) goToMainActivity();
            };
        });
    }

    private void goToMainActivity() {
        HomeActivity.this.startActivity(new Intent(HomeActivity.this, MainActivity.class));
        HomeActivity.this.finish();
    }

    private boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) FirebaseAuth.getInstance().removeAuthStateListener(authListener);
    }
}