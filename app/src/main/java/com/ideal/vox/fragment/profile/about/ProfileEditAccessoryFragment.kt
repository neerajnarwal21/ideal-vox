package com.ideal.vox.fragment.profile.about

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.AccessoryAdapter
import com.ideal.vox.customViews.MyEditText
import com.ideal.vox.data.UserData
import com.ideal.vox.data.profile.Accessory
import com.ideal.vox.data.profile.CategoryData
import com.ideal.vox.data.profile.AccessoryData
import com.ideal.vox.data.profile.KeyData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.*
import kotlinx.android.synthetic.main.fg_p_edit_accessories.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileEditAccessoryFragment : BaseFragment() {
    var file: File? = null
    private var addCall: Call<JsonObject>? = null
    private var deleteCall: Call<JsonObject>? = null
    private var catListCall: Call<JsonObject>? = null
    private var nameET: MyEditText? = null
    private var makeET: MyEditText? = null
    private var catSP: Spinner? = null
    private var picIV: ImageView? = null

    private var adapter: AccessoryAdapter? = null
    private var listCall: Call<JsonObject>? = null
    private val list = ArrayList<CategoryData>()

    private var userData: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_edit_accessories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("Edit Accessories")
        initUI()
    }

    private fun initUI() {
        addIV.visibility = View.VISIBLE
        addIV.setOnClickListener { loadCategories() }
        getList()
    }

    private fun loadCategories() {
        catListCall = apiInterface.getAccessoryCategories()
        apiManager.makeApiCall(catListCall!!, this)
    }

    private fun getList() {
        userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        if (userData != null) {
            listCall = apiInterface.allAccessories(userData!!.id)
            apiManager.makeApiCall(listCall!!, this, false)
        }
    }

    private fun showAddDialog(array: Array<String?>) {
        file = null
        val bldr = AlertDialog.Builder(baseActivity)
        val dialog: AlertDialog
        val view = View.inflate(baseActivity, R.layout.dialog_add_accessory, null)
        bldr.setView(view)
        nameET = view.findViewById(R.id.nameET)
        makeET = view.findViewById(R.id.makeET)
        catSP = view.findViewById(R.id.catSP)
        picIV = view.findViewById(R.id.picIV)
        picIV?.setOnClickListener {
            if (PermissionsManager.checkPermissions(baseActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 12,
                            PermissionsManager.PCallback({ selectImage() }))) selectImage()
        }
        val spinnerArrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item_dark, array)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.adapter_simple_item_list)
        catSP!!.adapter = spinnerArrayAdapter
        bldr.setPositiveButton("Add") { _, _ -> }
        bldr.setNegativeButton("Cancel", null)
        dialog = bldr.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.colorPrimary)
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (validate()) {
                dialog.dismiss()
                addAccessory()
                baseActivity.hideSoftKeyboard()
            }
        }
    }

    private fun addAccessory() {
        val name = RequestBody.create(MediaType.parse("text/plain"), getText(nameET!!))
        val make = RequestBody.create(MediaType.parse("text/plain"), getText(makeET!!))
        val category = RequestBody.create(MediaType.parse("text/plain"), list[catSP!!.selectedItemPosition].name)
        var fileFirst: MultipartBody.Part? = null
        try {
            baseActivity.log("File size: ${file?.length()}")
            fileFirst = MultipartBody.Part.createFormData("picture", file?.name, RequestBody.create(MediaType.parse("image/jpeg"), file))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addCall = apiInterface.updateAccessories(name, make, category, fileFirst)
        apiManager.makeApiCall(addCall!!, this)
    }

    fun deleteAccessory(id: Int) {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Confirm !")
        bldr.setMessage("Are you sure you want to delete this accessory?")
        bldr.setPositiveButton("Yes") { dialogInterface, i ->
            deleteCall = apiInterface.deleteAccessory(id)
            apiManager.makeApiCall(deleteCall!!, this)
            dialogInterface.dismiss()
        }
        bldr.setNegativeButton("No", null)
        bldr.create().show()
    }

    private fun validate(): Boolean {
        when {
            file == null -> showToast("Please add image", true)
            getText(nameET!!).isEmpty() -> showToast("Please enter name", true)
            getText(makeET!!).isEmpty() -> showToast("Please enter make model", true)
            else -> return true
        }
        return false
    }

    private fun selectImage() {
        ImageUtils.Builder(baseActivity, 11) { imagePath, resultCode ->
            val bitmap = imageCompress(imagePath)
            picIV?.setImageBitmap(bitmap)
            file = bitmapToFile(bitmap, baseActivity)
            baseActivity.picasso.load(file).transform(CircleTransform()).into(picIV)
        }.crop().start()
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (addCall != null && addCall === call) {
            showToast("Successfully added")
            apiClient.clearCache()
            getList()
        } else if (deleteCall != null && deleteCall === call) {
            showToast("Accessory deleted")
            apiClient.clearCache()
            getList()
        } else if (listCall != null && listCall === call) {
            setupAdapter(payload)
        } else if (catListCall != null && catListCall === call) {
            val jsonArray = payload as JsonArray
            val array = arrayOfNulls<String>(jsonArray.size())
            for ((i, datas) in jsonArray.withIndex()) {
                val obj = datas.asJsonObject
                val data = Gson().fromJson(obj, CategoryData::class.java)
                array[i] = data.name
                list.add(data)
            }
            showAddDialog(array)
        }
    }

    private fun setupAdapter(payload: Any) {
        loadingPB.visibility = View.GONE
        val jsonObj = payload as JsonObject
        val listObj = jsonObj.get("accessories").asJsonObject

        val keysArr = listObj["keys"].asJsonArray
        val objectType = object : TypeToken<ArrayList<KeyData>>() {}.type
        val datas = Gson().fromJson<ArrayList<KeyData>>(keysArr, objectType)
        val list = ArrayList<Accessory>()
        for (data in datas) {
            val arr = listObj[data.key].asJsonArray
            val objType = object : TypeToken<ArrayList<AccessoryData>>() {}.type
            val accDatas = Gson().fromJson<ArrayList<AccessoryData>>(arr, objType)
            if (accDatas.size > 0)
                list.add(Accessory(data.key, accDatas))
        }
        adapter = AccessoryAdapter(baseActivity, list, userData!!.id, true)
        if (list.size == 0) emptyTV.visibility = View.VISIBLE else emptyTV.visibility = View.GONE
        listRV.adapter = adapter
    }

    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        super.onError(call, statusCode, errorMessage, responseListener)
        loadingPB.visibility = View.GONE
    }
}
