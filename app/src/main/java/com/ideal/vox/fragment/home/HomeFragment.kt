package com.ideal.vox.fragment.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.HomeAdapter
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import kotlinx.android.synthetic.main.fg_home.*
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */

class HomeFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fg_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("Home", true)
        listRV.layoutManager = LinearLayoutManager(baseActivity)
        getList()
    }

    private fun getList() {
        loadingPB.visibility = View.VISIBLE
        listCall = apiInterface.allPhotographers()
        apiManager.makeApiCall(listCall!!, this, false)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        val jsonObj = payload as JsonObject
        val listArr = jsonObj.get("photographers").asJsonArray
        val objectType = object : TypeToken<ArrayList<UserData>>() {}.type
        val datas = Gson().fromJson<ArrayList<UserData>>(listArr, objectType)
        listRV.adapter = HomeAdapter(baseActivity, datas)
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}
