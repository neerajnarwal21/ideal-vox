package com.ideal.vox.fragment.loginSignup

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.customViews.MyEditText
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_ls_confirm_otp.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class OTPFragment : BaseFragment() {

    private var confirmCall: Call<JsonObject>? = null
    private var resendCall: Call<JsonObject>? = null
    private var data: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_ls_confirm_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoginTimeToolbar("Confirm OTP")
        initUI()
//        resendOTP(data?.mobileNumber!!)
    }

    private fun initUI() {
        data = store.getUserData(Const.USER_DATA, UserData::class.java)
        otpTV.text = "An OTP has been sent to\n\n${data?.mobileNumber}\n${data?.email}"
        submitBT.setOnClickListener { if (validate()) confirmOTP() }
        resendTV.setOnClickListener { showResendDialog() }
    }

    private fun showResendDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        val dialog: AlertDialog
        bldr.setTitle("Resend OTP")
        val view = View.inflate(baseActivity, R.layout.dialog_resend_otp, null)
        bldr.setView(view)
        val emailET = view.findViewById<MyEditText>(R.id.emailET)
        emailET.setText(data?.email)
        bldr.setPositiveButton("Submit") { _, _ -> }
        bldr.setNegativeButton("Cancel", null)
        dialog = bldr.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (getText(emailET).isNotEmpty()) {
                dialog.dismiss()
                resendOTP(getText(emailET))
                baseActivity.hideSoftKeyboard()
            }
        }
    }

    private fun resendOTP(email: String) {
        val id = RequestBody.create(MediaType.parse("text/plain"), data?.id.toString())
        val to = RequestBody.create(MediaType.parse("text/plain"), email)
        resendCall = apiInterface.resendOTP(id, to)
        apiManager.makeApiCall(resendCall!!, this)
    }

    private fun confirmOTP() {
        val id = RequestBody.create(MediaType.parse("text/plain"), data?.id.toString())
        val otp = RequestBody.create(MediaType.parse("text/plain"), getText(otpET))
        confirmCall = apiInterface.confirmOTP(id, otp)
        apiManager.makeApiCall(confirmCall!!, this)
    }

    private fun validate(): Boolean {
        when {
            getText(otpET).isEmpty() -> showToast("Please enter OTP completely", true)
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
            val token = jsonObj.get("token").asString
            store.saveString(Const.SESSION_KEY, token)
            store.saveUserData(Const.USER_DATA, userData)

//            if ((baseActivity as LoginSignupActivity).forLogin) {
//                baseActivity.finish()
//            } else {
            val intent = Intent(baseActivity, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
            startActivity(intent)
//            }
        } else if (resendCall != null && call === resendCall) {
            val jsonObj = payload as JsonObject
            val userObj = jsonObj.getAsJsonObject("user")
            val userData = Gson().fromJson(userObj, UserData::class.java)
            store.saveUserData(Const.USER_DATA, userData)
            initUI()
        }
    }
}
