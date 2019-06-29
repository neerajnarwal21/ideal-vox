package com.ideal.vox.fragment.home

import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.ideal.vox.R
import com.ideal.vox.activity.main.MainActivity
import com.ideal.vox.data.CityData
import com.ideal.vox.data.UserData
import com.ideal.vox.di.GlideApp
import com.ideal.vox.fragment.BaseFragment
import com.ideal.vox.fragment.home.detail.UserDetailFragment
import com.ideal.vox.utils.*
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fg_h_map_view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import java.util.*


/**
 * Created by neeraj.narwal on 16/1/17.
 */

class HomeMapFragment : BaseFragment(), LocationManager.LocationUpdates {

    private var googleMap: GoogleMap? = null
    private var myCurrentLocation: Location? = null
    private var locationManager = LocationManager()
    private var goBack: Boolean = false
    private var vieww: View? = null
    private var listCall: Call<JsonObject>? = null
    private var searchCityCall: Call<JsonObject>? = null
    private var cityCall: Call<JsonObject>? = null
    private val markerHashMap = HashMap<Marker, UserData>()

    private var isCitiesShowing = false
    private var behavior: TopSheetBehavior<NestedScrollView>? = null

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
        if (goBack) baseActivity.onBackPressed()
    }

    private fun initUI() {
        if (myCurrentLocation == null) {
            locationManager.startLocationManager(baseActivity, LocationManager.Accuracy.HIGH, this)
        }

        topSheetNSV.visibility = View.VISIBLE
        cityCall = apiInterface.getCities()
        apiManager.makeApiCall(cityCall!!, this, false)

        behavior = TopSheetBehavior.from(topSheetNSV)
        searchTV.setOnClickListener {
            TransitionManager.beginDelayedTransition(parentRL)
            if (behavior?.state == TopSheetBehavior.STATE_COLLAPSED) {
                behavior?.state = TopSheetBehavior.STATE_EXPANDED
            } else {
                behavior?.state = TopSheetBehavior.STATE_COLLAPSED
            }
            behavior?.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == TopSheetBehavior.STATE_COLLAPSED) {
                        if (!isCitiesShowing) distanceCV.visibility = View.VISIBLE
                        searchTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0)
                    } else if (newState == TopSheetBehavior.STATE_EXPANDED) {
                        distanceCV.visibility = View.GONE
                        searchTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_collapse, 0)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?) {
                }

            })
        }
        doneIV.setOnClickListener {
            val city = RequestBody.create(MediaType.parse("text/plain"), citySP.selectedItem.toString())
            searchCityCall = apiInterface.citywisePhotographers(city)
            apiManager.makeApiCall(searchCityCall!!, this)
        }
    }

    private fun getList() {
        log("LatLng>>> ${myCurrentLocation?.latitude}, ${myCurrentLocation?.longitude}")
        if (myCurrentLocation != null) {
            apiClient.clearCache()
            listCall = apiInterface.mapPhotographers(myCurrentLocation!!.latitude, myCurrentLocation!!.longitude, distanceSB.progress)
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
                it.uiSettings.isMapToolbarEnabled = false
                googleMap = it
                getAndSetCurrentLocation()

                distanceSB.onSeekChangeListener = object : OnSeekChangeListener {
                    override fun onSeeking(seekParams: SeekParams?) {
                    }

                    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                        if (seekBar != null && seekBar.progress < 10) distanceSB.setProgress(10.0f)
                        getList()
                    }
                }
                getList()
            }
            childFragmentManager.beginTransaction().replace(R.id.map, mapFrag).commit()
        }
    }

    private fun showInfoDialog(data: UserData) {
        (activity as MainActivity).userData = data
        baseActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fc_home, UserDetailFragment())
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
                    .center(LatLng(myCurrentLocation!!.latitude, myCurrentLocation!!.longitude))
                    .radius(distanceSB.progress * 1000.toDouble())
                    .fillColor(R.color.colorPrimaryTrans))

            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(circle.center, getZoomLevel(circle).toFloat()))

            if (list.size > 0)
                setupMapUI(list)
            emptyTV.visibility = if (list.size == 0) View.VISIBLE else View.GONE
        } else if (searchCityCall != null && searchCityCall === call) {
            googleMap?.clear()
            isCitiesShowing = true
            behavior?.state = TopSheetBehavior.STATE_COLLAPSED

            val jsonObject = payload as JsonArray
            val objectType = object : TypeToken<ArrayList<UserData>>() {}.type
            val list = Gson().fromJson<ArrayList<UserData>>(jsonObject, objectType)

            if (list.size > 0) {
                setupMapUI(list)

                if (list.size == 1) {
                    googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(list[0].photoProfile!!.lat, list[0].photoProfile!!.lng), 15.0f))
                } else {
                    val builder = LatLngBounds.Builder()
                    for (data in list) {
                        builder.include(LatLng(data.photoProfile!!.lat, data.photoProfile!!.lng))
                    }
                    googleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 40))
                }
            }

            emptyTV.visibility = if (list.size == 0) View.VISIBLE else View.GONE

        } else if (cityCall != null && cityCall === call) {
            val jsonArr = payload as JsonArray
            val objectType = object : TypeToken<ArrayList<CityData>>() {}.type
            val datas = Gson().fromJson<ArrayList<CityData>>(jsonArr, objectType)
            val stateMap = TreeMap<String, ArrayList<String>>(Comparator { t1, t2 -> t1.compareTo(t2) })
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
