package com.ideal.vox.fragment

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.ideal.vox.R
import com.ideal.vox.utils.LocationManager
import com.ideal.vox.utils.PinAdd
import kotlinx.android.synthetic.main.fg_ls_add_pin.*


/**
 * Created by neeraj.narwal on 16/1/17.
 */

class AddLocationPinFragment : BaseFragment(), LocationManager.LocationUpdates {

    private var googleMap: GoogleMap? = null
    private var myCurrentLocation: Location? = null
    //    private var mLocationManager: LocationManager? = null
    private var locationManager = LocationManager()
    private var goBack: Boolean = false
    private var isPin: Boolean = false
    private var addressToAddPin: String? = null
    private var vieww: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey("isPin")) {
            isPin = arguments!!.getBoolean("isPin")
            addressToAddPin = arguments!!.getString("address")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (vieww != null) {
            val parent = vieww?.parent as ViewGroup
            parent.removeView(vieww)
        }
        setToolbar(false, "Add Address")
        try {
            vieww = inflater.inflate(R.layout.fg_ls_add_pin, container, false)
        } catch (e: InflateException) {
            e.printStackTrace()
        }


        return vieww
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log("View created >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        initUI()
    }

    override fun onResume() {
        super.onResume()
        if (goBack)
            baseActivity.onBackPressed()
    }

    private fun initUI() {
        locationManager.startLocationManager(baseActivity, LocationManager.Accuracy.HIGH, this)
        doneBT.setOnClickListener {
            if (googleMap != null) {
                if (!isPin) {
                    val latLng = googleMap!!.cameraPosition.target
                    val address = getAddress(latLng, baseActivity)
                    if (address != null) {
                        PinAdd.sendBackLocation(address, latLng)
                        baseActivity.onBackPressed()
                    } else {
                        showToast("Please select some nearby place where's address could be fetched", true)
                    }
                } else {
                    showConfirmDialog()
                }
            }
        }
        if (isPin) {
            addressTV.text = baseActivity.getString(R.string.move_the_map_to_set_pin, addressToAddPin)
        }
    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(baseActivity)
        builder.setTitle("Confirm")
        builder.setMessage("Are you sure this is exact location for address $addressToAddPin")
        builder.setPositiveButton("Yes") { _, _ ->
            val latLng = googleMap!!.cameraPosition.target
            PinAdd.sendBackLocation(addressToAddPin, latLng)
            baseActivity.onBackPressed()
        }
        builder.setNegativeButton("No", null)
        builder.create().show()
    }

    private fun initializeMap() {
        if (googleMap == null) {
            val mapFrag = SupportMapFragment.newInstance()
            mapFrag.getMapAsync {
                it.isMyLocationEnabled = true
                googleMap = it
                getAndSetCurrentLocation()
                if (!isPin) it.setOnCameraIdleListener { updateText() }
            }
            childFragmentManager.beginTransaction().replace(R.id.map, mapFrag).commit();
        }
    }

    private fun updateText() {
        val latLng = googleMap!!.cameraPosition.target
        val address = getAddress(latLng, baseActivity)
        addressTV.text = address ?: "No address to this location"
    }

    private fun getAndSetCurrentLocation() {
        if (myCurrentLocation != null && googleMap != null) {
            val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(myCurrentLocation!!.latitude, myCurrentLocation!!.longitude)).zoom(15f).build()
            googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun getAddress(latLng: LatLng, context: Context): String? {
        try {
            val geocoder = Geocoder(context)
            val addresses: List<Address>
            if (latLng.latitude != 0.0 || latLng.longitude != 0.0) {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val address = addresses[0].getAddressLine(0)
                log("Address >>>>> $address")
//                val address1 = addresses[0].getAddressLine(1)
//                log("Address 1 >>>>> $address1")
//                val country = addresses[0].countryName
//                log("Country >>>>> " + country!!)
                return address.replace("null,", "")
            } else {
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }


    override fun onStartingGettingLocation() {
        initializeMap()
    }

    override fun onLocationFound(location: Location) {
        myCurrentLocation = location
        getAndSetCurrentLocation()
    }

    override fun onLocationNotFound() {
        goBack = true
    }

    override fun onLocationPermissionDenied() {
        goBack = true
    }

    fun onGPSEnable() {
        locationManager.onGPSEnable()
    }

    fun onGPSEnableDenied() {
        showToast("Please check GPS is ON and working fine!", true)
        goBack = true
    }


//    override fun onLocationChanged(location: Location) {
//
//    }
//
//    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {
//
//    }
//
//    override fun onProviderEnabled(provider: String) {
//        if (provider.equals(LocationManager.GPS_PROVIDER, ignoreCase = true))
//            getAndSetCurrentLocation()
//    }
//
//    override fun onProviderDisabled(provider: String) {
//        if (provider.equals(LocationManager.GPS_PROVIDER, ignoreCase = true))
//            buildAlertMessageNoGps()
//
//    }
}
