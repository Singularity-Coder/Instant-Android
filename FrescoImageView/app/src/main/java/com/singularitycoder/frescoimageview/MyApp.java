package com.singularitycoder.frescoimageview;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing
        Fresco.initialize(this);
    }
}
