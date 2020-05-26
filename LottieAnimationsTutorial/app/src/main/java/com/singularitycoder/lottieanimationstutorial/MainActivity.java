package com.singularitycoder.lottieanimationstutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationView1;
    LottieAnimationView lottieAnimationView2;
    LottieAnimationView lottieAnimationView3;
    LottieAnimationView lottieAnimationView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lottieAnimationView1 = findViewById(R.id.lottie_animation_1);
        lottieAnimationView2 = findViewById(R.id.lottie_animation_2);
        lottieAnimationView3 = findViewById(R.id.lottie_animation_3);
        lottieAnimationView4 = findViewById(R.id.lottie_animation_4);

        lottieAnimationView1.setAnimation(R.raw.anim1);
        lottieAnimationView2.setAnimation(R.raw.anim2);
        lottieAnimationView3.setAnimation(R.raw.anim3);
        lottieAnimationView4.setAnimation(R.raw.anim4);


        lottieAnimationView1.playAnimation();
        lottieAnimationView2.playAnimation();
        lottieAnimationView3.playAnimation();
        lottieAnimationView4.playAnimation();
    }
}
