package com.singularitycoder.daggerhilt1

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Application level dependency Container

@HiltAndroidApp
class RepoApplication : Application()