package com.ideal.vox.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.activity.main.PicGalleryActivity
import com.ideal.vox.data.profile.PicData
import com.ideal.vox.fragment.profile.albums.ProfileAlbumDetailFragment
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.bitmapToFile
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


class PicAdapter(private val activity: BaseActivity, private val datas: ArrayList<PicData>, private val userId: Int, private val frag: ProfileAlbumDetailFragment?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int) = if (datas[position].id == -1) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        if (viewType == 0) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_profile_album_pic, parent, false)
            viewHolder = MyViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_profile_album_add, parent, false)
            viewHolder = AddViewHolder(itemView)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddViewHolder) {
            holder.picIV.setOnClickListener {
                frag?.selectImage()
            }
        } else if (holder is MyViewHolder) {
            val data = datas[holder.adapterPosition]
            val tgt = MyTarget(data, holder.picIV)
            activity.picasso.load("${Const.IMAGE_ALBUM_BASE_URL}/${userId}/${data.albumId}/${data.name}")
                    .placeholder(R.drawable.ic_camera_yellow_large).error(R.drawable.ic_camera_yellow_large).into(tgt)
            holder.picIV.tag = tgt
            holder.picIV.setOnClickListener {
                val intent = Intent(activity, PicGalleryActivity::class.java)
                intent.putExtra("pos", position)
                intent.putExtra("userId", userId)
                intent.putParcelableArrayListExtra("data", datas)
                activity.startActivity(intent)
            }
            holder.picIV.setOnLongClickListener {
                frag?.deletePicDialog(data)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var picIV: ImageView = view.findViewById(R.id.picIV)
    }

    inner class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var picIV: ImageView = view.findViewById(R.id.picIV)
    }


    inner class MyTarget(val data: PicData, val imageView: ImageView) : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            imageView.setImageBitmap(bitmap)
            data.bitmapPath = bitmapToFile(bitmap!!,activity).absolutePath
        }
    }
}