package com.ideal.vox.data.schedule

import android.os.Parcel
import android.os.Parcelable

data class FilterData(
        var category: String = "",
        var priceDisable: Boolean = false,
        var min: Int = 500,
        var max: Int = 50000
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeByte(if (priceDisable) 1 else 0)
        parcel.writeInt(min)
        parcel.writeInt(max)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterData> {
        override fun createFromParcel(parcel: Parcel): FilterData {
            return FilterData(parcel)
        }

        override fun newArray(size: Int): Array<FilterData?> {
            return arrayOfNulls(size)
        }
    }
}
