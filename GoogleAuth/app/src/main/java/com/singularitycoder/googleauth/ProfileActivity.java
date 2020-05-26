package com.singularitycoder.googleauth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setIntentDataToViews();
        signOut();
    }

    private void signOut() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        findViewById(R.id.btn_sign_out).setOnClickListener(v -> googleSignInClient.signOut().addOnCompleteListener(task -> startActivity(new Intent(ProfileActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))));
    }

    private void setIntentDataToViews() {
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra("GOOGLE_ACCOUNT_DETAILS");
        if (null != googleSignInAccount) {
            if (null != googleSignInAccount.getPhotoUrl()) {
                AsyncTask.execute(() -> {
                    URL url = null;
                    try {
                        url = new URL(String.valueOf(googleSignInAccount.getPhotoUrl()));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (null != url) {
                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            runOnUiThread(() -> ((ImageView) findViewById(R.id.iv_image)).setImageBitmap(bmp));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            ((TextView) findViewById(R.id.tv_name)).setText("Display Name: " + googleSignInAccount.getDisplayName() + " \nFamily Name: " + googleSignInAccount.getFamilyName() + " \nGiven Name: " + googleSignInAccount.getGivenName());
            ((TextView) findViewById(R.id.tv_email)).setText(googleSignInAccount.getEmail());
        }
    }
}
