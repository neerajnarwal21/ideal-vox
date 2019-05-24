package com.ideal.vox.activity

import android.os.Bundle
import com.ideal.vox.R


class TestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fg_h_map_view)
//        val list = ArrayList<DayData>()
//        val cal = Calendar.getInstance()
//        for (i in 0..13) {
////            val status:Boolean
////            status = if (i % 2 == 0) dayStatus.isDayShift = true
////            if (i % 3 == 0) dayStatus.isDayShift = false
////            if (i % 5 == 0) dayStatus.isNightShift = true
////            if (i % 7 == 0) dayStatus.isNightShift = false
//
//            val dayStatus = DayData(true, false, cal[Calendar.DAY_OF_MONTH].toString()
//                    + SimpleDateFormat(" MMM\n(EEE)", Locale.ENGLISH).format(cal.time))
//            cal.roll(Calendar.DAY_OF_YEAR, 1)
//            list.add(dayStatus)
//        }
//        listRV.layoutManager = GridLayoutManager(this, 7)
//        listRV.adapter = ScheduleSetAdapter(this, list)
//
//        bt.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
//        }
    }
}