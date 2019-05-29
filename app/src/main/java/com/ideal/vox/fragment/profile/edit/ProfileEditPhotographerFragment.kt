package com.ideal.vox.fragment.profile.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.data.ExpertiseData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.AddLocationPinFragment
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.PinAdd
import com.ideal.vox.utils.getDateFromStringDate
import kotlinx.android.synthetic.main.fg_p_edit_advance_photographer.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class ProfileEditPhotographerFragment : BaseFragment() {

    private var confirmCall: Call<JsonObject>? = null
    private var listCall: Call<JsonObject>? = null
    private var data: UserData? = null
    private var list = ArrayList<ExpertiseData>()
    private var currentCal: Calendar = Calendar.getInstance()
    private var dobCal: Calendar = Calendar.getInstance()
    private var latLng: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_p_edit_advance_photographer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = store.getUserData(Const.USER_DATA, UserData::class.java)
        listCall = apiInterface.getExpertise()
        apiManager.makeApiCall(listCall!!, this)
    }

    private fun initUI() {
        for ((i, item) in list.withIndex()) {
            if (data?.photoProfile?.expertise == item.name) {
                expSP.setSelection(i)
                break
            }
        }
        yearET.setText(data?.photoProfile?.experienceInYear.toString())
        monthET.setText(data?.photoProfile?.experienceInMonths.toString())
        dobCal.time = getDateFromStringDate(data?.photoProfile?.dob, "yyyy-MM-dd")
        dobET.setText(SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(dobCal.time))
        if (data?.photoProfile?.gender == "M") maleRB.isChecked = true else femaleRB.isChecked = true
        ytET.setText(data?.photoProfile?.youtube)
        addressET.setText(data?.photoProfile?.address)
        pinET.setText(data?.photoProfile?.pinCode)
        locTV.setText("Update Location pin")
        locTV.setOnClickListener { if (getText(addressET).isNotEmpty()) gotoAddPin() else showToast("Enter address first") }
        latLng = LatLng(data?.photoProfile?.lat!!, data?.photoProfile?.lng!!)

        dobTIL.setOnClickListener { showDateDialog() }
        dobET.setOnClickListener { showDateDialog() }
        submitBT.setOnClickListener { if (validate()) submit() }
    }

    private fun showDateDialog() {
        val datePickerDialog = DatePickerDialog(baseActivity,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    dobCal = Calendar.getInstance()
                    dobCal.set(year, monthOfYear, dayOfMonth)
                    if (dobCal.after(currentCal)) {
                        showToast("Please select a past date")
                    } else {
                        dobET.setText(SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(dobCal.time))
                    }
                }, dobCal.get(Calendar.YEAR), dobCal.get(Calendar.MONTH), dobCal.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }


    private fun gotoAddPin() {
        PinAdd.setListener { address, latLng ->
            this.latLng = latLng
            locTV.setText("Location pin set")
        }

        val bundle = Bundle()
        bundle.putBoolean("isPin", true)
        bundle.putString("address", getText(addressET))
        bundle.putDouble("lat", latLng!!.latitude)
        bundle.putDouble("lng", latLng!!.longitude)
        val fragment = AddLocationPinFragment()
        fragment.arguments = bundle

        baseActivity.supportFragmentManager
                .beginTransaction()
                .add(R.id.fc_home, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    private fun submit() {
        val expertise = RequestBody.create(MediaType.parse("text/plain"), list[expSP.selectedItemPosition].name)
        val expYear = RequestBody.create(MediaType.parse("text/plain"), if (getText(yearET).isEmpty()) "0" else getText(yearET))
        val expMonth = RequestBody.create(MediaType.parse("text/plain"), if (getText(monthET).isEmpty()) "0" else getText(monthET))
        val dob = RequestBody.create(MediaType.parse("text/plain"), SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dobCal.time))
        val gender = RequestBody.create(MediaType.parse("text/plain"), if (maleRB.isChecked) "M" else "F")
        val address = RequestBody.create(MediaType.parse("text/plain"), getText(addressET))
        val pin = RequestBody.create(MediaType.parse("text/plain"), getText(pinET))
        val lat = RequestBody.create(MediaType.parse("text/plain"), latLng?.latitude.toString())
        val lng = RequestBody.create(MediaType.parse("text/plain"), latLng?.longitude.toString())
        val yt = RequestBody.create(MediaType.parse("text/plain"), getText(ytET))
        confirmCall = apiInterface.becomePhotographer(null,expertise, expYear, expMonth, dob, gender, address, pin, lat, lng, yt)
        apiManager.makeApiCall(confirmCall!!, this)
    }

    private fun validate(): Boolean {
        when {
            getText(monthET).isEmpty() && getText(yearET).isEmpty() -> showToast("Please enter experience", true)
            getText(monthET).isNotEmpty() && getText(monthET).toInt() > 12 -> showToast("Month value is wrong in experience")
            getText(dobET).isEmpty() -> showToast("Please enter date of birth", true)
            getText(addressET).isEmpty() -> showToast("Please enter address", true)
            getText(pinET).isEmpty() -> showToast("Please enter pincode", true)
            latLng == null -> {
                showToast("Please add location pin for address", true)
                gotoAddPin()
            }
            else -> return true
        }
        return false
    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (confirmCall != null && call === confirmCall) {
            val jsonObj = payload as JsonObject

            val userObj = jsonObj.getAsJsonObject("user")
            val userData = Gson().fromJson(userObj, UserData::class.java)
            store.saveUserData(Const.USER_DATA, userData)
            showToast("Details updated successfully")
        } else if (listCall != null && call === listCall) {
            list.clear()
            val jsonArr = payload as JsonArray
            val array = arrayOfNulls<String>(jsonArr.size())
            for ((i, datas) in jsonArr.withIndex()) {
                val obj = datas.asJsonObject
                val data = Gson().fromJson(obj, ExpertiseData::class.java)
                array[i] = data.name
                list.add(data)
            }
            loadSpinner(array)
            initUI()
        }
    }

    private fun loadSpinner(array: Array<String?>) {
        val spinnerArrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item_dark, array)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.adapter_simple_item_list)
        expSP.adapter = spinnerArrayAdapter
    }
}
