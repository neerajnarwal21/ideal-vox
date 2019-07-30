package com.ideal.vox.data.profile

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class BankData(
        @SerializedName("account_name") var accountName: String = "",
        @SerializedName("account_number") var accountNumber: String = "",
        @SerializedName("id") var id: Int = 0,
        @SerializedName("ifsc_code") var ifscCode: String = "",
        @SerializedName("passbook_pic") var passbookPic: String = "",
        @SerializedName("user_id") var userId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountName)
        parcel.writeString(accountNumber)
        parcel.writeInt(id)
        parcel.writeString(ifscCode)
        parcel.writeString(passbookPic)
        parcel.writeInt(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BankData> {
        override fun createFromParcel(parcel: Parcel): BankData {
            return BankData(parcel)
        }

        override fun newArray(size: Int): Array<BankData?> {
            return arrayOfNulls(size)
        }
    }

}