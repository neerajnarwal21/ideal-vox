package com.ideal.vox.activity

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import com.ideal.vox.R
import com.pugtools.fcalendar.data.CalendarAdapter
import com.pugtools.fcalendar.data.DayStatus
import com.pugtools.fcalendar.widget.DayClose
import com.pugtools.fcalendar.widget.DayTypeGet
import com.pugtools.fcalendar.widget.FlexibleCalendar
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*
import kotlin.collections.HashMap


class TestActivity : BaseActivity() {

    private var cal = Calendar.getInstance()
    val dayTypeHashMap = SparseArray<DayStatus>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test)

        val firstDayCal = cal.getActualMinimum(Calendar.DAY_OF_MONTH)
        val lastDayCal = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in firstDayCal..lastDayCal) {
            val dayStatus = DayStatus()
            dayStatus.day = DayStatus.DayEnum.UNAVAILABLE
            dayStatus.night = DayStatus.NightEnum.BOOKED
            dayTypeHashMap.put(i, dayStatus)
        }

        val adapter = CalendarAdapter(this, cal, dayTypeHashMap)
        flexCal.setAdapter(adapter, dayTypeHashMap)
        flexCal.setCalendarListener(object : FlexibleCalendar.CalendarListener {
            override fun onDaySelect() {
            }

            override fun onDayClick(v: View?, day: Int, dayClose: DayClose?) {
                dayTypeHashMap[day]?.day = if (dayTypeHashMap[day]?.day == DayStatus.DayEnum.AVAILABLE) DayStatus.DayEnum.UNAVAILABLE else DayStatus.DayEnum.AVAILABLE
                adapter.refresh(dayTypeHashMap)
                flexCal.reload()

                showToast("Day click", false)
                log("Day click")
            }

            override fun onNightClick(v: View?, day: Int, dayClose: DayClose?) {
                showToast("Night click", false)
                log("Night click")
            }

            override fun onDataUpdate() {
            }

            override fun onMonthChange(currentCal: Calendar, dayTypeGet: DayTypeGet?) {
                resetHashMap(currentCal, dayTypeGet)
            }

            override fun onWeekChange(position: Int) {
            }

        })
    }

    private fun resetHashMap(currentCal: Calendar, dayTypeGet: DayTypeGet?) {
        dayTypeHashMap.clear()
        val firstDayCal = currentCal.getActualMinimum(Calendar.DAY_OF_MONTH)
        val lastDayCal = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in firstDayCal..lastDayCal) {
            dayTypeHashMap.put(i, DayStatus())
        }
        dayTypeGet?.onDayMapFilled(dayTypeHashMap)
    }
}