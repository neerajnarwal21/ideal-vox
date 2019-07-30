package com.ideal.vox.data.profile

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AlbumData(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("user_id") var userId: Int = 0,
        @SerializedName("pictures") var pictures: ArrayList<PicData> = ArrayList()

) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.createTypedArrayList(PicData))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(userId)
        parcel.writeTypedList(pictures)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumData> {
        override fun createFromParcel(parcel: Parcel): AlbumData {
            return AlbumData(parcel)
        }

        override fun newArray(size: Int): Array<AlbumData?> {
            return arrayOfNulls(size)
        }
    }

}