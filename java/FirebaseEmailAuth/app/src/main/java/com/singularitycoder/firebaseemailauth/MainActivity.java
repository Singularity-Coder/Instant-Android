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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText etEmail, etPassword;
    private Button btnAuthenticate, btnAlreadyRegistered, btnResetPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_main);
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
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnAuthenticate = findViewById(R.id.btn_authenticate);
        btnAlreadyRegistered = findViewById(R.id.btn_already_registered);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        btnAuthenticate.setText("SignUp");
        btnAlreadyRegistered.setText("Already Registered? LogIn");
        btnResetPassword.setVisibility(View.GONE);
    }

    private void checkIfUserExists() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) goToHomeActivity();
    }

    private void setClickListeners() {
        btnAuthenticate.setOnClickListener(view -> btnAuthenticate());
        btnAlreadyRegistered.setOnClickListener(view -> btnAlreadyRegistered());
        btnResetPassword.setOnClickListener(view -> btnResetPassword());
    }

    private void btnAuthenticate() {
        if (("signup").equals(valueOf(btnAuthenticate.getText()).toLowerCase().trim())) {
            if (hasInternet(MainActivity.this)) {
                if (hasValidInput(etEmail, etPassword)) {
                    AsyncTask.execute(() -> signUp(valueOf(etEmail.getText()), valueOf(etPassword.getText())));
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (hasInternet(MainActivity.this)) {
                if (hasValidInput(etEmail, etPassword)) {
                    AsyncTask.execute(() -> signIn(valueOf(etEmail.getText()), valueOf(etPassword.getText())));
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void btnAlreadyRegistered() {
        if (("signup").equals(valueOf(btnAuthenticate.getText()).toLowerCase().trim())) {
            btnAuthenticate.setText("SignIn");
            btnResetPassword.setVisibility(View.VISIBLE);
            btnAlreadyRegistered.setText("Not a member? Create New Account");
        } else {
            btnAuthenticate.setText("SignUp");
            btnResetPassword.setVisibility(View.GONE);
            btnAlreadyRegistered.setText("Already Registered? LogIn");
        }
    }

    private void btnResetPassword() {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearParams);

        final EditText etSendToEmail = new EditText(MainActivity.this);
        etSendToEmail.setHint("Type Email");
        etSendToEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        LinearLayout.LayoutParams etSendToEmailParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etSendToEmailParams.setMargins(48, 16, 48, 0);
        etSendToEmail.setLayoutParams(etSendToEmailParams);

        linearLayout.addView(etSendToEmail);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Forgot Password")
                .setMessage("Enter registered Email ID to receive password reset instructions.")
                .setView(linearLayout)
                .setPositiveButton("RESET", (dialog1, which) -> {
                    if (hasInternet(MainActivity.this)) {
                        if (!TextUtils.isEmpty(valueOf(etSendToEmail.getText()))) {
                            AsyncTask.execute(() -> resetPassword(dialog1, valueOf(etSendToEmail.getText())));
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

    private boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean hasValidInput(EditText etEmail, EditText etPassword) {
        String email = valueOf(etEmail.getText()).trim();
        String password = valueOf(etPassword.getText()).trim();

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

    private void signUp(String email, String password) {
        runOnUiThread(() -> progressDialog.show());
        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            goToHomeActivity();
                        });
                    } else {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Authentication failed. Try again!" + task.getException(), Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "signUp: trace: " + e.getMessage()));
    }

    private void signIn(String email, String password) {
        runOnUiThread(() -> progressDialog.show());
        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, task -> {
                    if (task.isSuccessful()) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            goToHomeActivity();
                        });
                    } else {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Authentication failed. Try again!", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "signIn: trace: " + e.getMessage()));
    }

    private void resetPassword(DialogInterface dialog, String email) {
        runOnUiThread(() -> progressDialog.show());
        FirebaseAuth
                .getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "We have sent instructions to your email to reset your password. Please check!", Toast.LENGTH_LONG).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed to send 'reset password' email. Try again or restart Internet connection!", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "resetPassword: trace: " + e.getMessage()));
    }

    private void goToHomeActivity() {
        MainActivity.this.startActivity(new Intent(MainActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        MainActivity.this.finish();
    }
}
