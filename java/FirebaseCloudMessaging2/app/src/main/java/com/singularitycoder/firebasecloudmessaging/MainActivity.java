package com.singularitycoder.firebasecloudmessaging;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDeviceToken();
    }

    private void getDeviceToken() {
        FirebaseInstanceId
                .getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        if (!("").equals(token)) {
                            ((TextView) findViewById(R.id.tv_registered_device_token)).setText("Registered Token ID: " + token);
                        } else {
                            ((TextView) findViewById(R.id.tv_registered_device_token)).setText("Didn't receive Firebase token!");
                        }
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void subscribeToTopic() {
        FirebaseMessaging
                .getInstance()
                .subscribeToTopic("weather")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Successfully subscribed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
