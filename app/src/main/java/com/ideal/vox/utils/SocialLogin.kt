package com.ideal.vox.utils

import com.facebook.GraphResponse
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Created by neeraj.narwal on 9/8/16.
 */
object SocialLogin {

    private var fbListener: ((response: GraphResponse) -> Unit)? = null
    private var gListener: ((currentPerson: GoogleSignInAccount) -> Unit)? = null

    fun doFbSignin(listener: (( response: GraphResponse) -> Unit)) {
        this.fbListener = listener
    }

    fun onFbDone( response: GraphResponse) {
        fbListener?.invoke(response)
    }

    fun doGSignin(listener: ((currentPerson: GoogleSignInAccount) -> Unit)) {
        this.gListener = listener
    }

    fun onGDone(currentPerson: GoogleSignInAccount) {
        gListener?.invoke(currentPerson)
    }
}
