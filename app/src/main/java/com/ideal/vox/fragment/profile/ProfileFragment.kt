package com.ideal.vox.fragment.profile

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.adapter.PageAdapter
import com.ideal.vox.data.UserData
import com.ideal.vox.databinding.FgProfileBinding
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.*
import kotlinx.android.synthetic.main.fg_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileFragment : BaseFragment() {

    lateinit var model: ProfileViewModel
    private var avatarCall: Call<JsonObject>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FgProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fg_profile, container, false)

        model = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding.model = model
        binding.setLifecycleOwner(this)
        val view = binding.getRoot()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setToolbar(true, "Profile")
        model.getStatus().observe(this, Observer {
            when (it) {
                ProfileStatus.ABOUT, ProfileStatus.ABOUT_PAGER -> {
                    aboutTV.doColorChange(false)
                    albumsTV.doColorChange(true)
                    aboutIV.isSelected = true
                    albumsIV.isSelected = false
                }
                ProfileStatus.ALBUMS, ProfileStatus.ALBUMS_PAGER -> {
                    aboutTV.doColorChange(true)
                    albumsTV.doColorChange(false)
                    aboutIV.isSelected = false
                    albumsIV.isSelected = true
                }
            }
            when (it) {
                ProfileStatus.ABOUT -> pager.currentItem = 0
                ProfileStatus.ALBUMS -> pager.currentItem = 1
            }
        })

        val adapter = PageAdapter(childFragmentManager)
        val frag = ProfileAboutFragment()
        val frag1 = ProfileAlbumsFragment()
        adapter.addFragment(frag)
        adapter.addFragment(frag1)
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

    private fun updateUI() {
        val userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        if (userData != null) {
            nameTV.setText(userData.name)
            if (userData.avatar.isNotEmpty()) {
                baseActivity.picasso.load(Const.IMAGE_BASE_URL + userData.avatar).placeholder(R.drawable.ic_camera).error(R.drawable.ic_camera).transform(CircleTransform()).into(picIV)
            }
        }
        picIV.setOnClickListener {
            if (PermissionsManager.checkPermissions(baseActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 12,
                            PermissionsManager.PCallback({ selectImage() }))) selectImage()
        }
    }

    private fun selectImage() {
        ImageUtils.Builder(baseActivity, 11) { imagePath, resultCode ->
            val bitmap = imageCompress(imagePath)
            val file = bitmapToFile(bitmap, baseActivity)
            baseActivity.picasso.load(file).transform(CircleTransform()).into(picIV)
            updateUser(file)
        }.crop().start()
    }

    private fun updateUser(file: File) {
        var fileFirst: MultipartBody.Part? = null
        try {
            baseActivity.log("File size: ${file.length()}")
            fileFirst = MultipartBody.Part.createFormData("avatar", file.name, RequestBody.create(MediaType.parse("image/jpeg"), file))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        avatarCall = apiInterface.updateDP(fileFirst)
        apiManager.makeApiCall(avatarCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (avatarCall != null && call === avatarCall) {
            val jsonObj = payload as JsonObject
            val userData = Gson().fromJson(jsonObj, UserData::class.java)
            store.saveUserData(Const.USER_DATA, userData)
            (baseActivity as MainActivity).setupHeaderView()
            showToast("Profile picture updated")
        }
    }
}
