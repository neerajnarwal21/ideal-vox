package com.ideal.vox.adapter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.data.PicData
import com.ideal.vox.utils.Const
import java.io.File

class PicPagerAdapter(internal var context: BaseActivity, private val userId: Int, internal var images: ArrayList<PicData>) : PagerAdapter() {

    internal var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.adapter_album_gallery, container, false)

        val imageView = itemView.findViewById(R.id.picIV) as ImageView
        val data = images[position]
        if (data.bitmapPath == null)
            context.picasso.load("${Const.IMAGE_ALBUM_BASE_URL}/${userId}/${data.albumId}/${data.name}")
                    .placeholder(R.drawable.ic_camera_yellow_large).error(R.drawable.ic_camera_yellow_large).into(imageView)
        else
            context.picasso.load(File(data.bitmapPath))
                    .placeholder(R.drawable.ic_camera_yellow_large).error(R.drawable.ic_camera_yellow_large).into(imageView)

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}