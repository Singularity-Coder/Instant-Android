package com.example.androidstoragemadness

import android.app.Application
import com.downloader.PRDownloader

class ThisApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PRDownloader.initialize(applicationContext)
    }
}