package com.ideal.vox.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PhotographicData(
        @SerializedName("address") var address: String = "",
        @SerializedName("dob") var dob: String = "",
        @SerializedName("experience_in_months") var experienceInMonths: Int = 0,
        @SerializedName("experience_in_year") var experienceInYear: Int = 0,
        @SerializedName("expertise") var expertise: String = "",
        @SerializedName("gender") var gender: String = "",
        @SerializedName("id") var id: Int = 0,
        @SerializedName("lat") var lat: Double = 0.0,
        @SerializedName("lng") var lng: Double = 0.0,
        @SerializedName("user_id") var userId: String = ""
):Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(dob)
        parcel.writeInt(experienceInMonths)
        parcel.writeInt(experienceInYear)
        parcel.writeString(expertise)
        parcel.writeString(gender)
        parcel.writeInt(id)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotographicData> {
        override fun createFromParcel(parcel: Parcel): PhotographicData {
            return PhotographicData(parcel)
        }

        override fun newArray(size: Int): Array<PhotographicData?> {
            return arrayOfNulls(size)
        }
    }
}