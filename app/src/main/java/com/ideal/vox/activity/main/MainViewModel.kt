package com.ideal.vox.activity.main

import android.arch.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var mainCallbacks: MainCallbacks? = null

    fun registerSplash(mainCallbacks: MainCallbacks) {
        this.mainCallbacks = mainCallbacks
    }

    fun onLoginClick() {
        mainCallbacks?.onLoginClick()
    }
}