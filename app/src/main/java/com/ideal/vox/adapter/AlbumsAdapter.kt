package com.ideal.vox.adapter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.ideal.vox.R
import com.ideal.vox.activity.BaseActivity
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.profile.AlbumData
import com.ideal.vox.di.GlideApp
import com.ideal.vox.fragment.home.detail.UserAlbumDetailFragment
import com.ideal.vox.fragment.profile.albums.ProfileAlbumDetailFragment
import com.ideal.vox.fragment.profile.albums.ProfileAlbumsFragment
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.blur
import com.ideal.vox.utils.toBitmap


class AlbumsAdapter(private val activity: BaseActivity, private val datas: ArrayList<AlbumData>, private val userId: Int, private val frag: ProfileAlbumsFragment?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int) = if (datas[position].id == -1) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        if (viewType == 0) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_profile_album, parent, false)
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
                frag?.addUpdateAlbumDialog(null)
            }
        } else if (holder is MyViewHolder) {
            val data = datas[holder.adapterPosition]
            holder.picIV.setOnLongClickListener {
                frag?.addUpdateAlbumDialog(data)
                true
            }
            holder.picIV.setOnClickListener {
                val bndl = Bundle()
                bndl.putParcelable("data", data)

                val fragg = if (frag != null) ProfileAlbumDetailFragment() else UserAlbumDetailFragment()
                fragg.arguments = bndl
                activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fc_home, fragg)
                        .addToBackStack(null)
                        .commit()
            }
            if (data.pictures.size > 0) {
                GlideApp.with(activity).load("${Const.IMAGE_ALBUM_BASE_URL}/${userId}/${data.id}/${data.pictures[0].name}")
                        .dontTransform().listener(GlideTgt(holder.picIV)).into(holder.picIV)

//                activity.picasso.load("${Const.IMAGE_ALBUM_BASE_URL}/${userId}/${data.id}/${data.pictures[0].name}")
//                        .placeholder(R.drawable.ic_camera_yellow_large).error(R.drawable.ic_camera_yellow_large).into(MyTarget(holder.picIV))
            }
            holder.titleTV.text = data.name
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleTV: MyTextView = view.findViewById(R.id.titleTV)
        var picIV: ImageView = view.findViewById(R.id.picIV)
    }

    inner class AddViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var picIV: ImageView = view.findViewById(R.id.picIV)
    }

    inner class GlideTgt(val imageView: ImageView) : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            imageView.setImageBitmap(blur(activity,resource!!.toBitmap(),0.7f,20f))
            return true
        }

    }
}