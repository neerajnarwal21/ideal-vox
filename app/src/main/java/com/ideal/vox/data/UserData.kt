package com.ideal.vox.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UserData(
        @SerializedName("email") var email: String = "",
        @SerializedName("avatar") var avatar: String = "",
        @SerializedName("id") var id: Int = 0,
        @SerializedName("mobile_number") var mobileNumber: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("is_active") var is_active: Int = 0,
        @SerializedName("role") var userType: UserType,
        @SerializedName("photo_graphic_profile") var photoProfile: PhotographicData
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            UserType.values()[parcel.readInt()],
            parcel.readParcelable(PhotographicData::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(avatar)
        parcel.writeInt(id)
        parcel.writeString(mobileNumber)
        parcel.writeString(name)
        parcel.writeInt(is_active)
        parcel.writeInt(userType.ordinal)
        parcel.writeParcelable(photoProfile, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}