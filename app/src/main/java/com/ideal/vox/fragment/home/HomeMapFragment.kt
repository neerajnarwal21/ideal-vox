package com.ideal.vox.fragment.home

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.data.UserData
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.fragment.home.detail.UserDetailFragment
import com.ideal.vox.utils.CircleTransform
import com.ideal.vox.utils.Const
import com.ideal.vox.utils.LocationManager
import com.ideal.vox.utils.createDrawableFromView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fg_h_map_view.*
import retrofit2.Call


/**
 * Created by neeraj.narwal on 16/1/17.
 */

class HomeMapFragment : BaseFragment(), LocationManager.LocationUpdates {

    private var googleMap: GoogleMap? = null
    private var myCurrentLocation: Location? = null
    private var locationManager = LocationManager()
    private var goBack: Boolean = false
    private var vieww: View? = null
    private var latlng: LatLng? = null
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
        setToolbar("Map View")
        log("View created >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        initUI()
    }

    override fun onResume() {
        super.onResume()
        if (goBack)
            baseActivity.onBackPressed()

    }

    private fun initUI() {
        if (myCurrentLocation == null) {
            locationManager.startLocationManager(baseActivity, LocationManager.Accuracy.HIGH, this)
        }
    }

    private fun setVisibility(isSearchUI: Boolean) {
        searchPinIV.visibility = if (isSearchUI) View.GONE else View.VISIBLE
        pin.visibility = if (isSearchUI) View.VISIBLE else View.GONE
        distanceCV.visibility = if (isSearchUI) View.GONE else View.VISIBLE
        pinCV.visibility = if (isSearchUI) View.VISIBLE else View.GONE
    }

    fun getList() {
        log("LatLng>>> ${latlng?.latitude}, ${latlng?.longitude}")
        if (latlng != null) {
            apiClient.clearCache()
            listCall = apiInterface.mapPhotographers(latlng!!.latitude, latlng!!.longitude, distanceSB.progress)
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

                latlng = googleMap?.cameraPosition?.target
                distanceSB.onSeekChangeListener = object : OnSeekChangeListener {
                    override fun onSeeking(seekParams: SeekParams?) {
                    }

                    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                        getList()
                    }
                }
                searchPinIV.setOnClickListener {
                    setVisibility(true)
                    googleMap!!.clear()
                }
                doneBT.setOnClickListener {
                    latlng = googleMap!!.cameraPosition.target
                    getList()
                    setVisibility(false)
                }
                getList()
            }
            childFragmentManager.beginTransaction().replace(R.id.map, mapFrag).commit()
        }
    }

    private fun showInfoDialog(data: UserData) {
        (activity as MainActivity).userData = data
        val bndl = Bundle()
        bndl.putParcelable("data", data)
        val frag = UserDetailFragment()
        frag.arguments = bndl
        baseActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fc_home, frag)
                .addToBackStack(null)
                .commit()
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
                    .center(googleMap!!.cameraPosition.target)
                    .radius(distanceSB.progress * 1000.toDouble())
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
            baseActivity.picasso.load(Const.IMAGE_BASE_URL + "/${data.avatar}")
                    .transform(CircleTransform())
                    .into(MyTarget(data, view))
        }
    }

    inner class MyTarget(val data: UserData, val view: View) : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            val imageView = view.findViewById<ImageView>(R.id.markerIV)
            imageView.setImageBitmap(bitmap)
            val marker = googleMap!!.addMarker(MarkerOptions()
                    .position(LatLng(data.photoProfile!!.lat, data.photoProfile!!.lng))
                    .draggable(false)
                    .title(data.name)
                    .snippet(data.photoProfile!!.expertise)
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(baseActivity, view))))

            markerHashMap.put(marker, data)
        }
    }


    fun onGPSEnable() {
        locationManager.onGPSEnable()
    }

    fun onGPSEnableDenied() {
        showToast("Please check GPS is ON and working fine!", true)
        goBack = true
    }

    fun getZoomLevel(circle: Circle?): Int {
        var zoomLevel = 11
        if (circle != null) {
            val radius = circle.radius + circle.radius / 2
            val scale = radius / 500
            zoomLevel = (16 - Math.log(scale) / Math.log(2.0)).toInt()
        }
        return zoomLevel
    }
}
