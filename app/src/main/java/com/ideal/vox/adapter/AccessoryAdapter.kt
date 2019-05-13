package com.ideal.vox.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.AccessoryData
import com.ideal.vox.utils.CircleTransform
import com.ideal.vox.utils.Const


class AccessoryAdapter(private val activity: BaseActivity, private val data: ArrayList<AccessoryData>, private val userId: Int, private val showEdit: Boolean)
    : RecyclerView.Adapter<AccessoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_accessory, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val daata = data[holder.adapterPosition]
        activity.picasso.load(Const.IMAGE_ACC_BASE_URL + "/${userId}/" + daata.picture).transform(CircleTransform()).into(holder.picIV)
        holder.nameTV.text = daata.name
        holder.makeTV.text = daata.model
        if (showEdit) {
            holder.removeIV.visibility = View.VISIBLE
            holder.removeIV.setOnClickListener { activity.showToast("Yet to implement", true) }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var picIV: ImageView = view.findViewById(R.id.picIV)
        var nameTV: MyTextView = view.findViewById(R.id.nameTV)
        var makeTV: MyTextView = view.findViewById(R.id.makeTV)
        var removeIV: ImageView = view.findViewById(R.id.removeIV)
    }
}