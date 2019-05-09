package com.ideal.vox.fragment.loginSignup.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.ideal.vox.utils.SingleLiveEvent

class LoginFragViewModel : ViewModel() {

    private val status = SingleLiveEvent<LoginStatus>()

    fun getStatus(): LiveData<LoginStatus> {
        return status
    }

    fun onLoginClick() {
        status.value = LoginStatus.LOGIN
    }

    fun onSignUpClick() {
        status.value = LoginStatus.SIGNUP
    }

    fun onFbClick() {
        status.value = LoginStatus.FB
    }

    fun onGPlusClick() {
        status.value = LoginStatus.GPLUS
    }

    fun onForgotClick() {
        status.value = LoginStatus.FORGOT
    }
}