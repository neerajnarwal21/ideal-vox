package com.ideal.vox.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.DayType
import com.ideal.vox.data.NightType
import com.ideal.vox.data.ScheduleData
import com.ideal.vox.utils.changeDateFormat


class ScheduleSetAdapter(private val activity: BaseActivity, private val datas: ArrayList<ScheduleData>)
    : RecyclerView.Adapter<ScheduleSetAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_month_day, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = datas[holder.adapterPosition]
        holder.dayTV.text = changeDateFormat(data.scheduleDate, "yyyy-MM-dd", "dd MMM")
        holder.dayIV.isSelected = data.day == DayType.UNAVAILABLE
        holder.nightIV.isSelected = data.night == NightType.UNAVAILABLE
        if (data.day != DayType.BOOKED)
            holder.dayIV.setOnClickListener {
                if (holder.adapterPosition == 0) activity.showToast("Changing availability of current day isn't allowed.", false)
                else {
                    data.day = if (data.day == DayType.AVAILABLE) DayType.UNAVAILABLE else DayType.AVAILABLE
                    notifyDataSetChanged()
                }
            }
        if (data.night != NightType.BOOKED)
            holder.nightIV.setOnClickListener {
                if (holder.adapterPosition == 0) activity.showToast("Changing availability of current day isn't allowed.", false)
                else {
                    data.night = if (data.night == NightType.AVAILABLE) NightType.UNAVAILABLE else NightType.AVAILABLE
                    notifyDataSetChanged()
                }
            }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var dayIV: ImageView = view.findViewById(R.id.dayIV)
        var nightIV: ImageView = view.findViewById(R.id.nightIV)
        var dayTV: MyTextView = view.findViewById(R.id.dayTV)
    }
}