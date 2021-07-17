package com.singularitycoder.recurringtasks1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MyViewModel(application: Application) : AndroidViewModel(application) {
    val timerFlag: MutableLiveData<Boolean> = MutableLiveData()

    fun setTimerFlag(flag: Boolean) {
        timerFlag.postValue(flag)
    }

}