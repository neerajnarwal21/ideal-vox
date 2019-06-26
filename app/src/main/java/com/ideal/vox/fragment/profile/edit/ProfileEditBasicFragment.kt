package com.ideal.vox.fragment.profile.edit

import android.content.DialogInterface
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
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.logout
import com.mukesh.OtpView
import kotlinx.android.synthetic.main.fg_p_edit_advance_about.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileEditBasicFragment : BaseFragment() {

    private var updateCall: Call<JsonObject>? = null
    private var phoneCall: Call<JsonObject>? = null
    private var deleteCall: Call<JsonObject>? = null
    private var otpCall: Call<JsonObject>? = null
    private var data: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_edit_advance_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = store.getUserData(Const.USER_DATA, UserData::class.java)
        initUI()
    }

    private fun initUI() {
        nameET.setText(data?.name)
        emailET.setText(data?.email)
        mobileET.setText(data?.mobileNumber)
        mobileIV.setOnClickListener { showChangePhoneNumberDialog() }
        submitBT.setOnClickListener { if (validate()) submit() }
        deleteBT.setOnClickListener { showDeleteConfirmDialog() }
    }

    private fun showDeleteConfirmDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Confirm !")
        bldr.setMessage("Are you sure you want to delete your user account?")
        bldr.setPositiveButton("Yes") { dialogInterface, i ->
            deleteCall = apiInterface.deactivateAccount()
            apiManager.makeApiCall(deleteCall!!, this)
            dialogInterface.dismiss()
        }
        bldr.setNegativeButton("No", null)
        bldr.create().show()
    }

    private fun showChangePhoneNumberDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        val dialog: AlertDialog
        bldr.setTitle("Change Phone number")
        val view = View.inflate(baseActivity, R.layout.dialog_change_phone, null)
        bldr.setView(view)
        val phoneET = view.findViewById<MyEditText>(R.id.phoneET)
        val resendTV = view.findViewById<MyTextView>(R.id.resendTV)
        val otpET = view.findViewById<OtpView>(R.id.otpET)
        resendTV.setOnClickListener { resendOTP(getText(phoneET)) }
        bldr.setPositiveButton("Submit") { _, _ -> }
        bldr.setNegativeButton("Cancel", null)
        dialog = bldr.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (validatePhone(getText(phoneET), getText(otpET))) {
                dialog.dismiss()
                changePhone(getText(phoneET), getText(otpET))
                baseActivity.hideSoftKeyboard()
            }
        }
    }

    private fun validatePhone(text: String, text1: String): Boolean {
        when {
            text.isEmpty() -> showToast("Please enter Phone number")
            text1.isEmpty() -> showToast("Please enter OTP")
            else -> return true
        }
        return false
    }

    private fun resendOTP(text: String) {
        val phone = RequestBody.create(MediaType.parse("text/plain"), text)
        otpCall = apiInterface.resendOTPToPhone(phone)
        apiManager.makeApiCall(otpCall!!, this)
    }

    private fun changePhone(phone: String, otp: String) {
        val phonee = RequestBody.create(MediaType.parse("text/plain"), phone)
        val otpp = RequestBody.create(MediaType.parse("text/plain"), otp)
        phoneCall = apiInterface.updatePhone(otpp, phonee)
        apiManager.makeApiCall(phoneCall!!, this)
    }

    private fun submit() {
        val name = RequestBody.create(MediaType.parse("text/plain"), getText(nameET))
        updateCall = apiInterface.updateProfile(name)
        apiManager.makeApiCall(updateCall!!, this)
    }

    private fun validate(): Boolean {
        when {
            getText(nameET).isEmpty() -> showToast("Please enter name", true)
            else -> return true
        }
        return false
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (updateCall != null && updateCall === call) {
            val jsonObj = payload as JsonObject
            val userData = Gson().fromJson(jsonObj, UserData::class.java)
            store.saveUserData(Const.USER_DATA, userData)
            (baseActivity as MainActivity).setupHeaderView()
            showToast("Details updated successfully")
        } else if (phoneCall != null && phoneCall === call) {
            val jsonObj = payload as JsonObject
            val userData = Gson().fromJson(jsonObj, UserData::class.java)
            mobileET.setText(userData.mobileNumber)
            store.saveUserData(Const.USER_DATA, userData)
            showToast("Details updated successfully")
        } else if (otpCall != null && otpCall === call) {
            val jsonObj = payload as JsonObject

            val userObj = jsonObj.getAsJsonObject("user")
            val userData = Gson().fromJson(userObj, UserData::class.java)
            store.saveUserData(Const.USER_DATA, userData)
            showToast("OTP has been sent")
        } else if (deleteCall != null && deleteCall === call) {
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Account Deleted !")
        bldr.setMessage("Your user account has been deleted successfully")
        bldr.setPositiveButton("Ok", null)
        bldr.setOnDismissListener { logout(baseActivity, store) }
        bldr.create().show()
    }
}
