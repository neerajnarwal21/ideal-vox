package com.ideal.vox.adapter

import android.support.v7.widget.AppCompatRatingBar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.RatingData
import com.ideal.vox.utils.CircleTransform
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.isNotNullAndEmpty


class ReviewsAdapter(private val activity: BaseActivity, private val datas: ArrayList<RatingData>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_reviews, parent, false)
        viewHolder = MyViewHolder(itemView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val data = datas[holder.adapterPosition]
            if (data.reviewUser?.avatar.isNotNullAndEmpty())
                activity.picasso.load(Const.IMAGE_BASE_URL + "/${data.reviewUser?.avatar}")
                        .transform(CircleTransform()).resize(100,100).placeholder(R.drawable.ic_camera).error(R.drawable.ic_camera)
                        .into(holder.picIV)
            holder.nameTV.text = data.reviewUser?.name
            holder.ratingRB.rating = data.rating
            holder.reviewTV.text = data.reviews
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var picIV: ImageView = view.findViewById(R.id.picIV)
        var nameTV: MyTextView = view.findViewById(R.id.nameTV)
        var ratingRB: AppCompatRatingBar = view.findViewById(R.id.ratingRB)
        var reviewTV: MyTextView = view.findViewById(R.id.reviewTV)
    }
}