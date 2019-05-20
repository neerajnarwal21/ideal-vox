package com.ideal.vox.fragment.profile.albums

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.PicAdapter
import com.ideal.vox.data.AlbumData
import com.ideal.vox.data.PicData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.ImageUtils
import com.ideal.vox.utils.PermissionsManager
import com.ideal.vox.utils.bitmapToFile
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fg_p_albums.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileAlbumDetailFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null
    private var addCall: Call<JsonObject>? = null
    private var deleteCall: Call<JsonObject>? = null
    private var adapter: PicAdapter? = null
    var file: File? = null
    private var picIV: ImageView? = null
    private var albumData: AlbumData? = null
    private var MAX_ITEMS = 5
    private var userData: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey("data"))
            albumData = arguments!!.getParcelable("data")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        setToolbar(albumData!!.name)
        listRV.layoutManager = GridLayoutManager(baseActivity, 2)
        if (albumData != null) {
            loadingPB.visibility = View.VISIBLE
            listCall = apiInterface.allAlbumPics(albumData!!.id)
            apiManager.makeApiCall(listCall!!, this, false)
        }

//        val list = ArrayList<AlbumData>()
//        list.add(AlbumData(id = -1))
//        list.add(AlbumData(id = -1))
//        listRV.adapter = AlbumsAdapter(baseActivity, list, this)
    }

    fun addPicDialog() {
        file = null
        val bldr = AlertDialog.Builder(baseActivity)
        val dialog: AlertDialog
        val view = View.inflate(baseActivity, R.layout.dialog_add_pic, null)
        bldr.setView(view)
        picIV = view.findViewById(R.id.picIV)
        picIV?.setOnClickListener {
            if (PermissionsManager.checkPermissions(baseActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 12,
                            PermissionsManager.PCallback({ selectImage() }))) selectImage()
        }
        bldr.setPositiveButton("Add Pic") { _, _ -> }
        bldr.setNegativeButton("Cancel", null)
        dialog = bldr.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.colorPrimary)
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (file != null) {
                dialog.dismiss()
                addPic()
                baseActivity.hideSoftKeyboard()
            } else {
                showToast("Please add Pic", true)
            }
        }
    }


    fun deletePicDialog(data: PicData) {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Delete Pic")
        bldr.setMessage("Do you want to remove this pic from this album ?")
        bldr.setPositiveButton("Yes") { _, _ ->
            deletePic(data)
        }
        bldr.setNegativeButton("No", null)
        bldr.create().show()
    }

    private fun addPic() {
        val albumId = RequestBody.create(MediaType.parse("text/plain"), albumData?.id.toString())
        var fileFirst: MultipartBody.Part? = null
        try {
            baseActivity.log("File size: ${file?.length()}")
            fileFirst = MultipartBody.Part.createFormData("album_pic", file?.name, RequestBody.create(MediaType.parse("image/jpeg"), file))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addCall = apiInterface.addPic(albumId, fileFirst)
        apiManager.makeApiCall(addCall!!, this)
    }

    private fun selectImage() {
        ImageUtils.Builder(baseActivity, 11) { imagePath, resultCode ->
            val bitmap = Compressor(baseActivity).setMaxHeight(1000).setMaxWidth(1000).compressToBitmap(File(imagePath))
//            val bitmap = imageCompress(imagePath, 2000.0f, 2000.0f)
            picIV?.setImageBitmap(bitmap)
            file = bitmapToFile(bitmap, baseActivity)
            baseActivity.picasso.load(file).into(picIV)
        }.start()
    }

    private fun deletePic(data: PicData) {
        deleteCall = apiInterface.deletePicture(data.id)
        apiManager.makeApiCall(deleteCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        if (listCall != null && listCall == call) {
            setupUI(payload)
        } else if (addCall != null && addCall === call) {
            apiClient.clearCache()
            setupUI(payload)
        } else if (deleteCall != null && deleteCall === call) {
            apiClient.clearCache()
            setupUI(payload)
        }
    }

    private fun setupUI(payload: Any) {
        val jsonObj = payload as JsonObject
        val listArr = jsonObj.get("pictures").asJsonArray
        val objectType = object : TypeToken<ArrayList<PicData>>() {}.type
        val datas = Gson().fromJson<ArrayList<PicData>>(listArr, objectType)
        if (jsonObj.has("max_picture"))
            MAX_ITEMS = jsonObj["max_picture"].asInt
        if (datas.size < MAX_ITEMS) {
            datas.add(PicData(id = -1))
        }
        adapter = PicAdapter(baseActivity, datas, userData!!.id, this)
        listRV.adapter = adapter
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}