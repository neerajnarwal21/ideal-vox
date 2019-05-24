package com.ideal.vox.fragment.home

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.ScheduleSetAdapter
import com.ideal.vox.data.ScheduleData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_h_schedule.*
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ScheduleFragment : BaseFragment() {

    private var setAdapter: ScheduleSetAdapter? = null
    private var listCall: Call<JsonObject>? = null
    private var userData: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_h_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("My Schedule", showDrawer = false)
        userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        getSchedule()
    }

    private fun getSchedule() {
        loadingPB.visibility = View.VISIBLE
        listCall = apiInterface.getSchedule(userData!!.id)
        apiManager.makeApiCall(listCall!!, this, false)
        submitBT.setOnClickListener { showToast("Under progress") }
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        if (listCall != null && listCall == call) {
            val jsonObj = payload as JsonObject
            val listArr = jsonObj.get("schedules").asJsonArray
            val objectType = object : TypeToken<ArrayList<ScheduleData>>() {}.type
            val datas = Gson().fromJson<ArrayList<ScheduleData>>(listArr, objectType)
            listRV.layoutManager = GridLayoutManager(baseActivity, 7)
            setAdapter = ScheduleSetAdapter(baseActivity, datas)
            listRV.adapter = setAdapter
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}
