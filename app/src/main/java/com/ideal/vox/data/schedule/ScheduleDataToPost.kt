package com.ideal.vox.data.schedule

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ScheduleDataToPost(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("day") var day: DayType = DayType.AVAILABLE,
        @SerializedName("night") var night: NightType = NightType.AVAILABLE,
        @SerializedName("schedule_date") var scheduleDate: String = "",
        @SerializedName("user_id") var userId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            DayType.values()[parcel.readInt()],
            NightType.values()[parcel.readInt()],
            parcel.readString(),
            parcel.readString())

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

    companion object CREATOR : Parcelable.Creator<ScheduleDataToPost> {
        override fun createFromParcel(parcel: Parcel): ScheduleDataToPost {
            return ScheduleDataToPost(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleDataToPost?> {
            return arrayOfNulls(size)
        }
    }

}