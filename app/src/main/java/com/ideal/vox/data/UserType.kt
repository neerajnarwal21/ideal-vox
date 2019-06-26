package com.ideal.vox.data

import com.google.gson.annotations.SerializedName

enum class UserType {
    @SerializedName("user")
    USER,
    @SerializedName("photographer")
    PHOTOGRAPHER,
    @SerializedName("helper")
    HELPER
}