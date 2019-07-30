package com.ideal.vox.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PagerPicData(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var url: String? = null
):Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PagerPicData> {
        override fun createFromParcel(parcel: Parcel): PagerPicData {
            return PagerPicData(parcel)
        }

        override fun newArray(size: Int): Array<PagerPicData?> {
            return arrayOfNulls(size)
        }
    }
}