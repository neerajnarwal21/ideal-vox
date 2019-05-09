package com.ideal.vox.fragment.loginSignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_ls_forgot_pass.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ForgotPasswordFragment : BaseFragment() {

    private var confirmCall: Call<JsonObject>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_ls_forgot_pass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoginTimeToolbar("Forgot Password")
        initUI()
    }

    private fun initUI() {
        submitBT.setOnClickListener { if (validate()) resetPassword() }
    }

    private fun resetPassword() {
        val mobile = RequestBody.create(MediaType.parse("text/plain"), getText(mobileET))
        confirmCall = apiInterface.forgotPassword(mobile)
        apiManager.makeApiCall(confirmCall!!, this)
    }

    private fun validate(): Boolean {
        when {
            getText(mobileET).isEmpty() -> showToast("Please enter mobile number", true)
            else -> return true
        }
        return false
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (confirmCall != null && call === confirmCall) {
            val jsonObj = payload as JsonObject

            val userObj = jsonObj.getAsJsonObject("user")
            val userData = Gson().fromJson(userObj, UserData::class.java)
            store.saveUserData(Const.USER_DATA, userData)

            baseActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fc_ls, ResetPasswordFragment())
                    .commit()
        }
    }
}
