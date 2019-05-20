package com.ideal.vox.fragment.home.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.adapter.AccessoryAdapter
import com.ideal.vox.data.AccessoryData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.getAge
import kotlinx.android.synthetic.main.fg_p_about.*
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class HomeAboutFragment : BaseFragment() {

    private var adapter: AccessoryAdapter? = null
    private var listCall: Call<JsonObject>? = null
    private var userData: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fg_p_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        emptyTV.text = "No Accessories"
    }

    private fun initUI() {
        userData = (baseActivity as MainActivity).userData
        if (userData != null) {
            expTV.text = userData!!.photoProfile.expertise
            experTV.text = "${userData!!.photoProfile.experienceInYear} years, ${userData!!.photoProfile.experienceInMonths}"
            ageTV.text = "${getAge(userData!!.photoProfile.dob)}, ${userData!!.photoProfile.gender}"
            mobileTV.text = userData!!.mobileNumber
            emailTV.text = userData!!.email
            addressTV.text = "${userData!!.photoProfile.address}\n${userData!!.photoProfile.pinCode}"
            callIV.setOnClickListener {
                val call = Uri.parse("tel:${userData!!.mobileNumber}")
                val callIntent = Intent(Intent.ACTION_DIAL, call)
                baseActivity.startActivity(Intent.createChooser(callIntent, "Call with"))
            }
            mapIV.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:${userData!!.photoProfile.lat},${userData!!.photoProfile.lng}" +
                        "?q=${userData!!.photoProfile.lat},${userData!!.photoProfile.lng}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(baseActivity.packageManager) != null) {
                    startActivity(Intent.createChooser(mapIntent, "Open with"))
                }
            }
            getList()
        }
        editAccessoryIV.visibility = View.INVISIBLE
    }

    private fun getList() {
        loadingPB.visibility = View.VISIBLE
        listCall = apiInterface.allAccessories(userData!!.id)
        apiManager.makeApiCall(listCall!!, this, false)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        if (listCall != null && listCall == call) {
            val jsonObj = payload as JsonObject
            val listArr = jsonObj.get("accessories").asJsonArray
            val objectType = object : TypeToken<ArrayList<AccessoryData>>() {}.type
            val datas = Gson().fromJson<ArrayList<AccessoryData>>(listArr, objectType)
            adapter = AccessoryAdapter(baseActivity, datas, userData!!.id, false)
            if (datas.size == 0) emptyTV.visibility = View.VISIBLE
            accListRV.adapter = adapter
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}
