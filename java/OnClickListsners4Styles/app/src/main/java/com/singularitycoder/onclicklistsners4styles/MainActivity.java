package com.singularitycoder.onclicklistsners4styles;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {  // Click Type 4


    // Click Type 1
    private Button click1Btn;
    private View.OnClickListener click1Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            click1BtnFunc();
        }
    };

    // Click Type 2
    private Button click2Btn;


    // Click Type 3
    private Button click3Btn;


    // Click Type 4
    private Button click4Btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Click Type 1
        click1Btn = findViewById(R.id.button1);
        click1Btn.setOnClickListener(click1Listener);


        // Click Type 2
        click2Btn = findViewById(R.id.button2);
        click2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click2BtnFunc();
            }
        });


        // Click Type 3
        click3Btn = findViewById(R.id.button3);
        click3Btn.setOnClickListener(new click3BtnClass());


        // Click Type 4
        click4Btn = findViewById(R.id.button4);
        click4Btn.setOnClickListener(this);
    }


    // Click Type 1
    private void click1BtnFunc() {
        click1Btn.setText("1 GOT CLICKED!");
    }


    // Click Type 2
    private void click2BtnFunc() {
        click2Btn.setText("2 GOT CLICKED!");
    }


    // Click Type 3
    class click3BtnClass implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            click3BtnFunc();
        }
    }

    private void click3BtnFunc() {
        click3Btn.setText("3 GOT CLICKED!");
    }


    // Click Type 4
    @Override
    public void onClick(View v) {
        clic4BtnFunc();
    }

    private void clic4BtnFunc() {
        click4Btn.setText("4 GOT CLICKED!");
    }
}