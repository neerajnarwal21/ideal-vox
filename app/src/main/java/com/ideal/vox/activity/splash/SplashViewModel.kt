package com.ideal.vox.activity.splash

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.ideal.vox.utils.SingleLiveEvent

class SplashViewModel : ViewModel() {
    private val status = SingleLiveEvent<SplashStatus>()

    fun getStatus(): LiveData<SplashStatus> {
        return status
    }

    fun onLoginClick() {
        status.value = SplashStatus.LOGIN
    }

    fun onSkipClick() {
        status.value = SplashStatus.SKIP
    }
}