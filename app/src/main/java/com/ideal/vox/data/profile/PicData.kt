package com.ideal.vox.data.profile

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PicData(
        @SerializedName("album_id") var albumId: Int = 0,
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        var bitmapPath:String? = null
):Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(albumId)
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(bitmapPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PicData> {
        override fun createFromParcel(parcel: Parcel): PicData {
            return PicData(parcel)
        }

        override fun newArray(size: Int): Array<PicData?> {
            return arrayOfNulls(size)
        }
    }
}