package com.singularitycoder.googleauth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> startActivityForResult(googleSignInClient.getSignInIntent(), 1010));
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (null != googleSignInAccount) {
            startActivity(new Intent(this, ProfileActivity.class).putExtra("GOOGLE_ACCOUNT_DETAILS", googleSignInAccount));
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010 && resultCode == Activity.RESULT_OK) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                startActivity(new Intent(this, ProfileActivity.class).putExtra("GOOGLE_ACCOUNT_DETAILS", googleSignInAccount));
                finish();
            } catch (ApiException e) {
                Toast.makeText(this, e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
