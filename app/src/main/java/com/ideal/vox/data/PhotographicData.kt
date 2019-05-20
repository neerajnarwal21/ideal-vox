package com.ideal.vox.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PhotographicData(
        @SerializedName("address") var address: String = "",
        @SerializedName("pin") var pinCode: String = "",
        @SerializedName("dob") var dob: String = "",
        @SerializedName("experience_in_months") var experienceInMonths: Int = 0,
        @SerializedName("experience_in_year") var experienceInYear: Int = 0,
        @SerializedName("expertise") var expertise: String = "",
        @SerializedName("gender") var gender: String = "",
        @SerializedName("lat") var lat: Double = 0.0,
        @SerializedName("lng") var lng: Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(pinCode)
        parcel.writeString(dob)
        parcel.writeInt(experienceInMonths)
        parcel.writeInt(experienceInYear)
        parcel.writeString(expertise)
        parcel.writeString(gender)
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
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