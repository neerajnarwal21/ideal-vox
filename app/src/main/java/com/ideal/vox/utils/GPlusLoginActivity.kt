package com.ideal.vox.utils

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


/**
 * Created by neeraj.narwal on 9/8/16.
 */
class GPlusLoginActivity : AppCompatActivity() {
    private val G_PLUS_SIGNIN_REQ_CODE = 1212

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createGoogleClient()
    }

    private fun createGoogleClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.signOut()

//        val account = GoogleSignIn.getLastSignedInAccount(this)
//        if (account != null) {
//            SocialLogin.onGDone(account)
//            finish()
//        } else {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, G_PLUS_SIGNIN_REQ_CODE)
//        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == G_PLUS_SIGNIN_REQ_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Signed in successfully, show authenticated UI.
                SocialLogin.onGDone(account!!)
            } catch (e: ApiException) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.e("GPlus", "signInResult:failed code=" + e.statusCode)
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }

            finish()
        } /*else if (requestCode == RC_SIGN_IN) {
//            resetAndLogin();
        }*/
        else
            finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
