package com.ideal.vox.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.data.PagerPicData
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.isNotNullAndEmpty
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class HomeAdPagerAdapter(internal var context: BaseActivity, internal var images: ArrayList<PagerPicData>) : PagerAdapter() {

    internal var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.adapter_home_viewpager, container, false)

        val imageView = itemView.findViewById(R.id.picIV) as ImageView
        val data = images[position]
        if (data.url.isNotNullAndEmpty()) {
            val tgt = MyTarget(imageView)
            context.picasso.load("${Const.IMAGE_SLIDER_BASE_URL}/${data.url}")
                    .placeholder(R.drawable.ic_camera_yellow_large).error(R.drawable.ic_camera_yellow_large).into(tgt)
            imageView.tag = tgt
        }

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

    inner class MyTarget(private val image: ImageView) : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            if (bitmap != null) {
                image.setImageBitmap(bitmap)
            }
        }
    }
}