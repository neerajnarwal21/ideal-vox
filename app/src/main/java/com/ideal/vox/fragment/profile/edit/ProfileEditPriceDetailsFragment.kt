package com.ideal.vox.fragment.profile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_p_edit_advance_price.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileEditPriceDetailsFragment : BaseFragment() {

    private var updateCall: Call<JsonObject>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_edit_advance_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = store.getUserData(Const.USER_DATA, UserData::class.java)?.photoProfile
        dayET.setText(data!!.dayPrice)
        nightET.setText(data!!.nightPrice)
        fullET.setText(data!!.fullDayPrice)
        saveBT.setOnClickListener { if (validate()) submit() }
    }

    private fun validate(): Boolean {
        when {
            getText(dayET).isEmpty() -> showToast("Please enter day booking price", true)
            getText(nightET).isEmpty() -> showToast("Please enter night booking price", true)
            getText(fullET).isEmpty() -> showToast("Please enter full day booking price", true)
            else -> return true
        }
        return false
    }

    private fun submit() {
        val day = RequestBody.create(MediaType.parse("text/plain"), getText(dayET))
        val night = RequestBody.create(MediaType.parse("text/plain"), getText(nightET))
        val fullDay = RequestBody.create(MediaType.parse("text/plain"), getText(fullET))

        updateCall = apiInterface.updatePrice(day, night, fullDay)
        apiManager.makeApiCall(updateCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        val jsonObj = payload as JsonObject
        val userObj = jsonObj.getAsJsonObject("user")
        val userData = Gson().fromJson(userObj, UserData::class.java)
        store.saveUserData(Const.USER_DATA, userData)
        showToast("Details updated successfully")
    }
}