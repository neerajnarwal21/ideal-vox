package com.ideal.vox.fragment.home

import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.data.UserData
import com.ideal.vox.di.GlideApp
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.LocationManager
import com.ideal.vox.utils.createDrawableFromView
import com.ideal.vox.utils.toBitmap
import kotlinx.android.synthetic.main.dialog_help_dont_ask.view.*
import kotlinx.android.synthetic.main.fg_h_map_view.*
import retrofit2.Call


/**
 * Created by neeraj.narwal on 16/1/17.
 */

class HelpFragment : BaseFragment(), LocationManager.LocationUpdates {

    private var googleMap: GoogleMap? = null
    private var myCurrentLocation: Location? = null
    private var locationManager = LocationManager()
    private var goBack: Boolean = false
    private var vieww: View? = null
    private var listCall: Call<JsonObject>? = null
    private val markerHashMap = HashMap<Marker, UserData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        try {
            if (vieww != null) {
                val parent = vieww?.parent as ViewGroup
                parent.removeView(vieww)
            }
            vieww = inflater.inflate(R.layout.fg_h_map_view, container, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return vieww
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar("Help")
        distanceCV.visibility = View.GONE
        log("View created >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        if (store.getBoolean(Const.SHOW_HELP_DIALOG, true))
            showHelpDialog()
        else
            initUI()
    }

    private fun showHelpDialog(showDontShow: Boolean = true) {
        val builder = AlertDialog.Builder(baseActivity)
        builder.setTitle("Need some help!")
        builder.setMessage("Using this feature, in any emergency situation you can find people nearby registered with us.")
        val view = View.inflate(baseActivity, R.layout.dialog_help_dont_ask, null)
        builder.setView(view)
        if (!showDontShow) view.dontAskCB.visibility = View.GONE
        builder.setPositiveButton("Ok") { _, _ ->
            if (view.dontAskCB.isChecked) {
                store.setBoolean(Const.SHOW_HELP_DIALOG, false)
            }
        }
        builder.setOnDismissListener { if(showDontShow)initUI() }
        builder.create().show()
    }

    override fun onResume() {
        super.onResume()
        if (goBack) baseActivity.onBackPressed()
    }

    private fun initUI() {
        if (myCurrentLocation == null) {
            locationManager.startLocationManager(baseActivity, LocationManager.Accuracy.HIGH, this)
        }
        helpIV.apply {
            visibility = View.VISIBLE
            setOnClickListener { showHelpDialog(false) }
        }
    }

    fun getList() {
        log("LatLng>>> ${myCurrentLocation?.latitude}, ${myCurrentLocation?.longitude}")
        if (myCurrentLocation != null) {
            apiClient.clearCache()
            listCall = apiInterface.mapPhotographers(myCurrentLocation!!.latitude, myCurrentLocation!!.longitude, 10)
            apiManager.makeApiCall(listCall!!, this)
        }
    }

    private fun initializeMap() {
        if (googleMap == null) {
            val mapFrag = SupportMapFragment.newInstance()
            mapFrag.getMapAsync {
                it.setOnInfoWindowClickListener {
                    showInfoDialog(markerHashMap[it]!!)
                }
                googleMap = it
                getAndSetCurrentLocation()
                getList()
            }
            childFragmentManager.beginTransaction().replace(R.id.map, mapFrag).commit()
        }
    }

    private fun showInfoDialog(data: UserData) {
        val bldr = AlertDialog.Builder(baseActivity)
        bldr.setTitle("Contact")
        bldr.setMessage("Connect to ${data.name}")
        bldr.setPositiveButton("Call") { _, _ ->
            val call = Uri.parse("tel:${data.mobileNumber}")
            val callIntent = Intent(Intent.ACTION_DIAL, call)
            baseActivity.startActivity(Intent.createChooser(callIntent, "Call with"))
        }
        bldr.create().show()
    }

    private fun getAndSetCurrentLocation() {
        if (myCurrentLocation != null && googleMap != null) {
            val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(myCurrentLocation!!.latitude, myCurrentLocation!!.longitude)).zoom(15f).build()
            googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        if (listCall != null && listCall === call) {
            googleMap?.clear()

            val jsonObject = payload as JsonObject
            val actualData = jsonObject.getAsJsonArray("photographers")
            val objectType = object : TypeToken<ArrayList<UserData>>() {}.type
            val list = Gson().fromJson<ArrayList<UserData>>(actualData, objectType)

            val circle = googleMap!!.addCircle(CircleOptions()
                    .center(LatLng(myCurrentLocation!!.latitude, myCurrentLocation!!.longitude))
                    .radius(10 * 1000.toDouble())
                    .fillColor(R.color.colorPrimaryTrans))

            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(circle.center, getZoomLevel(circle).toFloat()))

            if (list.size > 0)
                setupMapUI(list)
            emptyTV.visibility = if (list.size == 0) View.VISIBLE else View.GONE
        }
    }

    private fun setupMapUI(list: ArrayList<UserData>) {
        markerHashMap.clear()
        for (data in list) {
            val view = View.inflate(baseActivity, R.layout.inc_marker_icon, null)

            val tgt = GlideTgt(data, view)
            GlideApp.with(baseActivity).load(Const.IMAGE_BASE_URL + "/${data.avatar}")
                    .circleCrop().listener(tgt).into(view.findViewById(R.id.markerIV))
        }
    }

    inner class GlideTgt(val data: UserData, val view: View) : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            val imageView = view.findViewById<ImageView>(R.id.markerIV)
            imageView.setImageBitmap(resource!!.toBitmap())
            val marker = googleMap!!.addMarker(MarkerOptions()
                    .position(LatLng(data.photoProfile!!.lat, data.photoProfile!!.lng))
                    .draggable(false)
                    .title(data.name)
                    .snippet(data.photoProfile!!.expertise.toUpperCase())
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(baseActivity, view))))

            markerHashMap.put(marker, data)
            return true
        }
    }

    fun onGPSEnable() {
        locationManager.onGPSEnable()
    }

    fun onGPSEnableDenied() {
        showToast("Please check GPS is ON and working fine!", true)
        goBack = true
    }

    private fun getZoomLevel(circle: Circle?): Int {
        var zoomLevel = 11
        if (circle != null) {
            val radius = circle.radius + circle.radius / 2
            val scale = radius / 500
            zoomLevel = (16 - Math.log(scale) / Math.log(2.0)).toInt()
        }
        return zoomLevel
    }
}
