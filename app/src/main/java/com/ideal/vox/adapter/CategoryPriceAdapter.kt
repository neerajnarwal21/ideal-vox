package com.ideal.vox.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.profile.CategoryPriceData
import com.ideal.vox.fragment.profile.edit.ProfileEditPriceDetailsFragment


class CategoryPriceAdapter(private val datas: ArrayList<CategoryPriceData>, private val frag: ProfileEditPriceDetailsFragment? = null)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_profile_category_price, parent, false)
        viewHolder = MyViewHolder(itemView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val data = datas[holder.adapterPosition]
            holder.nameTV.text = data.category
            holder.priceTV.text = "Start From: ₹${data.price}"
            if (frag != null) holder.removeIV.visibility = View.VISIBLE
            holder.removeIV.setOnClickListener {
                frag?.showAddDialog(arrayOf(data.category!!),true,data.id!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameTV: MyTextView = view.findViewById(R.id.nameTV)
        var priceTV: MyTextView = view.findViewById(R.id.priceTV)
        var removeIV: ImageView = view.findViewById(R.id.removeIV)
    }
}