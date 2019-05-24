package com.ideal.vox.data

import com.google.gson.annotations.SerializedName

enum class NightType {
    @SerializedName("night available")
    AVAILABLE,
    @SerializedName("night not available")
    UNAVAILABLE,
    @SerializedName("night booked")
    BOOKED
}