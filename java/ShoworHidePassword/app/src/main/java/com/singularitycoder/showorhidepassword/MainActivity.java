package com.singularitycoder.showorhidepassword;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etPassword;
    Button btnShowHidePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showHidePassword();
    }

    private void showHidePassword() {
        etPassword = findViewById(R.id.et_password);
        btnShowHidePassword = findViewById(R.id.btn_show_hide_password);

        btnShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!("").equals(etPassword.getText().toString().trim())) {
                    if (btnShowHidePassword.getText().toString().trim().equals("SHOW PASSWORD")) {
                        etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        btnShowHidePassword.setText("HIDE PASSWORD");
                    } else {
                        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        btnShowHidePassword.setText("SHOW PASSWORD");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Enter a password to see it!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
