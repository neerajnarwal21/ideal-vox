package com.ideal.vox.fragment.profile.albums

import android.Manifest
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.PicAdapter
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.profile.AlbumData
import com.ideal.vox.data.profile.PicData
import com.ideal.vox.data.UserData
import com.ideal.vox.di.MyGlideEngine
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.*
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fg_p_albums_view.*
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
    private var albumData: AlbumData? = null
    private var MAX_ITEMS = 10
    private var userData: UserData? = null
    private var datas = ArrayList<PicData>()
    private var progressPB: ProgressBar? = null
    private var progressTV: MyTextView? = null
    private val list = ArrayList<File?>()
    private var currentPic: Int = 0
    private var dialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey("data"))
            albumData = arguments!!.getParcelable("data")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_albums_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        setToolbar(albumData!!.name)
        listRV.layoutManager = GridLayoutManager(baseActivity, 2)
        if (albumData != null) {
            loadingPB.visibility = View.VISIBLE
            getList()
        }
    }

    private fun getList() {
        listCall = apiInterface.allAlbumPics(albumData!!.id)
        apiManager.makeApiCall(listCall!!, this, false)
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
        progressPB?.progress = currentPic + 1
        progressTV?.text = "Uploading ${currentPic + 1} of ${list.size}"

        val file = list[currentPic]
        val albumId = RequestBody.create(MediaType.parse("text/plain"), albumData?.id.toString())
        var fileFirst: MultipartBody.Part? = null
        try {
            baseActivity.log("File size: ${file?.length()}")
            fileFirst = MultipartBody.Part.createFormData("album_pic", file?.name, RequestBody.create(MediaType.parse("image/jpeg"), file))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addCall = apiInterface.addPic(albumId, fileFirst)
        apiManager.makeApiCall(addCall!!, this, false)
    }

    fun selectImage() {
        if (PermissionsManager.checkPermissions(baseActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 12,
                        PermissionsManager.PCallback({ selectImage() }))) {
            list.clear()
            Matisse.from(baseActivity)
                    .choose(MimeType.ofImage())
                    .showSingleMediaType(true)
                    .countable(true)
                    .maxSelectable(MAX_ITEMS - datas.size + 1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    .thumbnailScale(0.85f)
                    .imageEngine(MyGlideEngine())
                    .theme(R.style.Matisse_Dracula)
                    .forResult(Const.REQUEST_CODE_CHOOSE)
            MatisseList.setListener { uriList ->
                if (uriList != null) {
                    for (data in uriList) {
                        val path = getRealPath(baseActivity, data)
                        log("Size before >>>>" + File(path).length())
                        val bitmap = Compressor(baseActivity).setMaxHeight(1000).setMaxWidth(1000).compressToBitmap(File(path))
                        list.add(bitmapToFile(bitmap, baseActivity))
                        log("Size>>>>" + list[list.size - 1]?.length())
                    }
                    showPicUploadDialog()
                }
            }
        }
    }

    private fun showPicUploadDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        val view = View.inflate(baseActivity, R.layout.dialog_upload_pics, null)
        progressPB = view.findViewById(R.id.progressPB)
        progressTV = view.findViewById(R.id.progressTV)
        progressPB!!.max = list.size
        bldr.setPositiveButton("Cancel") { _, _ -> }
        bldr.setView(view)
        dialog = bldr.create()
        dialog?.show()
        dialog?.window?.setBackgroundDrawableResource(R.color.colorPrimary)
        dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
            addCall?.cancel()
            dialog?.dismiss()
        }
        currentPic = 0
        addPic()
    }

    private fun deletePic(data: PicData) {
        deleteCall = apiInterface.deletePicture(data.id)
        apiManager.makeApiCall(deleteCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        datas.clear()
        loadingPB.visibility = View.GONE
        if (listCall != null && listCall == call) {
            setupUI(payload)
        } else if (addCall != null && addCall === call) {
            if (currentPic + 1 < list.size) {
                currentPic++
                addPic()
            } else {
                dialog?.dismiss()
                apiClient.clearCache()
                setupUI(payload)
            }
        } else if (deleteCall != null && deleteCall === call) {
            apiClient.clearCache()
            setupUI(payload)
        }
    }

    private fun setupUI(payload: Any) {
        val jsonObj = payload as JsonObject
        val listArr = jsonObj.get("pictures").asJsonArray
        val objectType = object : TypeToken<ArrayList<PicData>>() {}.type
        datas = Gson().fromJson<ArrayList<PicData>>(listArr, objectType)
//        if (jsonObj.has("max_picture"))
//            MAX_ITEMS = jsonObj["max_picture"].asInt
        if (datas.size < MAX_ITEMS) {
            datas.add(PicData(id = -1))
        }
        adapter = PicAdapter(baseActivity, datas, userData!!.id, this)
        listRV.adapter = adapter
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        if (addCall != null && addCall === call) {
            dialog?.dismiss()
            apiClient.clearCache()
            getList()
        } else
            super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}