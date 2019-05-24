package com.ideal.vox.data

import com.google.gson.annotations.SerializedName

enum class DayType {
    @SerializedName("day available")
    AVAILABLE,
    @SerializedName("day not available")
    UNAVAILABLE,
    @SerializedName("day booked")
    BOOKED
}