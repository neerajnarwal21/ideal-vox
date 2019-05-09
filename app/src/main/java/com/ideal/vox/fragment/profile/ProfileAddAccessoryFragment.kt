package com.ideal.vox.fragment.profile

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonArray
import com.ideal.vox.R
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.*
import kotlinx.android.synthetic.main.fg_p_add_accessory.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileAddAccessoryFragment : BaseFragment() {
    var file: File? = null
    private var addCall: Call<JsonArray>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fg_p_add_accessory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, "Add Accessory")
        initUI()
    }

    private fun initUI() {
        picIV.setOnClickListener {
            if (PermissionsManager.checkPermissions(baseActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 12,
                            PermissionsManager.PCallback({ selectImage() }))) selectImage()
        }
        addBT.setOnClickListener { if (validate()) addAccessory() }
    }

    private fun addAccessory() {

        val userId = RequestBody.create(MediaType.parse("text/plain"), store.getString(Const.SESSION_KEY, "")!!)
        val name = RequestBody.create(MediaType.parse("text/plain"), getText(nameET))
        val make = RequestBody.create(MediaType.parse("text/plain"), getText(makeET))
        var fileFirst: MultipartBody.Part? = null
        try {
            baseActivity.log("File size: ${file?.length()}")
            fileFirst = MultipartBody.Part.createFormData("Pimage", file?.name, RequestBody.create(MediaType.parse("image/jpeg"), file))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addCall = apiInterface.updateAccessories(userId, name, make, fileFirst)
        apiManager.makeApiCall(addCall!!, this)
    }

    private fun validate(): Boolean {
        when {
            getText(nameET).isEmpty() -> showToast("Please enter name", true)
            getText(makeET).isEmpty() -> showToast("Please enter make model", true)
            else -> return true
        }
        return false
    }

    private fun selectImage() {
        ImageUtils.Builder(baseActivity, 11) { imagePath, resultCode ->
            val bitmap = imageCompress(imagePath)
            picIV.setImageBitmap(bitmap)
            file = bitmapToFile(bitmap, baseActivity)
            baseActivity.picasso.load(file).transform(CircleTransform()).into(picIV)
        }.crop().start()
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        showToast("Success")
    }
}
