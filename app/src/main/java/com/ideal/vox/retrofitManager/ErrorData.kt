package com.ideal.vox.retrofitManager

import com.google.gson.annotations.SerializedName

/**
 * Created by neeraj on 15/6/17.
 */
data class ErrorData(@SerializedName("error") var message: String? = null)
