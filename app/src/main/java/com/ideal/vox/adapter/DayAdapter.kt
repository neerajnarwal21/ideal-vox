package com.ideal.vox.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.DayData


class DayAdapter(private val activity: BaseActivity, private val datas: ArrayList<DayData>)
    : RecyclerView.Adapter<DayAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_month_day, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = datas[holder.adapterPosition]
        holder.dayTV.text = data.day
        holder.dayIV.isSelected = data.isDayShift
        holder.nightIV.isSelected  =data.isNightShift
        holder.dayIV.setOnClickListener {
            data.isDayShift = !data.isDayShift
            notifyDataSetChanged()
        }
        holder.nightIV.setOnClickListener {
            data.isNightShift = !data.isNightShift
            notifyDataSetChanged()
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