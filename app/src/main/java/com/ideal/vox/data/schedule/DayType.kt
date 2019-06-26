package com.ideal.vox.data.schedule

import com.google.gson.annotations.SerializedName

enum class DayType {
    //    'day available','day not available','day pending','day confirmed','day booked','day reject'
    @SerializedName("day available")
    AVAILABLE,
    @SerializedName("day not available")
    UNAVAILABLE,
    @SerializedName("day pending")
    PENDING,
    @SerializedName("day confirmed")
    CONFIRMED,
    @SerializedName("day booked")
    BOOKED,
    @SerializedName("day reject")
    REJECT
}