package com.ideal.vox.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.profile.Accessory
import com.ideal.vox.data.profile.AccessoryData
import com.ideal.vox.fragment.profile.about.ProfileEditAccessoryFragment
import com.ideal.vox.utils.CircleTransform
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.animateImage
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder


class AccessoryAdapter(private val activity: BaseActivity, groups: ArrayList<Accessory>, private val userId: Int, private val showEdit: Boolean)
    : ExpandableRecyclerViewAdapter<AccessoryAdapter.HeadingViewHolder, AccessoryAdapter.MyViewHolder>(groups) {

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): HeadingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_accessory_header, parent, false)
        return HeadingViewHolder(itemView)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_accessory, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindGroupViewHolder(holder: HeadingViewHolder, flatPosition: Int, group: ExpandableGroup<*>?) {
        holder.titleTV.text = group?.title
    }

    override fun onBindChildViewHolder(holder: MyViewHolder, flatPosition: Int, group: ExpandableGroup<*>?, childIndex: Int) {
        val daata = (group?.items as ArrayList<AccessoryData>)[childIndex]
        activity.picasso.load(Const.IMAGE_ACC_BASE_URL + "/${userId}/" + daata.picture)
                .resize(180,140)
                .placeholder(R.drawable.ic_camera).error(R.drawable.ic_camera)
                .into(holder.picIV)
        holder.nameTV.text = daata.name
        holder.makeTV.text = daata.model
        if (showEdit) {
            holder.removeIV.visibility = View.VISIBLE
            holder.removeIV.setOnClickListener {
                val frag = activity.supportFragmentManager.findFragmentById(R.id.fc_home)
                if (frag is ProfileEditAccessoryFragment) {
                    frag.deleteAccessory(daata.id)
                }
            }
        }
    }

    inner class MyViewHolder(view: View) : ChildViewHolder(view) {
        var picIV: ImageView = view.findViewById(R.id.picIV)
        var nameTV: MyTextView = view.findViewById(R.id.nameTV)
        var makeTV: MyTextView = view.findViewById(R.id.makeTV)
        var removeIV: ImageView = view.findViewById(R.id.removeIV)
    }

    inner class HeadingViewHolder(view: View) : GroupViewHolder(view) {
        var titleTV: MyTextView = view.findViewById(R.id.titleTV)
        var expandIV: ImageView = view.findViewById(R.id.expandIV)
        override fun expand() {
            super.expand()
            animateImage(expandIV, true)
        }

        override fun collapse() {
            super.collapse()
            animateImage(expandIV, false)
        }
    }
}