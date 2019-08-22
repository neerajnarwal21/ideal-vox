package com.ideal.vox.fragment.profile.albums

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.AlbumsAdapter
import com.ideal.vox.customViews.MyEditText
import com.ideal.vox.customViews.MyTextView
import com.ideal.vox.data.profile.AlbumData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_p_albums.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileAlbumsFragment : BaseFragment() {

    private var listCall: Call<JsonObject>? = null
    private var addCall: Call<JsonObject>? = null
    private var updateCall: Call<JsonObject>? = null
    private var adapter: AlbumsAdapter? = null
    private var userData: UserData? = null
    private var MAX_ITEMS = 6

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userData = store.getUserData(Const.USER_DATA, UserData::class.java)

        listRV.layoutManager = GridLayoutManager(baseActivity, 2)
        if (userData != null) {
            loadingPB.visibility = View.VISIBLE
            listCall = apiInterface.allAlbums(userData!!.id)
            apiManager.makeApiCall(listCall!!, this, false)
        }
    }

    fun addUpdateAlbumDialog(data: AlbumData?) {
        val bldr = AlertDialog.Builder(baseActivity)
        val dialog: AlertDialog
        val view = View.inflate(baseActivity, R.layout.dialog_add_album, null)
        bldr.setView(view)
        val titleTV = view.findViewById<MyTextView>(R.id.titleTV)
        val nameET = view.findViewById<MyEditText>(R.id.nameET)
        if (data != null) nameET.setText(data.name)
        titleTV.text = if (data != null) "Update album name" else "Add album"
        bldr.setPositiveButton("Submit") { _, _ -> }
        bldr.setNegativeButton("Cancel", null)
        dialog = bldr.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.colorPrimary)
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (!getText(nameET).isEmpty()) {
                dialog.dismiss()
                if (data != null) {
                    updateAlbum(getText(nameET), data)
                } else
                    addAlbum(getText(nameET))
                baseActivity.hideSoftKeyboard()
            } else {
                showToast("Please enter name", true)
            }
        }
    }

    private fun addAlbum(name: String) {
        val namee = RequestBody.create(MediaType.parse("text/plain"), name)
        addCall = apiInterface.createAlbum(namee)
        apiManager.makeApiCall(addCall!!, this)
    }

    private fun updateAlbum(name: String, data: AlbumData) {
        val namee = RequestBody.create(MediaType.parse("text/plain"), name)
        updateCall = apiInterface.renameAlbum(data.id, namee)
        apiManager.makeApiCall(updateCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        loadingPB.visibility = View.GONE
        if (listCall != null && listCall == call) {
            setupUI(payload)
        } else if (addCall != null && addCall === call) {
            apiClient.clearCache()
            setupUI(payload)
        } else if (updateCall != null && updateCall === call) {
            apiClient.clearCache()
            setupUI(payload)
        }
    }

    private fun setupUI(payload: Any) {
        val jsonObj = payload as JsonObject
        val listArr = jsonObj.get("albums").asJsonArray
        val objectType = object : TypeToken<ArrayList<AlbumData>>() {}.type
        val datas = Gson().fromJson<ArrayList<AlbumData>>(listArr, objectType)
//        if (jsonObj.has("max_album"))
//            MAX_ITEMS = jsonObj["max_album"].asInt
        if (datas.size < MAX_ITEMS) {
            datas.add(AlbumData(id = -1))
        }
        adapter = AlbumsAdapter(baseActivity, datas, userData!!.id, this)
        listRV.adapter = adapter
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}