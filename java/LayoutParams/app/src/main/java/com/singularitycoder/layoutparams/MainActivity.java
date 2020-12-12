package com.singularitycoder.layoutparams;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    LinearLayout outerLinearLayout;
    RelativeLayout relativeLayout;
    EditText etEmail, etPassword;
    Button proButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = new EditText(this);
        etPassword = new EditText(this);

        // Step 7 - define the parent view group n add the child view group that contains all views
        // Parent ViewGroup that contains all child views and view groups
        outerLinearLayout = new LinearLayout(this);
        outerLinearLayout = findViewById(R.id.lin_layout);

        outerLinearLayout.addView(programmaticField("Email", "Enter Email", etEmail, "0", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT, R.mipmap.ic_launcher));
        outerLinearLayout.addView(programmaticField("Password", "Enter Password", etPassword, "0", InputType.TYPE_TEXT_VARIATION_PASSWORD, R.mipmap.ic_launcher));
        programmaticButton("LOGIN");
    }


    View programmaticField(String title, String hint, EditText fieldTv, String id, int inputType, int imgSrc) {

        // Step 1 - define the child view group if exists
        // Child View Group that contains all other views
        linearLayout = new LinearLayout(this);

        // Step 1.1 - decorate layout
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Step 1.2 - give view group some size properties
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearParams.setMargins(20, 10, 20, 10);

        // Step 1.3 - set the layout params to linear layout. meaning set the dimensions to the view group
        linearLayout.setLayoutParams(linearParams);

        // Step 1.3 - Alternative - if u want the root view to be set as Linear Layout then do this. Notice how we added the setContentView above. When u want to set ur own then u remove the above thing.
        // setContentView(linLayout, linLayoutParam);        // set LinearLayout as a root element of the screen


        // Step 2 - create a child view
        // Add textview as a child view to Linear layout
        TextView titleTextView = new TextView(this);

        // Step 2.1 - Add child view properties
        titleTextView.setText(title);

        // Step 2.1 - Add child view properties
        titleTextView.setTextSize(18);
        titleTextView.setPadding(5, 10, 0, 5);

        // Step 2.2 - create layout params for the child view
        ViewGroup.LayoutParams titleTvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Step 2.3 - add layout params of the child view
        titleTextView.setLayoutParams(titleTvParams);

        // Step 2.4 - Add the child to the parent view group
        linearLayout.addView(titleTextView);


        // Step 3 - then we add another view group as a child to the parent linear layout view group
        // Add a relative layout view group as a child view to Linear layout
        relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(relativeParams);
        linearLayout.addView(relativeLayout);


        // Step 4 - Repeat above procedure for other child elements
        // Add list text view to relative layout as a child view
        fieldTv.setHint(hint);
        fieldTv.setTextColor(ContextCompat.getColor(this, R.color.purple_500));
        fieldTv.setTextSize(16);
        fieldTv.setPadding(70, 0, 0, 0);
        fieldTv.setInputType(inputType);
        fieldTv.setGravity(Gravity.CENTER_VERTICAL);
        fieldTv.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_background));
        ViewGroup.LayoutParams listTvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 75);
        fieldTv.setLayoutParams(listTvParams);
        relativeLayout.addView(fieldTv);

        // Step 5 - Add a child to the relative layout child view group
        // Add image view to relative layout as a child view
        ImageView spinnerImage = new ImageView(this);
        spinnerImage.setImageDrawable(ContextCompat.getDrawable(this, imgSrc));
        spinnerImage.setPadding(4, 4, 4, 4);
        spinnerImage.setColorFilter(ContextCompat.getColor(this, R.color.purple_700));
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(40, 40);    // u must add relative params or the view group's params
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        imageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageParams.setMargins(20, 0, 0, 0);     // if u use the view group params then u will get this
        spinnerImage.setLayoutParams(imageParams);
        relativeLayout.addView(spinnerImage);

        return linearLayout;
    }


    // Step 6 - Add button to child linear layout
    void programmaticButton(String btnText) {
        proButton = new Button(this);
        proButton.setText(btnText);
        proButton.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_200));
        proButton.setTextColor(Color.parseColor("#ffffff"));
        proButton.setTextSize(16);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 75);
        btnParams.setMargins(0, 30, 0, 0);
        proButton.setLayoutParams(btnParams);
        linearLayout.addView(proButton);
        proButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
                hideKeyboard();
                Toast.makeText(getApplicationContext(), "Email with " + etEmail.getText().toString() + " and Password " + etPassword.getText().toString() + " Logged-In!", Toast.LENGTH_LONG).show();
                etEmail.setText("");
                etPassword.setText("");
            }
        });
    }

    void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}