package com.ideal.vox.adapter

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.home.HomeFragment
import com.ideal.vox.fragment.home.detail.UserDetailFragment
import com.ideal.vox.utils.CircleTransform
import com.ideal.vox.utils.Const


class HomeAdapter(private val activity: BaseActivity, private val datas: ArrayList<UserData>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int) = if (datas[position].id == -1) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        if (viewType == 0) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_home, parent, false)
            viewHolder = MyViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.inc_load_more, parent, false)
            viewHolder = ProgressViewHolder(itemView)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val data = datas[holder.adapterPosition]
            if (data.avatar != null && data.avatar.isNotEmpty())
                activity.picasso.load(Const.IMAGE_BASE_URL + "/${data.avatar}")
                        .transform(CircleTransform()).placeholder(R.drawable.ic_camera).error(R.drawable.ic_camera)
                        .into(holder.picIV)
            holder.nameTV.text = "Name: ${data.name}"
            holder.expTV.text = "Experience: ${data.photoProfile?.experienceInYear} years, ${data.photoProfile?.experienceInMonths} months"
            holder.expertTV.text = "Expert in: ${data.photoProfile?.expertise}"
            holder.parentCL.setOnClickListener {
                (activity as MainActivity).userData = data
                val bndl = Bundle()
                bndl.putParcelable("data", data)
                val frag = UserDetailFragment()
                frag.arguments = bndl
                activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fc_home, frag)
                        .addToBackStack(null)
                        .commit()

                val fragg = activity.supportFragmentManager.findFragmentById(R.id.fc_home)
                if (fragg is HomeFragment)
                    fragg.resetEdittext()
            }
        }
    }

    fun updateLoadMoreView(isLoading: Boolean) {
        if (isLoading) {
            datas.add(UserData(id = -1))
            notifyItemInserted(datas.size - 1)
        } else {
            if (datas.size > 0 && datas[datas.size - 1].id == -1) {
                datas.removeAt(datas.size - 1)
                notifyItemRemoved(datas.size)
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var parentCL: ConstraintLayout = view.findViewById(R.id.parentCL)
        var picIV: ImageView = view.findViewById(R.id.picIV)
        var nameTV: MyTextView = view.findViewById(R.id.nameTV)
        var expTV: MyTextView = view.findViewById(R.id.expTV)
        var expertTV: MyTextView = view.findViewById(R.id.expertTV)
    }

    inner class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view)
}