package com.ideal.vox.fragment.loginSignup.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ideal.vox.BuildConfig
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.data.UserData
import com.ideal.vox.databinding.FgLsLoginBinding
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.fragment.loginSignup.ForgotPasswordFragment
import com.ideal.vox.fragment.loginSignup.OTPFragment
import com.ideal.vox.fragment.loginSignup.signup.SignupFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.FacebookLogin
import com.ideal.vox.utils.GPlusLoginActivity
import com.ideal.vox.utils.SocialLogin
import kotlinx.android.synthetic.main.fg_ls_login.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class LoginFragment : BaseFragment() {

    lateinit var model: LoginFragViewModel
    private var loginCall: Call<JsonObject>? = null
    private var socailLoginCall: Call<JsonObject>? = null
    private var reactivateCall: Call<JsonObject>? = null
    private var socialEmail: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FgLsLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fg_ls_login, container, false)

        model = ViewModelProviders.of(this).get(LoginFragViewModel::class.java)
        binding.model = model
        model.getStatus().observe(this, Observer {
            when (it) {
                LoginStatus.LOGIN -> if (validate()) doLogin()
                LoginStatus.SIGNUP -> jumpToSignUpFragment()
                LoginStatus.FORGOT -> jumpToForgotFragment()
                LoginStatus.FB -> {
                    baseActivity.startActivity(Intent(baseActivity, FacebookLogin::class.java))
                    SocialLogin.doFbSignin {
                        val jsonObject = it.jsonObject
                        val name = jsonObject["first_name"].toString() + " " + jsonObject["last_name"].toString()
                        val email = jsonObject["email"].toString()
                        doSocialLogin(name, email)
                    }
                }
                LoginStatus.GPLUS -> {
                    baseActivity.startActivity(Intent(baseActivity, GPlusLoginActivity::class.java))
                    SocialLogin.doGSignin {
                        val name = it.displayName.toString()
                        val email = it.email.toString()
                        doSocialLogin(name, email)
                    }
                }
            }
        })
        binding.setLifecycleOwner(this)
        val view = binding.getRoot()
        return view
    }

    private fun doSocialLogin(name: String, email: String) {
        socialEmail = email
        val namee = RequestBody.create(MediaType.parse("text/plain"), name)
        val emaill = RequestBody.create(MediaType.parse("text/plain"), email)
        socailLoginCall = apiInterface.socialLogin(namee, emaill)
        apiManager.makeApiCall(socailLoginCall!!, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoginTimeToolbar("Login")
        if (BuildConfig.DEBUG) {
            emailET.setText("7015004571")
            passET.setText("admin")
        }
    }

    private fun jumpToForgotFragment() {
        baseActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fc_ls, ForgotPasswordFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun jumpToSignUpFragment() {
        baseActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fc_ls, SignupFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun validate(): Boolean {
        when {
            getText(emailET).isEmpty() -> showToast("Please enter email", true)
            getText(passET).isEmpty() -> showToast("Please enter password", true)
            else -> return true
        }
        return false
    }

    private fun doLogin() {
        val email = RequestBody.create(MediaType.parse("text/plain"), getText(emailET))
        val pass = RequestBody.create(MediaType.parse("text/plain"), getText(passET))
        loginCall = apiInterface.login(email, pass)
        apiManager.makeApiCall(loginCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (reactivateCall != null && reactivateCall === call) {
            showToast("Account successfully recovered, Login now")
        } else {
            val jsonObj = payload as JsonObject

            val userObj = jsonObj.getAsJsonObject("user")
            val userData = Gson().fromJson(userObj, UserData::class.java)
            store.saveUserData(Const.USER_DATA, userData)
            if (userData.is_active == 1) {
                val token = jsonObj.get("token").asString
                store.saveString(Const.SESSION_KEY, token)

//            if ((baseActivity as LoginSignupActivity).forLogin) {
//                baseActivity.finish()
//            } else {
                val intent = Intent(baseActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
//            }
            } else {
                val bndl = Bundle()
                bndl.putBoolean("isFromLogin",true)
                val frag = OTPFragment()
                frag.arguments = bndl
                baseActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fc_ls, frag)
                        .addToBackStack(null)
                        .commit()
            }
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        if (errorMessage == "inactive_user") {
            showReactivateDialog()
        } else
            super.onError(call, statusCode, errorMessage, responseListener)
    }

    private fun showReactivateDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Account recover !")
        bldr.setMessage("Do you want us to restore your user account ?")
        bldr.setPositiveButton("Yes") { _, _ ->
            val email = RequestBody.create(MediaType.parse("text/plain"), socialEmail
                    ?: getText(emailET))
            reactivateCall = apiInterface.reactivateAccount(email)
            apiManager.makeApiCall(reactivateCall!!, this)
        }
        bldr.setNegativeButton("No", null)
        bldr.create().show()
    }
}
