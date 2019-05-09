package com.ideal.vox.fragment.loginSignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_ls_reset_pass.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ResetPasswordFragment : BaseFragment() {

    private var resetCall: Call<JsonObject>? = null
    private var data: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_ls_reset_pass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoginTimeToolbar("Reset Password")
        initUI()
    }

    private fun initUI() {
        data = store.getUserData(Const.USER_DATA, UserData::class.java)
        resetTV.text = "Enter the OTP sent to\n${data?.mobileNumber}\nand set your new password"
        submitBT.setOnClickListener { if (validate()) resetPassword() }
    }

    private fun resetPassword() {
        val userId = RequestBody.create(MediaType.parse("text/plain"), data?.id.toString())
        val otp = RequestBody.create(MediaType.parse("text/plain"), getText(otpET))
        val password = RequestBody.create(MediaType.parse("text/plain"), getText(passET))
        resetCall = apiInterface.resetPassword(userId, otp, password)
        apiManager.makeApiCall(resetCall!!, this)
    }

    private fun validate(): Boolean {
        when {
            getText(otpET).isEmpty() -> showToast("Please enter OTP completely", true)
            getText(passET).isEmpty() -> showToast("Please enter password", true)
            getText(confPassET).isEmpty() -> showToast("Please enter confirm password", true)
            getText(passET) != getText(confPassET) -> showToast("Password and Confirm Password doesn't match", true)
            else -> return true
        }
        return false
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (resetCall != null && call === resetCall) {
            showToast("Password Changed successfully")
            baseActivity.onBackPressed()
        }
    }
}
