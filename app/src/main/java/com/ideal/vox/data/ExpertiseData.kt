package com.ideal.vox.data

import com.google.gson.annotations.SerializedName

data class ExpertiseData(
        @SerializedName("id") var id: Int,
        @SerializedName("name") var name: String
)