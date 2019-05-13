package com.ideal.vox.fragment.profile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_p_edit_advance_about.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileEditBasicFragment : BaseFragment() {

    private var updateCall: Call<JsonObject>? = null
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
        submitBT.setOnClickListener { if (validate()) submit() }
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
        }
    }
}
