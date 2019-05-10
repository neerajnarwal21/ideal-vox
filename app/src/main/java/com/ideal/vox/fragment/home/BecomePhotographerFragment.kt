package com.ideal.vox.fragment.home

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.data.ExpertiseData
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.AddLocationPinFragment
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.PinAdd
import com.ideal.vox.utils.selection
import kotlinx.android.synthetic.main.fg_form_photographer.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Neeraj Narwal on 5/5/17.
 */
class BecomePhotographerFragment : BaseFragment() {

    private var confirmCall: Call<JsonObject>? = null
    private var listCall: Call<JsonObject>? = null
    private var data: UserData? = null
    private var list = ArrayList<ExpertiseData>()
    private var currentCal: Calendar = Calendar.getInstance()
    private var dobCal: Calendar = Calendar.getInstance()
    private var latLng: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_form_photographer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(false, "Details")
        initUI()
    }

    private fun initUI() {
        dobCal.roll(Calendar.YEAR, -20)
        listCall = apiInterface.getExpertise()
        apiManager.makeApiCall(listCall!!, this)
        dobTIL.setOnClickListener { showDateDialog() }
        dobET.setOnClickListener { showDateDialog() }
        addPinIV.setOnClickListener { if (getText(addressET).isNotEmpty()) gotoAddPin() else showToast("Enter address first") }
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
        }

        val bundle = Bundle()
        bundle.putBoolean("isPin", true)
        bundle.putString("address", getText(addressET))
        val fragment = AddLocationPinFragment()
        fragment.arguments = bundle

        baseActivity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fc_home, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    override fun onResume() {
        super.onResume()
        if (latLng != null) {
            addPinIV.selection(true)
            addPinIV.setImageResource(R.drawable.ic_pin_added)
        }
    }

    private fun submit() {
        val expertise = RequestBody.create(MediaType.parse("text/plain"), list[expSP.selectedItemPosition].name)
        val expYear = RequestBody.create(MediaType.parse("text/plain"), if (getText(yearET).isEmpty()) "0" else getText(yearET))
        val expMonth = RequestBody.create(MediaType.parse("text/plain"), if (getText(monthET).isEmpty()) "0" else getText(monthET))
        val dob = RequestBody.create(MediaType.parse("text/plain"), SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dobCal.time))
        val gender = RequestBody.create(MediaType.parse("text/plain"), if (maleRB.isChecked) "M" else "F")
        val address = RequestBody.create(MediaType.parse("text/plain"), getText(addressET))
        val lat = RequestBody.create(MediaType.parse("text/plain"), latLng?.latitude.toString())
        val lng = RequestBody.create(MediaType.parse("text/plain"), latLng?.longitude.toString())
        confirmCall = apiInterface.becomePhotographer(expertise, expYear, expMonth, dob, gender, address, lat, lng)
        apiManager.makeApiCall(confirmCall!!, this)
    }

    private fun validate(): Boolean {
        when {
            getText(monthET).isEmpty() && getText(yearET).isEmpty() -> showToast("Please enter experience", true)
            getText(monthET).isNotEmpty() && getText(monthET).toInt() > 12 -> showToast("Month value is wrong in experience")
            getText(dobET).isEmpty() -> showToast("Please enter date of birth", true)
            getText(addressET).isEmpty() -> showToast("Please enter address", true)
            latLng == null -> {
                showToast("Please add location pin for address", true)
                addPinIV.selection(false)
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

            showSuccessDialog()
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
        }
    }

    private fun showSuccessDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Registeration Success")
        bldr.setMessage("You have successfully registered as photographer\n" +
                "You can add payment details in profile section")
        bldr.setCancelable(false)
        bldr.setPositiveButton("Ok, Great") { _, _ ->
            val intent = Intent(baseActivity, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
            startActivity(intent)
            baseActivity.finish()
        }
        bldr.create().show()
    }

    private fun loadSpinner(array: Array<String?>) {
        val spinnerArrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item_dark, array)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        expSP.adapter = spinnerArrayAdapter
    }
}
