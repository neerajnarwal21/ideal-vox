package com.ideal.vox.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AccessoryData(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("model") var model: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("picture") var picture: String = "",
        @SerializedName("user_id") var userId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(model)
        parcel.writeString(name)
        parcel.writeString(picture)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccessoryData> {
        override fun createFromParcel(parcel: Parcel): AccessoryData {
            return AccessoryData(parcel)
        }

        override fun newArray(size: Int): Array<AccessoryData?> {
            return arrayOfNulls(size)
        }
    }

}