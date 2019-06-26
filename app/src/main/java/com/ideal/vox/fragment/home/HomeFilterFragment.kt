package com.ideal.vox.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.data.ExpertiseData
import com.ideal.vox.data.schedule.FilterData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Filters
import kotlinx.android.synthetic.main.fg_h_filter.*
import retrofit2.Call
import java.util.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class HomeFilterFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null
    private var list = ArrayList<ExpertiseData>()
    private var filterData: FilterData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey("data"))
            filterData = arguments!!.getParcelable("data")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_h_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("Filters")
        if (filterData == null) filterData = FilterData()
        initUI()
    }

    private fun initUI() {
        listCall = apiInterface.getExpertise()
        apiManager.makeApiCall(listCall!!, this)
        priceCB.setOnCheckedChangeListener { _, b ->
            rangeSB.isEnabled = b
            transFL.visibility = if (b) View.GONE else View.VISIBLE
        }
        priceCB.isChecked = filterData!!.priceDisable
        submitBT.setOnClickListener {
            filterData?.category = list[expSP.selectedItemPosition].name
            filterData?.min = getText(minTV).toInt()
            filterData?.max = getText(maxTV).toInt()
            filterData?.priceDisable = priceCB.isChecked
            Filters.updateFilters(filterData)
            baseActivity.onBackPressed()
        }
        resetBT.setOnClickListener {
            Filters.updateFilters(null)
            baseActivity.onBackPressed()
        }
        rangeSB.setMinStartValue(filterData!!.min.toFloat())
                .setMaxStartValue(filterData!!.max.toFloat()).apply()
        rangeSB.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            minTV.setText(minValue.toString())
            maxTV.setText(maxValue.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setToolbar("Home", true, showMap = true)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (listCall != null && call === listCall) {
            list.clear()
            val jsonArr = payload as JsonArray
            val array = arrayOfNulls<String>(jsonArr.size() + 1)
            list.add(ExpertiseData(0, "All"))
            array[0] = "All"
            for ((i, datas) in jsonArr.withIndex()) {
                val obj = datas.asJsonObject
                val data = Gson().fromJson(obj, ExpertiseData::class.java)
                array[i + 1] = data.name
                list.add(data)
            }
            loadSpinner(array)
        }
    }

    private fun loadSpinner(array: Array<String?>) {
        val spinnerArrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item_dark, array)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.adapter_simple_item_list)
        expSP.adapter = spinnerArrayAdapter
        for ((i, data) in list.withIndex()) {
            if (data.name == filterData?.category) {
                expSP.setSelection(i)
                break
            }
        }
    }
}
