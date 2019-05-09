package com.ideal.vox.retrofitManager

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.TextView
import com.ideal.vox.R


/**
 * Created by neeraj on 27/7/17.
 */

class ProgressDialog(val activity: Activity) {
    private var progressDialog: Dialog? = null
    private var txtMsgTV: TextView? = null

    init {
        initiateProgressDialog()
    }

    fun initiateProgressDialog() {
        progressDialog = Dialog(activity)
        val view = View.inflate(activity, R.layout.progress_dialog, null)
        progressDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog!!.setContentView(view)
        progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        txtMsgTV = view.findViewById(R.id.txtMsgTV)
        progressDialog!!.setCancelable(false)
    }

    fun startProgressDialog() {
        if (progressDialog != null && !progressDialog!!.isShowing) {
            try {
                progressDialog!!.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            try {
                progressDialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateMessage(message: String) {
        txtMsgTV!!.text = message
    }
}
