package com.ideal.vox.data

import com.google.gson.annotations.SerializedName

data class CityData(
        @SerializedName("city") var city: String?,
        @SerializedName("id") var id: Int?,
        @SerializedName("state") var state: String?
)