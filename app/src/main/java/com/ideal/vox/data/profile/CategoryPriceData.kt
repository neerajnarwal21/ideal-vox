package com.ideal.vox.data.profile

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CategoryPriceData(
        @SerializedName("category") var category: String? = null,
        @SerializedName("id") var id: Int? = 0,
        @SerializedName("price") var price: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeValue(id)
        parcel.writeString(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryPriceData> {
        override fun createFromParcel(parcel: Parcel): CategoryPriceData {
            return CategoryPriceData(parcel)
        }

        override fun newArray(size: Int): Array<CategoryPriceData?> {
            return arrayOfNulls(size)
        }
    }

}