package com.ideal.vox.data.schedule

import com.google.gson.annotations.SerializedName

enum class NightType {
    //    'night available','night not available','night pending','night confirmed','night booked','night reject'
    @SerializedName("night available")
    AVAILABLE,
    @SerializedName("night not available")
    UNAVAILABLE,
    @SerializedName("night pending")
    PENDING,
    @SerializedName("night confirmed")
    CONFIRMED,
    @SerializedName("night booked")
    BOOKED,
    @SerializedName("night reject")
    REJECT
}