package com.ideal.vox.fragment.loginSignup.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.fragment.loginSignup.OTPFragment
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.isValidMail
import kotlinx.android.synthetic.main.fg_ls_signup.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class SignupFragment : BaseFragment() {

    private var signUpCall: Call<JsonObject>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_ls_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoginTimeToolbar("SignUp")

        signUpBT.setOnClickListener { if (validate()) doSignUp() }
    }

    private fun validate(): Boolean {
        when {
            getText(nameET).isEmpty() -> showToast("Please enter name", true)
            getText(mobileET).isEmpty() -> showToast("Please enter mobile no.", true)
            getText(emailET).isEmpty() -> showToast("Please enter email", true)
            !isValidMail(getText(emailET)) -> showToast("Please enter valid email", true)
            getText(passET).isEmpty() -> showToast("Please enter password", true)
            getText(confPassET).isEmpty() -> showToast("Please enter confirm password", true)
            getText(passET) != getText(confPassET) -> showToast("Password and Confirm Password doesn't match", true)
            else -> return true
        }
        return false
    }

    private fun doSignUp() {
        val name = RequestBody.create(MediaType.parse("text/plain"), getText(nameET))
        val mobile = RequestBody.create(MediaType.parse("text/plain"), getText(mobileET))
        val email = RequestBody.create(MediaType.parse("text/plain"), getText(emailET))
        val pass = RequestBody.create(MediaType.parse("text/plain"), getText(passET))
        val confPass = RequestBody.create(MediaType.parse("text/plain"), getText(confPassET))
        signUpCall = apiInterface.signupUser(name, mobile, email, pass, confPass)
        apiManager.makeApiCall(signUpCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        val jsonObj = payload as JsonObject
        val userObj = jsonObj.getAsJsonObject("user")
        val userData = Gson().fromJson(userObj, UserData::class.java)
        store.saveUserData(Const.USER_DATA, userData)

        baseActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fc_ls, OTPFragment())
                .commit()
    }
}
