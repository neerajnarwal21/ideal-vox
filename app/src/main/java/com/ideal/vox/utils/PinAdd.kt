package com.ideal.vox.utils

import com.google.android.gms.maps.model.LatLng

object PinAdd {

    private var listener: ((address: String?, latLng: LatLng) -> Unit)? = null

    fun setListener(listener: ((address: String?, latLng: LatLng) -> Unit)) {
        this.listener = listener
    }

    fun sendBackLocation(address: String?, latLng: LatLng) {
        listener?.invoke(address, latLng)
    }
}
