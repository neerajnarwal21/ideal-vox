package com.ideal.vox.fragment.profile.edit

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.adapter.CategoryPriceAdapter
import com.ideal.vox.customViews.MyEditText
import com.ideal.vox.data.UserData
import com.ideal.vox.data.profile.CategoryData
import com.ideal.vox.data.profile.CategoryPriceData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.fg_p_edit_accessories.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileEditPriceDetailsFragment : BaseFragment() {

    private var addCall: Call<JsonObject>? = null
    private var listCall: Call<JsonObject>? = null
    private var deleteCall: Call<JsonObject>? = null
    private var catListCall: Call<JsonObject>? = null
    private val list = ArrayList<CategoryData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_edit_advance_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getList()
        addIV.setOnClickListener { loadCategories() }
    }

    private fun getList() {
        val data = store.getUserData(Const.USER_DATA, UserData::class.java)
        emptyTV.visibility = View.GONE
        listRV.visibility = View.GONE
        loadingPB.visibility = View.VISIBLE
        listCall = apiInterface.userCategories(data!!.id)
        apiManager.makeApiCall(listCall!!, this, false)
    }

    private fun loadCategories() {
        catListCall = apiInterface.allCategories()
        apiManager.makeApiCall(catListCall!!, this)
    }


    fun showAddDialog(array: Array<String?>, isUpdate: Boolean = false, itemId: Int = 0) {
        val bldr = AlertDialog.Builder(baseActivity)
        val dialog: AlertDialog
        val view = View.inflate(baseActivity, R.layout.dialog_add_category, null)
        bldr.setView(view)
        val priceET = view.findViewById<MyEditText>(R.id.priceET)
        val catSP = view.findViewById<Spinner>(R.id.catSP)
        val spinnerArrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item_dark, array)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.adapter_simple_item_list)
        catSP.adapter = spinnerArrayAdapter
        bldr.setPositiveButton(if (isUpdate) "Update" else "Add") { _, _ -> }
        bldr.setNegativeButton(if (isUpdate) "Delete" else "Cancel") { _, _ -> }
        dialog = bldr.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.colorPrimary)
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(baseActivity, R.color.WhiteSmoke))
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            if (getText(priceET).isNotEmpty()) {
                dialog.dismiss()
                submit(getText(priceET), if (isUpdate) array[0]!! else list[catSP.selectedItemPosition].name)
                baseActivity.hideSoftKeyboard()
            } else showToast("Please enter price", true)
        }
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
            dialog.dismiss()
            if (isUpdate) deleteAccessory(itemId)
        }
    }

    private fun deleteAccessory(id: Int) {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Confirm !")
        bldr.setMessage("Are you sure you want to delete this price category?")
        bldr.setPositiveButton("Yes") { dialogInterface, i ->
            deleteCall = apiInterface.deleteCategory(id)
            apiManager.makeApiCall(deleteCall!!, this)
            dialogInterface.dismiss()
        }
        bldr.setNegativeButton("No", null)
        bldr.create().show()
    }

    private fun submit(pricee: String, name: String) {
        val price = RequestBody.create(MediaType.parse("text/plain"), pricee)
        val category = RequestBody.create(MediaType.parse("text/plain"), name)

        addCall = apiInterface.addCategory(category, price)
        apiManager.makeApiCall(addCall!!, this)
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (catListCall != null && catListCall === call) {
            val jsonArray = payload as JsonArray
            val array = arrayOfNulls<String>(jsonArray.size())
            for ((i, datas) in jsonArray.withIndex()) {
                val obj = datas.asJsonObject
                val data = Gson().fromJson(obj, CategoryData::class.java)
                array[i] = data.name
                list.add(data)
            }
            showAddDialog(array)
        } else if (addCall != null && addCall === call) {
            getList()
        } else if (deleteCall != null && deleteCall === call) {
            getList()
        } else if (listCall != null && listCall === call) {
            val jsonArr = payload as JsonArray
            val objectType = object : TypeToken<ArrayList<CategoryPriceData>>() {}.type
            val datas = Gson().fromJson<ArrayList<CategoryPriceData>>(jsonArr, objectType)
            if (datas.size == 0) emptyTV.visibility = View.VISIBLE
            listRV.adapter = CategoryPriceAdapter(datas, this)
            listRV.visibility = View.VISIBLE
            loadingPB.visibility = View.GONE
        }
    }
}