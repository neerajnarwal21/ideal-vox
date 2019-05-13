package com.ideal.vox.fragment.profile

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.*
import kotlinx.android.synthetic.main.fg_p_basic.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileBasicFragment : BaseFragment() {


    private var avatarCall: Call<JsonObject>? = null
    private var updateCall: Call<JsonObject>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_basic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, "Profile", false)
        initUI()
    }

    private fun initUI() {
        val userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        if (userData != null) {
            nameET.setText(userData.name)
            emailET.setText(userData.email)
            mobileET.setText(userData.mobileNumber)
            if (userData.avatar.isNotEmpty()) {
                baseActivity.picasso.load(Const.IMAGE_BASE_URL + userData.avatar).placeholder(R.drawable.ic_camera).error(R.drawable.ic_camera).transform(CircleTransform()).into(picIV)
            }
        }
        picIV.setOnClickListener {
            if (PermissionsManager.checkPermissions(baseActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 12,
                            PermissionsManager.PCallback({ selectImage() }))) selectImage()
        }
        nameIV.setOnClickListener {
            nameET.isEnabled = true
            nameET.isFocusableInTouchMode = true
            saveBT.visibility = View.VISIBLE
            saveBT.setOnClickListener {
                if (getText(nameET).isEmpty()) {
                    showToast("Please enter name")
                } else {
                    updateProfile()
                }
            }
        }
    }

    private fun updateProfile() {
        val name = RequestBody.create(MediaType.parse("text/plain"), getText(nameET))
        updateCall = apiInterface.updateProfile(name)
        apiManager.makeApiCall(updateCall!!, this)
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
        } else if (updateCall != null && updateCall === call) {
            val jsonObj = payload as JsonObject
            val userData = Gson().fromJson(jsonObj, UserData::class.java)
            store.saveUserData(Const.USER_DATA, userData)
            (baseActivity as MainActivity).setupHeaderView()
        }
    }
}
