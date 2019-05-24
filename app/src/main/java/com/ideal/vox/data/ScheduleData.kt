package com.ideal.vox.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ScheduleData(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("day") var day: DayType,
        @SerializedName("night") var night: NightType,
        @SerializedName("schedule_date") var scheduleDate: String = "",
        @SerializedName("user_id") var userId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            DayType.values()[parcel.readInt()],
            NightType.values()[parcel.readInt()],
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(day.ordinal)
        parcel.writeInt(night.ordinal)
        parcel.writeString(scheduleDate)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleData> {
        override fun createFromParcel(parcel: Parcel): ScheduleData {
            return ScheduleData(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleData?> {
            return arrayOfNulls(size)
        }
    }

}