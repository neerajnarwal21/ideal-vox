package com.ideal.vox.utils

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ideal.vox.R

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton

import java.util.Arrays


/**
 * Created by neeraj.narwal on 9/8/16.
 */
class FacebookLogin : AppCompatActivity() {
    private var callbackmanager: CallbackManager? = null
    private var request: GraphRequest? = null
    private var fbLoginButton: LoginButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.social_facebook_login)
        callbackmanager = CallbackManager.Factory.create()
        fbLoginButton = findViewById(R.id.fbLoginButton)
        fbLoginButton!!.setOnClickListener { initFacebook() }
        fbLoginButton!!.callOnClick()
    }

    fun initFacebook() {
        fbLoginButton!!.setReadPermissions(Arrays.asList("email", "public_profile"))
        LoginManager.getInstance().logOut()
        fbLoginButton!!.registerCallback(callbackmanager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                request = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                    SocialLogin.onFbDone(response)
                    finish()
                }
                val parameters = Bundle()
                parameters.putString("fields", "id, first_name, last_name, email")
                request!!.parameters = parameters
                request!!.executeAsync()
            }

            override fun onCancel() {
                log("Facebook login cancel")
                finish()
            }

            override fun onError(error: FacebookException) {
                error.printStackTrace()
                log("Facebook error :" + error.toString())
                finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackmanager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun log(s: String) {
        debugLog("Facebook Login : >> ", s)
    }
}
