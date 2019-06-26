package com.ideal.vox.fragment.home.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.adapter.PageAdapter
import com.ideal.vox.data.UserData
import com.ideal.vox.data.UserType
import com.ideal.vox.databinding.FgUserDetailBinding
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.doColorChange
import com.ideal.vox.utils.isNotNullAndEmpty
import com.ideal.vox.utils.showFullScreenImage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.fg_user_detail.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class UserDetailFragment : BaseFragment() {

    lateinit var model: UserViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FgUserDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fg_user_detail, container, false)

        model = ViewModelProviders.of(this).get(UserViewModel::class.java)
        binding.model = model
        binding.setLifecycleOwner(this)
        val view = binding.getRoot()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("", showToolbar = false)
        val data = (baseActivity as MainActivity).userData
        if (data?.userType == UserType.HELPER) topCL.visibility = View.GONE
        model.getStatus().observe(this, Observer {
            when (it) {
                UserStatus.ABOUT, UserStatus.ABOUT_PAGER -> {
                    aboutTV.doColorChange(false)
                    albumsTV.doColorChange(true)
                    aboutCL.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorPrimary2))
                    albumsCL.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorPrimary))
                    aboutIV.isSelected = true
                    albumsIV.isSelected = false
                }
                UserStatus.ALBUMS, UserStatus.ALBUMS_PAGER -> {
                    aboutTV.doColorChange(true)
                    albumsTV.doColorChange(false)
                    aboutCL.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorPrimary))
                    albumsCL.setBackgroundColor(ContextCompat.getColor(baseActivity, R.color.colorPrimary2))
                    aboutIV.isSelected = false
                    albumsIV.isSelected = true
                }
            }
            when (it) {
                UserStatus.ABOUT -> pager.currentItem = 0
                UserStatus.ALBUMS -> pager.currentItem = 1
            }
        })

        val adapter = PageAdapter(childFragmentManager)
        val frag = UserAboutFragment()
        adapter.addFragment(frag)

        if (data?.userType == UserType.PHOTOGRAPHER) {
            val frag1 = UserAlbumsFragment()
            adapter.addFragment(frag1)
        }
        pager.adapter = adapter
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> model.onAboutPagerClick()
                    1 -> model.onAlbumsPagerClick()
                }
            }
        })
        model.onAboutClick()

        updateUI()
    }

    private var tgt: MyTarget? = null

    private fun updateUI() {
        val userData = (baseActivity as MainActivity).userData
        if (userData != null) {
            collapseTL.title = userData.name
            if (userData.avatar.isNotNullAndEmpty()) {
                tgt = MyTarget()
                baseActivity.picasso.load(Const.IMAGE_BASE_URL + userData.avatar).placeholder(R.drawable.ic_camera).error(R.drawable.ic_camera).into(tgt)
                picIV.tag = tgt
            }
        }
        backIV.setOnClickListener { baseActivity.onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        baseActivity.picasso.cancelRequest(tgt)
    }

    inner class MyTarget : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            if (bitmap != null) {
                picIV.setImageBitmap(bitmap)
                picIV.setOnClickListener { showFullScreenImage(baseActivity, bitmap) }
            }
        }
    }
}
