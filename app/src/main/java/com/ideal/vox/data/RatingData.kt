package com.ideal.vox.data

import com.google.gson.annotations.SerializedName

data class RatingData(
        @SerializedName("by_user") var byUser: String?,
        @SerializedName("id") var id: Int?,
        @SerializedName("rating") var rating: Float = 0.0f,
        @SerializedName("reviews") var reviews: String?,
        @SerializedName("user_id") var userId: String?,
        @SerializedName("review_by") var reviewUser: UserData? = null
)