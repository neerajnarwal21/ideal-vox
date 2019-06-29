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
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.data.CityData
import com.ideal.vox.data.ExpertiseData
import com.ideal.vox.data.UserData
import com.ideal.vox.data.UserType
import com.ideal.vox.fragment.AddLocationPinFragment
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.PinAdd
import com.ideal.vox.utils.isNotNullAndEmpty
import com.ideal.vox.utils.mySpinnerCallback
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
    private var cityCall: Call<JsonObject>? = null
    private var otpCall: Call<JsonObject>? = null
    private var list = ArrayList<ExpertiseData>()
    private var currentCal: Calendar = Calendar.getInstance()
    private var dobCal: Calendar = Calendar.getInstance()
    private var latLng: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_form_photographer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("Details")
        showChooserDialog()
    }

    private fun showChooserDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Welcome")
        bldr.setMessage("Before joining, Please tell you want to join us as a photographer or as a helper?")
        bldr.setPositiveButton("Photographer") { dialogInterface, _ ->
            initUI(UserType.PHOTOGRAPHER)
            dialogInterface.dismiss()
        }
        bldr.setNegativeButton("Helper") { dialogInterface, _ ->
            initUI(UserType.HELPER)
            dialogInterface.dismiss()
        }
        bldr.setCancelable(false)
        bldr.create().show()
    }

    private fun initUI(user: UserType) {
        if (user == UserType.HELPER) {
            expertiseTV.visibility = View.GONE
            expSP.visibility = View.GONE
        } else {
            listCall = apiInterface.getExpertise()
            apiManager.makeApiCall(listCall!!, this)
        }
        cityCall = apiInterface.getCities()
        apiManager.makeApiCall(cityCall!!, this, false)

        sendOTPtoUser()
        dobCal.roll(Calendar.YEAR, -25)
        dobTIL.setOnClickListener { showDateDialog() }
        dobET.setOnClickListener { showDateDialog() }
        locTV.setOnClickListener { if (getText(addressET).isNotEmpty()) gotoAddPin() else showToast("Enter address first") }
        submitBT.setOnClickListener { if (validate()) submit(user) }
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
            setToolbar("Details")
            locTV.visibility = View.VISIBLE
        }

        val bundle = Bundle()
        bundle.putBoolean("isPin", true)
        bundle.putString("address", getText(addressET))
        val fragment = AddLocationPinFragment()
        fragment.arguments = bundle

        baseActivity.supportFragmentManager
                .beginTransaction()
                .add(R.id.fc_home, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    private fun submit(user: UserType) {
        val expertise = if (list.size > 0) RequestBody.create(MediaType.parse("text/plain"), list[expSP.selectedItemPosition].name) else null
        val expYear = RequestBody.create(MediaType.parse("text/plain"), if (getText(yearET).isEmpty()) "0" else getText(yearET))
        val expMonth = RequestBody.create(MediaType.parse("text/plain"), if (getText(monthET).isEmpty()) "0" else getText(monthET))
        val dob = RequestBody.create(MediaType.parse("text/plain"), SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dobCal.time))
        val gender = RequestBody.create(MediaType.parse("text/plain"), if (maleRB.isChecked) "M" else "F")
        val address = RequestBody.create(MediaType.parse("text/plain"), getText(addressET))
        val state = RequestBody.create(MediaType.parse("text/plain"), stateSP.selectedItem.toString())
        val city = RequestBody.create(MediaType.parse("text/plain"), citySP.selectedItem.toString())
        val pin = RequestBody.create(MediaType.parse("text/plain"), getText(pinET))
        val lat = RequestBody.create(MediaType.parse("text/plain"), latLng?.latitude.toString())
        val lng = RequestBody.create(MediaType.parse("text/plain"), latLng?.longitude.toString())
        val otp = RequestBody.create(MediaType.parse("text/plain"), getText(otpET))
        val about = RequestBody.create(MediaType.parse("text/plain"), getText(aboutET))
        val userType = RequestBody.create(MediaType.parse("text/plain"), Gson().toJson(user).replace("\"", ""))
        confirmCall = apiInterface.becomePhotographer(expertise, expYear, expMonth, dob, gender, address, state, city, pin, lat, lng, otp, about, userType)
        apiManager.makeApiCall(confirmCall!!, this)
    }

    private fun validate(): Boolean {
        when {
            getText(monthET).isEmpty() && getText(yearET).isEmpty() -> showToast("Please enter experience", true)
            getText(monthET).isNotEmpty() && getText(monthET).toInt() > 12 -> showToast("Month value is wrong in experience")
            getText(dobET).isEmpty() -> showToast("Please enter date of birth", true)
            getText(addressET).isEmpty() -> showToast("Please enter address", true)
            getText(pinET).isEmpty() -> showToast("Please enter pincode", true)
            getText(otpET).isEmpty() -> showToast("Please enter OTP", true)
            latLng == null -> gotoAddPin()
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
        } else if (otpCall != null && otpCall === call) {
            val jsonObj = payload as JsonObject

            val userObj = jsonObj.getAsJsonObject("user")
            val userData = Gson().fromJson(userObj, UserData::class.java)
            otpTV.setText("An OTP has been sent to ${userData.mobileNumber} kindly enter that here")
        } else if (cityCall != null && cityCall === call) {
            val jsonArr = payload as JsonArray
            val objectType = object : TypeToken<ArrayList<CityData>>() {}.type
            val datas = Gson().fromJson<ArrayList<CityData>>(jsonArr, objectType)
            val stateMap = TreeMap<String, ArrayList<String>>(kotlin.Comparator { t1, t2 -> t1.compareTo(t2) })
            log("Total cities ${datas.size}")
            for (data in datas) {
                if (stateMap.containsKey(data.state)) {
                    stateMap.get(data.state)?.add(data.city!!)
                } else
                    stateMap.put(data.state!!, arrayListOf(data.city!!))
            }
            val spinnerArrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item_dark, stateMap.keys.toMutableList())
            spinnerArrayAdapter.setDropDownViewResource(R.layout.adapter_simple_item_list)
            stateSP.adapter = spinnerArrayAdapter


            stateSP.mySpinnerCallback {
                val arrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item_dark, stateMap.get(it)!!.toMutableList())
                arrayAdapter.setDropDownViewResource(R.layout.adapter_simple_item_list)
                citySP.adapter = arrayAdapter
            }
        }
    }

    private fun sendOTPtoUser() {
        val userData = store.getUserData(Const.USER_DATA, UserData::class.java)
        if (userData != null && userData.mobileNumber.isNotNullAndEmpty()) {
            val phone = RequestBody.create(MediaType.parse("text/plain"), userData.mobileNumber)
            otpCall = apiInterface.resendOTPToPhone(phone)
            apiManager.makeApiCall(otpCall!!, this, false)
        } else {
            showToast("Please enter your phone number in profile first")
            baseActivity.onBackPressed()
        }
    }

    private fun showSuccessDialog() {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Registeration Success")
        bldr.setMessage("You have successfully registered with us\n" +
                "It is recommended to add your remaining details and price details in profile section.")
        bldr.setCancelable(false)
        bldr.setPositiveButton("Ok, Great", null)
        bldr.setOnDismissListener {
            val intent = Intent(baseActivity, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP }
            startActivity(intent)
            baseActivity.finish()
        }
        bldr.create().show()
    }

    private fun loadSpinner(array: Array<String?>) {
        val spinnerArrayAdapter = ArrayAdapter<String>(baseActivity, R.layout.adapter_simple_item_dark, array)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.adapter_simple_item_list)
        expSP.adapter = spinnerArrayAdapter
    }
}