package com.ideal.vox.retrofitManager

import retrofit2.Call

/**
 * Created by Neeraj Narwal on 31/5/17.
 */
interface ResponseListener {
    fun onSuccess(call: Call<*>, payload: Any)

    fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener)

    /** Uncomment below method and its occurenece in [ApiManager]
     * if you want a method which is sure to fire even request completes or fails
     */

    //    fun onFinish()

}
