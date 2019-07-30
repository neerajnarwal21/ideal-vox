package com.ideal.vox.fragment.home.detail

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.data.UserData
import com.ideal.vox.data.schedule.DayType
import com.ideal.vox.data.schedule.NightType
import com.ideal.vox.data.schedule.ScheduleData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.changeDateFormat
import com.pugtools.fcalendar.data.CalendarAdapter
import com.pugtools.fcalendar.data.DayStatus
import com.pugtools.fcalendar.widget.DayTypeGet
import com.pugtools.fcalendar.widget.FlexibleCalendar
import kotlinx.android.synthetic.main.fg_h_schedule.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class UserScheduleFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null
    private var userData: UserData? = null
    private var currentCal = Calendar.getInstance()
    val dayTypeHashMap = SparseArray<DayStatus>()
    private var dayTypeGet: DayTypeGet? = null
    private var adapter: CalendarAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_h_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("Schedule", showDrawer = false, showToolbar = false)
//        submitBT.text = "Submit"
        userData = (baseActivity as MainActivity).userData
        getSchedule(currentCal, null)
        toggle.visibility = View.GONE
        submitBT.visibility = View.GONE
        backTBIV.setOnClickListener { baseActivity.onBackPressed() }
        flexCal.setCalendarListener(object : FlexibleCalendar.CalendarListener {
            override fun onDayClick(v: View?, day: Int) {

            }

            override fun onNightClick(v: View?, day: Int) {
            }

            override fun onMonthChange(currentCal: Calendar, dayTypeGet: DayTypeGet?) {
                getSchedule(currentCal, dayTypeGet)
            }
        })
    }

    private fun getSchedule(cal: Calendar, dayTypeGet: DayTypeGet?) {
        this.dayTypeGet = dayTypeGet
        currentCal.time = cal.time

        loadingPB.visibility = View.VISIBLE
        flexCal.visibility = View.INVISIBLE
        val firstDayCal = cal.clone() as Calendar
        val lastDayCal = cal.clone() as Calendar
        firstDayCal.set(Calendar.DAY_OF_MONTH, firstDayCal.getActualMinimum(Calendar.DAY_OF_MONTH))
        lastDayCal.set(Calendar.DAY_OF_MONTH, lastDayCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val startDate = RequestBody.create(MediaType.parse("text/plain"), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(firstDayCal.time))
        val endDate = RequestBody.create(MediaType.parse("text/plain"), SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(lastDayCal.time))

        listCall = apiInterface.getSchedule(userData!!.id, startDate, endDate)
        apiManager.makeApiCall(listCall!!, this, false)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        flexCal.visibility = View.VISIBLE
        dayTypeHashMap.clear()
        if (listCall != null && listCall == call) {
            val jsonObj = payload as JsonObject
            val listArr = jsonObj.get("schedules").asJsonArray
            val objectType = object : TypeToken<ArrayList<ScheduleData>>() {}.type
            val datas = Gson().fromJson<ArrayList<ScheduleData>>(listArr, objectType)

            val serverDataIntMap = SparseArray<ScheduleData>()
            if (datas.size > 0) {
                for (data in datas) {
                    val dateInt = changeDateFormat(data.scheduleDate, "yyyy-MM-dd", "dd").toInt()
                    serverDataIntMap.put(dateInt, data)
                }
            }

            val firstDayCal = currentCal.getActualMinimum(Calendar.DAY_OF_MONTH)
            val lastDayCal = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            for (i in firstDayCal..lastDayCal) {
                val dayStatus = DayStatus()
                val scheduleData = serverDataIntMap[i, ScheduleData()]

                dayStatus.day = when (scheduleData.day) {
                    DayType.BOOKED -> DayStatus.DayEnum.BOOKED
                    DayType.UNAVAILABLE -> DayStatus.DayEnum.UNAVAILABLE
                    DayType.PENDING -> DayStatus.DayEnum.PENDING
                    DayType.CONFIRMED -> DayStatus.DayEnum.CONFIRMED
                    DayType.REJECT -> DayStatus.DayEnum.REJECT
                    else -> DayStatus.DayEnum.AVAILABLE
                }
                dayStatus.night = when (scheduleData.night) {
                    NightType.BOOKED -> DayStatus.NightEnum.BOOKED
                    NightType.UNAVAILABLE -> DayStatus.NightEnum.UNAVAILABLE
                    NightType.PENDING -> DayStatus.NightEnum.PENDING
                    NightType.CONFIRMED -> DayStatus.NightEnum.CONFIRMED
                    NightType.REJECT -> DayStatus.NightEnum.REJECT
                    else -> DayStatus.NightEnum.AVAILABLE
                }
                dayTypeHashMap.put(i, dayStatus)
            }
            if (dayTypeGet == null) {
                adapter = CalendarAdapter(baseActivity, currentCal, dayTypeHashMap)
                flexCal.setAdapter(adapter, dayTypeHashMap)
            } else {
                dayTypeGet?.onDayMapFilled(dayTypeHashMap)
            }
        }
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
        flexCal.visibility = View.VISIBLE
    }
}